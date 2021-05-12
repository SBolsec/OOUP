package hr.ooup.lab3.command;

import hr.ooup.lab3.observer.StackObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class UndoManager {
    private static UndoManager instance;

    private Stack<EditAction> undoStack = new Stack<>();
    private Stack<EditAction> redoStack = new Stack<>();

    private List<StackObserver> undoStackObservers = new ArrayList<>();
    private List<StackObserver> redoStackObservers = new ArrayList<>();

    private UndoManager() {
        super();
    };

    public static UndoManager getInstance() {
        if (instance == null) {
            instance = new UndoManager();
        }
        return instance;
    }

    public void undo() {
        if (undoStack.isEmpty()) return;
        EditAction action = undoStack.pop();
        redoStack.push(action);
        action.executeUndo();
    }

    public void redo() {
        if (redoStack.isEmpty()) return;
        EditAction action = redoStack.pop();
        undoStack.push(action);
        action.executeDo();
    }

    public void push(EditAction c) {
        redoStack.clear();
        undoStack.push(c);
    }

    public void attachUndoStackObserver(StackObserver observer) {
        undoStackObservers.add(observer);
    }

    public void dettachUndoStackObserver(StackObserver observer) {
        undoStackObservers.remove(observer);
    }

    private void notifyUndoStackObservers() {
        if (redoStack.isEmpty()) {
            for (StackObserver o : undoStackObservers)
                o.empty();
        } else {
            for (StackObserver o : undoStackObservers)
                o.hasElements();
        }
    }

    public void attachRedoStackObserver(StackObserver observer) {
        redoStackObservers.add(observer);
    }

    public void dettachRedoStackObserver(StackObserver observer) {
        redoStackObservers.remove(observer);
    }

    private void notifyRedoStackObservers() {
        if (redoStack.isEmpty()) {
            for (StackObserver o : redoStackObservers)
                o.empty();
        } else {
            for (StackObserver o : redoStackObservers)
                o.hasElements();
        }
    }


}
