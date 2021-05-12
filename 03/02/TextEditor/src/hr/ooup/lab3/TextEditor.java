package hr.ooup.lab3;

import hr.ooup.lab3.clipboard.CliboardStack;
import hr.ooup.lab3.command.UndoManager;
import hr.ooup.lab3.model.Location;
import hr.ooup.lab3.model.LocationRange;
import hr.ooup.lab3.model.TextEditorModel;
import hr.ooup.lab3.observer.CursorObserver;
import hr.ooup.lab3.observer.StackObserver;
import hr.ooup.lab3.observer.TextObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class TextEditor extends JFrame {

    private TextEditorModel model;
    private TextEditorComponent editorComponent;
    private CliboardStack clipboard;
    private UndoManager undoManager;
    private JPanel statusBar;
    private JLabel statusCursor;
    private JLabel statusLines;

    public TextEditor() {
        model = new TextEditorModel("Neki tekst\nProba\nNeki tekst");
        clipboard = new CliboardStack();
        undoManager = UndoManager.getInstance();

        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setTitle("Text Editor");
        setLocationRelativeTo(null);
        setFocusTraversalKeysEnabled(false);
        getContentPane().setLayout(new BorderLayout());



        initTextEditorComponent();
        initActions();
        initMenus();
        initToolbar();
        initStatusBar();
        initObservers();
    }

    private void initTextEditorComponent() {
        editorComponent = new TextEditorComponent(model);
        JScrollPane scrollPane = new JScrollPane(editorComponent);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        editorComponent.requestFocus();
        editorComponent.requestFocusInWindow();

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

    private Action openDocumentAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Open file");
            if (fc.showOpenDialog(TextEditor.this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File fileName = fc.getSelectedFile();
            Path filePath = fileName.toPath();
            if (!Files.isReadable(filePath)) {
                String[] options = new String[] {"OK"};
                String message = String.format("%s %s %s",
                        "File",
                        fileName.getAbsolutePath(),
                        "does not exist"
                );
                JOptionPane.showOptionDialog(
                        TextEditor.this, message,
                        "Error", JOptionPane.OK_OPTION,
                        JOptionPane.ERROR_MESSAGE, null, options, options[0]);

                JOptionPane.showMessageDialog(TextEditor.this,
                        "File " + fileName.getAbsolutePath() + " doesn't exist!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                model.setLines(Files.readAllLines(filePath));
            } catch (IOException exc) {
                String[] options = new String[] {"OK"};
                String message = String.format("%s %s.", "Error reading file", fileName.getAbsolutePath());
                JOptionPane.showOptionDialog(
                        TextEditor.this, message,
                        "Error", JOptionPane.OK_OPTION,
                        JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                return;
            }
        }
    };

    private Action saveDocumentAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Path path = null;
                JFileChooser jfc = new JFileChooser();
                jfc.setDialogTitle("Save document as");
                if (jfc.showSaveDialog(TextEditor.this) != JFileChooser.APPROVE_OPTION) {
                    String[] options = new String[] {"OK"};
                    JOptionPane.showOptionDialog(
                            TextEditor.this, "Nothing was recorded.",
                            "Warning", JOptionPane.OK_OPTION,
                            JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                    return;
                }
            path = jfc.getSelectedFile().toPath();

            if (Files.exists(path)) {
                String[] options = new String[] { "Yes", "No", "Cancel" };
                String message = "File already exists! Do you want to override it?";
                String title = "Warning!";

                int result = JOptionPane.showOptionDialog(TextEditor.this, message, title, JOptionPane.DEFAULT_OPTION,
                        JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                switch (result) {
                    case JOptionPane.CLOSED_OPTION:
                        return;
                    case 0:
                        break;
                    case 1:
                        return;
                    case 2:
                        return;
                }
            }

            try {
                byte[] data = model.getLines().stream().collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8);
                Files.write(path, data);
            } catch (IOException e1) {
                String[] options = new String[] {"OK"};
                String message = String.format("%s %s.\n%s",
                        "Error writing file",
                        path.toFile().getAbsolutePath(),
                        "Attention: it is not clear in what condition the files on the disk are!"
                );
                JOptionPane.showOptionDialog(
                        TextEditor.this, message,
                        "Info", JOptionPane.OK_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                return;
            }

            String[] options = new String[] {"OK"};
            JOptionPane.showOptionDialog(
                    TextEditor.this, "File saved.",
                    "Info", JOptionPane.OK_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        }
    };

    private Action exitAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    };

    private Action undoAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private Action redoAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private Action cutAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private Action copyAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private Action pasteAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private Action pasteAndTakeAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private Action deleteSelectionAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private Action clearDocumentAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            model.setLines(new ArrayList<>());
        }
    };

    private Action cursorToDocumentStartAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            model.setCursorLocation(new Location(0,0));
        }
    };

    private Action cursorToDocumentEndAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Iterator<String> it = model.allLines();
            String line = null;
            int i;
            for (i = 0; it.hasNext(); i++) {
                line = it.next();
            }
            model.setCursorLocation(new Location(line.length(), i-1));
        }
    };

    private void initActions() {
        openDocumentAction.putValue(Action.NAME, "Open");
        openDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Open a file from the disk.");
        openDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));

        saveDocumentAction.putValue(Action.NAME, "Save");
        saveDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Save current document to disk.");
        saveDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));

        exitAction.putValue(Action.NAME, "Exit");
        exitAction.putValue(Action.SHORT_DESCRIPTION, "Exit the program.");
        exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt F4"));
        //
        undoAction.putValue(Action.NAME, "Undo");
        undoAction.putValue(Action.SHORT_DESCRIPTION, "Undo previous action.");
        undoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));

        redoAction.putValue(Action.NAME, "Redo");
        redoAction.putValue(Action.SHORT_DESCRIPTION, "Redo previous action.");
        redoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Y"));

        cutAction.putValue(Action.NAME, "Cut");
        cutAction.putValue(Action.SHORT_DESCRIPTION, "Cut selected text.");
        cutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));

        copyAction.putValue(Action.NAME, "Copy");
        copyAction.putValue(Action.SHORT_DESCRIPTION, "Copy selected text.");
        copyAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));

        pasteAction.putValue(Action.NAME, "Paste");
        pasteAction.putValue(Action.SHORT_DESCRIPTION, "Paste text from clipboard.");
        pasteAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));

        pasteAndTakeAction.putValue(Action.NAME, "Paste and Take");
        pasteAndTakeAction.putValue(Action.SHORT_DESCRIPTION, "Paste text from clipboard and remove it from clipboard.");
        pasteAndTakeAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift V"));

        deleteSelectionAction.putValue(Action.NAME, "Delete selection");
        deleteSelectionAction.putValue(Action.SHORT_DESCRIPTION, "Delete selected text.");
        deleteSelectionAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control D"));

        clearDocumentAction.putValue(Action.NAME, "Clear document");
        clearDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Clear the document.");
        clearDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control R"));

        cursorToDocumentStartAction.putValue(Action.NAME, "Cursor to document start");
        cursorToDocumentStartAction.putValue(Action.SHORT_DESCRIPTION, "Move cursor to document start.");
        cursorToDocumentStartAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control G"));

        cursorToDocumentEndAction.putValue(Action.NAME, "Cursor to document end");
        cursorToDocumentEndAction.putValue(Action.SHORT_DESCRIPTION, "Move cursor to document end.");
        cursorToDocumentEndAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift G"));

        // disable at start
        undoAction.setEnabled(false);
        redoAction.setEnabled(false);
        cutAction.setEnabled(false);
        copyAction.setEnabled(false);
        pasteAction.setEnabled(false);
        pasteAndTakeAction.setEnabled(false);
        deleteSelectionAction.setEnabled(false);
    }

    private void initMenus() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        fileMenu.add(new JMenuItem(openDocumentAction));
        fileMenu.add(new JMenuItem(saveDocumentAction));
        fileMenu.add(new JMenuItem(exitAction));

        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        editMenu.add(new JMenuItem(undoAction));
        editMenu.add(new JMenuItem(redoAction));
        editMenu.add(new JMenuItem(cutAction));
        editMenu.add(new JMenuItem(copyAction));
        editMenu.add(new JMenuItem(pasteAction));
        editMenu.add(new JMenuItem(pasteAndTakeAction));
        editMenu.add(new JMenuItem(deleteSelectionAction));
        editMenu.add(new JMenuItem(clearDocumentAction));

        JMenu moveMenu = new JMenu("Move");
        menuBar.add(moveMenu);

        moveMenu.add(new JMenuItem(cursorToDocumentStartAction));
        moveMenu.add(new JMenuItem(cursorToDocumentEndAction));

        this.setJMenuBar(menuBar);
    }

    private void initToolbar() {
        JToolBar toolBar = new JToolBar("Actions");
        toolBar.setFloatable(true);

        toolBar.add(undoAction);
        toolBar.add(redoAction);
        toolBar.add(cutAction);
        toolBar.add(copyAction);
        toolBar.add(pasteAction);

        this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
    }

    private void initStatusBar() {
        statusBar = new JPanel(new GridLayout(1, 2));
        statusBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        Location cursor = model.getCursorLocation();
        statusCursor = new JLabel("Ln " + (cursor.getY()+1) + ", Col " + (cursor.getX()+1));
        Iterator<String> it = model.allLines();
        int i;
        for (i = 0; it.hasNext(); i++) {
            it.next();
        }
        statusLines = new JLabel("Lines in document: " + i);

        statusBar.add(statusCursor);
        statusBar.add(statusLines);
        add(statusBar, BorderLayout.PAGE_END);
    }

    private void initObservers() {
        clipboard.attachClipboardObserver(() -> {
            if (clipboard.isEmpty()) {
                pasteAction.setEnabled(false);
                pasteAndTakeAction.setEnabled(false);
            } else {
                pasteAction.setEnabled(true);
                pasteAndTakeAction.setEnabled(true);
            }
        });

        model.attachCursorObserver((location) -> {
            statusCursor.setText("Ln " + (location.getY()+1) + ", Col " + (location.getX()+1));

            if (model.getSelectionRange() == null) {
                cutAction.setEnabled(false);
                copyAction.setEnabled(false);
                deleteSelectionAction.setEnabled(false);
            } else {
                cutAction.setEnabled(true);
                copyAction.setEnabled(true);
                deleteSelectionAction.setEnabled(true);
            }
        });

        model.attachTextObserver(() -> {
            Iterator<String> it = model.allLines();
            int i;
            for (i = 0; it.hasNext(); i++) {
                it.next();
            }
            statusLines.setText("Lines in document: " + i);
        });

        undoManager.attachUndoStackObserver(new StackObserver() {
            @Override
            public void empty() {
                undoAction.setEnabled(false);
            }

            @Override
            public void hasElements() {
                undoAction.setEnabled(true);
            }
        });

        undoManager.attachRedoStackObserver(new StackObserver() {
            @Override
            public void empty() {
                redoAction.setEnabled(false);
            }

            @Override
            public void hasElements() {
                redoAction.setEnabled(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TextEditor().setVisible(true));
    }
}
