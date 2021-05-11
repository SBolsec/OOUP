package hr.ooup.lab3;

import hr.ooup.lab3.clipboard.CliboardStack;
import hr.ooup.lab3.model.Location;
import hr.ooup.lab3.model.LocationRange;
import hr.ooup.lab3.model.TextEditorModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;

public class TextEditor extends JFrame {

    private TextEditorModel model;
    private TextEditorComponent editorComponent;
    private CliboardStack clipboard;

    public TextEditor() {
        model = new TextEditorModel("Neki tekst\nProba\nNeki tekst");
        clipboard = new CliboardStack();

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
                        if (e.isControlDown() && e.getKeyCode() != KeyEvent.VK_CONTROL) {
                            if (e.isShiftDown() && e.getKeyCode() != KeyEvent.VK_SHIFT) {
                                if (e.getKeyCode() == KeyEvent.VK_V) {
                                    if (!clipboard.isEmpty())
                                        model.insert(clipboard.pop());
                                }
                                return;
                            }
                            switch (e.getKeyCode()) {
                                case KeyEvent.VK_C -> {
                                    String selectedText = getSelectedText();
                                    if (selectedText != null) {
                                        clipboard.push(selectedText);
                                    }
                                }
                                case KeyEvent.VK_X -> {
                                    String selectedText = getSelectedText();
                                    if (selectedText != null) {
                                        clipboard.push(selectedText);
                                        model.deleteRange(model.getSelectionRange());
                                    }
                                }
                                case KeyEvent.VK_V -> {
                                    if (!clipboard.isEmpty())
                                        model.insert(clipboard.peek());
                                }
                            }
                            return;
                        }

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

    private String getSelectedText() {
        LocationRange selectionRange = model.getSelectionRange();
        if (selectionRange != null) {
            if (selectionRange.getStart().compareTo(selectionRange.getEnd()) == 1) {
                selectionRange = new LocationRange(selectionRange.getEnd(), selectionRange.getStart());
            }
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = model.linesRange(selectionRange.getStart().getY(), selectionRange.getEnd().getY()+1);
            for (int i = selectionRange.getStart().getY(); it.hasNext(); i++) {
                String line = it.next();
                if (i == selectionRange.getStart().getY()) {
                    if (i == selectionRange.getEnd().getY()) {
                        sb.append(line.substring(selectionRange.getStart().getX(), selectionRange.getEnd().getX()));
                    } else {
                        sb.append(line.substring(selectionRange.getStart().getX()));
                    }
                } else if (i == selectionRange.getEnd().getY()) {
                    sb.append(line.substring(0, selectionRange.getEnd().getX()));
                } else {
                    sb.append(line);
                }
                if (selectionRange.getStart().getY() != selectionRange.getEnd().getY() && i != selectionRange.getEnd().getY()) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TextEditor().setVisible(true));
    }
}
