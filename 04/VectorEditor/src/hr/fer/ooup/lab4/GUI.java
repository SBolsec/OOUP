package hr.fer.ooup.lab4;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.listeners.DocumentModelListener;
import hr.fer.ooup.lab4.model.CompositeShape;
import hr.fer.ooup.lab4.model.GraphicalObject;
import hr.fer.ooup.lab4.model.LineSegment;
import hr.fer.ooup.lab4.model.Oval;
import hr.fer.ooup.lab4.renderer.G2DRendererImpl;
import hr.fer.ooup.lab4.renderer.Renderer;
import hr.fer.ooup.lab4.renderer.SVGRendererImpl;
import hr.fer.ooup.lab4.state.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class GUI extends JFrame {

    private static final State IDLE_STATE = new IdleState();

    private List<GraphicalObject> objects;
    private DocumentModel documentModel;
    private Canvas canvas;
    private State currentState;
    private Map<String, GraphicalObject> prototypes;

    public GUI(List<GraphicalObject> objects) {
        this.objects = objects;
        this.documentModel = new DocumentModel();
        this.prototypes = new HashMap<>();
        this.currentState = IDLE_STATE;

        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setTitle("VectorEditor");

        addComponents();
        addToolbar();
        addListeners();
    }

    private void addComponents() {
        canvas = new Canvas(documentModel);
        add(canvas, BorderLayout.CENTER);

        // add prototypes
        for (GraphicalObject go : objects) {
            prototypes.put(go.getShapeID(), go);
        }
        GraphicalObject composite = new CompositeShape();
        prototypes.put(composite.getShapeID(), composite);
    }

    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(true);

        loadAction.putValue(Action.NAME, "Učitaj");
        toolBar.add(loadAction);

        saveAction.putValue(Action.NAME, "Pohrani");
        toolBar.add(saveAction);

        svgExportAction.putValue(Action.NAME, "SVG export");
        toolBar.add(svgExportAction);

        for (GraphicalObject go : objects) {
            Action action = new CanvasAction(go);
            action.putValue(Action.NAME, go.getShapeName());
            toolBar.add(action);
        }

        selectShapeAction.putValue(Action.NAME, "Selektiraj");
        toolBar.add(selectShapeAction);

        eraserAction.putValue(Action.NAME, "Brisalo");
        toolBar.add(eraserAction);

        add(toolBar, BorderLayout.PAGE_START);
    }

    private void addListeners() {
        documentModel.addDocumentModelListener(new DocumentModelListener() {
            @Override
            public void documentChange() {
                canvas.repaint();
            }
        });

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    currentState.onLeaving();
                    currentState = IDLE_STATE;
                } else {
                    currentState.keyPressed(e.getKeyCode());
                }
            }
        });

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentState.mouseDown(new Point(e.getX(), e.getY()), e.isShiftDown(), e.isControlDown());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentState.mouseUp(new Point(e.getX(), e.getY()), e.isShiftDown(), e.isControlDown());
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currentState.mouseDragged(new Point(e.getX(), e.getY()));
            }
        });
    }

    private Action selectShapeAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentState.onLeaving();
            currentState = new SelectShapeState(documentModel);
        }
    };

    private Action eraserAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentState.onLeaving();
            currentState = new EraserState(documentModel);
        }
    };

    private Action svgExportAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("SVG export");
            if (fc.showSaveDialog(GUI.this) != JFileChooser.APPROVE_OPTION) return;
            String filePath = fc.getSelectedFile().getPath();
            if (!filePath.endsWith(".svg")) {
                filePath += ".svg";
            }

            SVGRendererImpl r = new SVGRendererImpl(filePath);
            for (GraphicalObject go : documentModel.list()) {
                go.render(r);
            }
            try {
                r.close();
                JOptionPane.showMessageDialog(
                        GUI.this,
                        "Export uspješan!",
                        "INFO",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(
                        GUI.this,
                        "Export neuspješan!",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    private Action saveAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Pohrani");
            if (fc.showSaveDialog(GUI.this) != JFileChooser.APPROVE_OPTION) return;
            String filePath = fc.getSelectedFile().getPath();

            List<String> rows = new ArrayList<>();
            for (GraphicalObject go : documentModel.list()) {
                go.save(rows);
            }
            try {
                FileWriter fw = new FileWriter(filePath);
                for (String row : rows) {
                    fw.write(row+"\n");
                }
                fw.flush();
                fw.close();
                JOptionPane.showMessageDialog(
                        GUI.this,
                        "Pohrana uspješna!",
                        "INFO",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        GUI.this,
                        "Pohrana neuspješna!",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    private Action loadAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Učitaj");
            if (fc.showSaveDialog(GUI.this) != JFileChooser.APPROVE_OPTION) return;
            String filePath = fc.getSelectedFile().getPath();

            try {
                List<String> lines = Files.readAllLines(Path.of(filePath));
                Stack<GraphicalObject> stack = new Stack<>();
                for (String line : lines) {
                    if (line.isBlank()) continue;
                    if (!line.startsWith("@")) continue;
                    int separator = line.indexOf(" ");
                    GraphicalObject go = prototypes.get(line.substring(0, separator));
                    go.load(stack, line.substring(separator+1));
                }
                documentModel.clear();
                for (GraphicalObject go : stack) {
                    documentModel.addGraphicalObject(go);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        GUI.this,
                        "Učitavanje neuspješno!",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    private class CanvasAction extends AbstractAction {

        private GraphicalObject go;

        public CanvasAction(GraphicalObject go) {
            this.go = go;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            currentState.onLeaving();
            currentState = new AddShapeState(documentModel, go);
        }
    }

    private class Canvas extends JComponent {

        private DocumentModel documentModel;

        public Canvas(DocumentModel documentModel) {
            this.documentModel = documentModel;
            setFocusable(true);
            requestFocusInWindow();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            Renderer r = new G2DRendererImpl(g2d);
            for (GraphicalObject go : this.documentModel.list()) {
                go.render(r);
                currentState.afterDraw(r, go);
            }
            currentState.afterDraw(r);
            requestFocusInWindow();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<GraphicalObject> objects = new ArrayList<>();

            objects.add(new LineSegment());
            objects.add(new Oval());

            GUI gui = new GUI(objects);
            gui.setVisible(true);
        });
    }
}
