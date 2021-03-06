package hr.ooup.lab3.model;

import hr.ooup.lab3.command.*;
import hr.ooup.lab3.iterator.ListIterator;
import hr.ooup.lab3.iterator.ListRangeIterator;
import hr.ooup.lab3.observer.CursorObserver;
import hr.ooup.lab3.observer.TextObserver;

import java.util.*;

public class TextEditorModel {
    private List<String> lines;
    private LocationRange selectionRange;
    private Location cursorLocation;
    private List<CursorObserver> cursorObservers = new ArrayList<>();
    private List<TextObserver> textObservers = new ArrayList<>();
    private UndoManager undoManager = UndoManager.getInstance();

    public TextEditorModel(String startingText) {
        lines = new ArrayList<>(Arrays.asList(startingText.split("\n")));
        selectionRange = null;
        cursorLocation = new Location(0,0);
        notifyCursorObservers();
        notifyTextObservers();
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
        this.cursorLocation = new Location(0,0);
        notifyTextObservers();
        notifyCursorObservers();
    }

    public Location getCursorLocation() {
        return cursorLocation;
    }

    public void setCursorLocation(Location cursorLocation) {
        this.cursorLocation = cursorLocation;
        notifyCursorObservers();
    }

    // ITERATORS
    public Iterator<String> allLines() {
        return new ListIterator<>(lines);
    }

    public Iterator<String> linesRange(int index1, int index2) {
        return new ListRangeIterator<>(new ListIterator<>(lines), index1, index2);
    }

    // CURSOR OBSERVERS
    public void attachCursorObserver(CursorObserver observer) {
        cursorObservers.add(observer);
    }

    public void dettachCursorObserver(CursorObserver observer) {
        cursorObservers.remove(observer);
    }

    public void notifyCursorObservers() {
        for (CursorObserver o : cursorObservers) {
            o.updateCursorLocation(cursorLocation);
        }
    }

    // CURSOR MANIPULATION
    public void moveCursorLeft() {
        if (cursorLocation.getX() - 1 >= 0) {
            cursorLocation = new Location(cursorLocation.getX() - 1, cursorLocation.getY());
            notifyCursorObservers();
        } else if (cursorLocation.getY() - 1 >= 0) {
            cursorLocation = new Location(lines.get(cursorLocation.getY() - 1).length(), cursorLocation.getY() - 1);
            notifyCursorObservers();
        }
    }

    public void moveCursorRight() {
        if (cursorLocation.getX() + 1 <= lines.get(cursorLocation.getY()).length()) {
            cursorLocation = new Location(cursorLocation.getX() + 1, cursorLocation.getY());
            notifyCursorObservers();
        } else if (cursorLocation.getY() + 1 < lines.size()) {
            cursorLocation = new Location(0, cursorLocation.getY() + 1);
            notifyCursorObservers();
        }
    }

    public void moveCursorUp() {
        if (cursorLocation.getY() != 0) {
            int x = Math.min(cursorLocation.getX(), lines.get(cursorLocation.getY() - 1).length());
            int y = cursorLocation.getY() - 1;
            cursorLocation = new Location(x, y);
        } else {
            cursorLocation = new Location(0, 0);
        }
        notifyCursorObservers();
    }

    public void moveCursorDown() {
        if (cursorLocation.getY() != lines.size() - 1) {
            int x = Math.min(cursorLocation.getX(), lines.get(cursorLocation.getY() + 1).length());
            int y = cursorLocation.getY() + 1;
            cursorLocation = new Location(x, y);
            notifyCursorObservers();
        } else {
            cursorLocation = new Location(lines.get(cursorLocation.getY()).length(), lines.size()-1);
            notifyCursorObservers();
        }
    }

    // TEXT OBSERVERS
    public void attachTextObserver(TextObserver observer) {
        textObservers.add(observer);
    }

    public void dettachTextObserver(TextObserver observer) {
        textObservers.remove(observer);
    }

    public void notifyTextObservers() {
        for (TextObserver o : textObservers) {
            o.updateText();
        }
    }

    // TEXT MANIPULATIONS
    public void deleteBefore() {
        EditAction action = new DeleteBeforeAction(this);
        action.executeDo();
        undoManager.push(action);
    }

    public void deleteAfter() {
        EditAction action = new DeleteAfterAction(this);
        action.executeDo();
        undoManager.push(action);
    }

    public void deleteRange(LocationRange range) {
        EditAction action = new DeleteRangeAction(this, range);
        action.executeDo();
        undoManager.push(action);
    }

    public LocationRange getSelectionRange() {
        return selectionRange;
    }

    public void setSelectionRange(LocationRange range) {
        if (range != null && range.getStart().equals(range.getEnd())) {
            selectionRange = null;
        } else {
            selectionRange = range;
        }
        notifyCursorObservers();
    }

    public void insert(char c) {
        EditAction action = new InsertCharacterAction(this, c);
        action.executeDo();
        undoManager.push(action);
    }

    public void insert(String text) {
        EditAction action = new InsertTextAction(this, text);
        action.executeDo();
        undoManager.push(action);
    }
}
