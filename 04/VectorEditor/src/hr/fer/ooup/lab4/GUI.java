package hr.fer.ooup.lab4;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.model.GraphicalObject;
import hr.fer.ooup.lab4.model.LineSegment;
import hr.fer.ooup.lab4.model.Oval;
import hr.fer.ooup.lab4.renderer.G2DRendererImpl;
import hr.fer.ooup.lab4.renderer.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {

    private List<GraphicalObject> objects;
    private DocumentModel documentModel;
    private Canvas canvas;


    public GUI(List<GraphicalObject> objects) {
        this.objects = objects;
        this.documentModel = new DocumentModel();

        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setTitle("VectorEditor");

        addComponents();
        addTolbar();
    }

    private void addComponents() {
        canvas = new Canvas(documentModel);
        add(canvas, BorderLayout.CENTER);

    }

    private void addTolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(true);

        for (GraphicalObject go : objects) {
            Action action = new CanvasAction(go);
            action.putValue(Action.NAME, go.getShapeName());
            toolBar.add(action);
        }

        add(toolBar, BorderLayout.PAGE_START);
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
            }
            requestFocusInWindow();
        }
    }

    private class CanvasAction extends AbstractAction {

        private GraphicalObject go;

        public CanvasAction(GraphicalObject go) {
            this.go = go;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

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
