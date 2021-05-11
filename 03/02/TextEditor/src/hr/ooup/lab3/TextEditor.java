package hr.ooup.lab3;

import hr.ooup.lab3.model.Location;
import hr.ooup.lab3.model.LocationRange;
import hr.ooup.lab3.model.TextEditorModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TextEditor extends JFrame {

    private TextEditorModel model;
    private TextEditorComponent editorComponent;

    public TextEditor() {
        model = new TextEditorModel("Neki tekst\nProba\nNeki tekst");

        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setTitle("Text Editor");
        setLocationRelativeTo(null);
        setFocusTraversalKeysEnabled(false);

        initTextEditorComponent();
    }

    private void initTextEditorComponent() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        editorComponent = new TextEditorComponent(model);
        JScrollPane scrollPane = new JScrollPane(editorComponent);
        cp.add(scrollPane, BorderLayout.CENTER);

        editorComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> {
                        if (e.isShiftDown()) {
                            Location cursor = model.getCursorLocation();
                            model.moveCursorUp();
                            if (model.getSelectionRange() == null) model.setSelectionRange(new LocationRange(cursor, model.getCursorLocation()));
                            else model.setSelectionRange(new LocationRange(model.getSelectionRange().getStart(), model.getCursorLocation()));
                        }
                        else {
                            model.setSelectionRange(null);
                            model.moveCursorUp();
                        }
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (e.isShiftDown()) {
                            Location cursor = model.getCursorLocation();
                            model.moveCursorDown();
                            if (model.getSelectionRange() == null) model.setSelectionRange(new LocationRange(cursor, model.getCursorLocation()));
                            else model.setSelectionRange(new LocationRange(model.getSelectionRange().getStart(), model.getCursorLocation()));
                        }
                        else {
                            model.setSelectionRange(null);
                            model.moveCursorDown();
                        }
                    }
                    case KeyEvent.VK_LEFT -> {
                        if (e.isShiftDown()) {
                            Location cursor = model.getCursorLocation();
                            model.moveCursorLeft();
                            if (model.getSelectionRange() == null) model.setSelectionRange(new LocationRange(cursor, model.getCursorLocation()));
                            else model.setSelectionRange(new LocationRange(model.getSelectionRange().getStart(), model.getCursorLocation()));
                        }
                        else {
                            model.setSelectionRange(null);
                            model.moveCursorLeft();
                        }
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (e.isShiftDown()) {
                            Location cursor = model.getCursorLocation();
                            model.moveCursorRight();
                            if (model.getSelectionRange() == null) model.setSelectionRange(new LocationRange(cursor, model.getCursorLocation()));
                            else model.setSelectionRange(new LocationRange(model.getSelectionRange().getStart(), model.getCursorLocation()));
                        }
                        else {
                            model.setSelectionRange(null);
                            model.moveCursorRight();
                        }
                    }
                    case KeyEvent.VK_BACK_SPACE -> {
                        if (model.getSelectionRange() == null) model.deleteBefore();
                        else model.deleteRange(model.getSelectionRange());
                    }
                    case KeyEvent.VK_DELETE -> {
                        if (model.getSelectionRange() == null) model.deleteAfter();
                        else model.deleteRange(model.getSelectionRange());
                    }
                    default -> {
                        if(!e.isActionKey() && !e.isMetaDown() && e.getKeyCode() != KeyEvent.VK_SHIFT && e.getKeyCode() != KeyEvent.VK_ALT
                                && e.getKeyCode() != KeyEvent.VK_ALT_GRAPH && !e.isControlDown() && e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                            if (model.getSelectionRange() != null)
                                model.deleteRange(model.getSelectionRange());
                            model.insert(e.getKeyChar());
                        }
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TextEditor().setVisible(true));
    }
}
