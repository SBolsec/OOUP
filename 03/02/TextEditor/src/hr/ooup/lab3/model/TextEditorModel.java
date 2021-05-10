package hr.ooup.lab3.model;

import hr.ooup.lab3.iterator.ListIterator;
import hr.ooup.lab3.iterator.ListRangeIterator;
import hr.ooup.lab3.observer.CursorObserver;
import hr.ooup.lab3.observer.TextObserver;

import java.util.*;

public class TextEditorModel {
    private List<String> lines;
    private LocationRange selectionRange;
    private Location cursorLocation = new Location(0,0);

    private List<CursorObserver> cursorObservers = new ArrayList<>();
    private List<TextObserver> textObservers = new ArrayList<>();

    public TextEditorModel(String startingText) {
        lines = new ArrayList<>(Arrays.asList(startingText.split("\n")));
    }

    public Location getCursorLocation() {
        return cursorLocation;
    }

    public void setCursorLocation(Location cursorLocation) {
        this.cursorLocation = cursorLocation;
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

    private void notifyCursorObservers() {
        for (CursorObserver o : cursorObservers) {
            o.updateCursorLocation(cursorLocation);
        }
    }

    // CURSOR MANIPULATION
    public void moveCursorLeft() {
        if (cursorLocation.getX() - 1 >= 0) {
            cursorLocation.setX(cursorLocation.getX() - 1);
            notifyCursorObservers();
            return;
        }
        if (cursorLocation.getY() - 1 >= 0) {
            cursorLocation.setX(lines.get(cursorLocation.getY() - 1).length());
            cursorLocation.setY(cursorLocation.getY() - 1);
            notifyCursorObservers();
        }
    }

    public void moveCursorRight() {
        if (cursorLocation.getX() + 1 <= lines.get(cursorLocation.getY()).length()) {
            cursorLocation.setX(cursorLocation.getX() + 1);
            notifyCursorObservers();
            return;
        }
        if (cursorLocation.getY() + 1 < lines.size()) {
            cursorLocation.setX(0);
            cursorLocation.setY(cursorLocation.getY() + 1);
            notifyCursorObservers();
        }
    }

    public void moveCursorUp() {
        if (cursorLocation.getY() != 0) {
            cursorLocation.setX(Math.min(cursorLocation.getX(), lines.get(cursorLocation.getY() - 1).length()));
            cursorLocation.setY(cursorLocation.getY() - 1);
            notifyCursorObservers();
        }
    }

    public void moveCursorDown() {
        if (cursorLocation.getY() != lines.size() - 1) {
            cursorLocation.setX(Math.min(cursorLocation.getX(), lines.get(cursorLocation.getY() + 1).length()));
            cursorLocation.setY(cursorLocation.getY() + 1);
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

    private void notifyTextObservers() {
        for (TextObserver o : textObservers) {
            o.updateText();
        }
    }

    // TEXT MANIPULATIONS
    public void deleteBefore() {
        if (cursorLocation.getY() == 0 && cursorLocation.getX() == 0) return;
        if (cursorLocation.getX() == 0) {
            cursorLocation.setX(lines.get(cursorLocation.getY()-1).length());
            lines.set(cursorLocation.getY()-1, lines.get(cursorLocation.getY()-1) + lines.get(cursorLocation.getY()));
            lines.remove(cursorLocation.getY());
            cursorLocation.setY(cursorLocation.getY()-1);

            notifyTextObservers();
            notifyCursorObservers();
            return;
        }

        String line = lines.get(cursorLocation.getY());
        String newLine = line.substring(0, cursorLocation.getX()-1) + line.substring(cursorLocation.getX());
        lines.set(cursorLocation.getY(), newLine);
        cursorLocation.setX(cursorLocation.getX()-1);

        notifyTextObservers();
        notifyCursorObservers();
    }

    public void deleteAfter() {
        if (cursorLocation.getX() == lines.get(cursorLocation.getY()).length() && cursorLocation.getY() == lines.size()-1) return;
        if (cursorLocation.getX() == lines.get(cursorLocation.getY()).length()) {
            lines.set(cursorLocation.getY(), lines.get(cursorLocation.getY()) + lines.get(cursorLocation.getY()+1));
            lines.remove(cursorLocation.getY()+1);
            notifyTextObservers();
            return;
        }

        String line = lines.get(cursorLocation.getY());
        String newLine = line.substring(0, cursorLocation.getX()) + line.substring(cursorLocation.getX()+1);
        lines.set(cursorLocation.getY(), newLine);

        notifyTextObservers();
    }

    public void deleteRange(LocationRange range) {
        if (range == null) return;
        if (range.getStart().getY() != range.getEnd().getY()) {
            lines.set(range.getStart().getY(), lines.get(range.getStart().getY()).substring(0, range.getStart().getX()));
            lines.set(range.getEnd().getY(), lines.get(range.getEnd().getY()).substring(range.getEnd().getX()));
            if (lines.get(range.getEnd().getY()).isBlank())
                lines.remove(range.getEnd().getY());
            for (int i = range.getStart().getY() + 1; i < range.getEnd().getY(); i++) {
                lines.remove(range.getStart().getY()+1);
            }
            if (lines.get(range.getStart().getY()).isBlank())
                lines.remove(range.getStart().getY());
        } else {
            lines.set(range.getStart().getY(), lines.get(range.getStart().getY()).substring(0, range.getStart().getX()) + lines.get(range.getStart().getY()).substring(range.getEnd().getX()));
        }

        cursorLocation = range.getStart();
        selectionRange = null;
        notifyTextObservers();
    }

    public LocationRange getSelectionRange() {
        return selectionRange;
    }

    public void setSelectionRange(LocationRange range) {
        selectionRange = range;
    }

    public void insert(char c) {
        String line = lines.get(cursorLocation.getY());
        if (c == 10) { // new line
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()));
            lines.add(cursorLocation.getY()+1, line.substring(cursorLocation.getX()));
            cursorLocation.setX(0);
            cursorLocation.setY(cursorLocation.getY()+1);
        } else {
            String newLine = line.substring(0, cursorLocation.getX()) + c + line.substring(cursorLocation.getX());
            lines.set(cursorLocation.getY(), newLine);
            cursorLocation.setX(cursorLocation.getX()+1);
        }
        notifyCursorObservers();
        notifyTextObservers();
    }

    public void insert(String text) {
        String line = lines.get(cursorLocation.getY());
        if (text.contains("\n")) {
            String[] toAdd = text.split("\n");
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()) + toAdd[0]);
            int i;
            for (i = 1; i < toAdd.length - 1; i++) {
                lines.add(cursorLocation.getY() + i, toAdd[i]);
            }
            lines.add(cursorLocation.getY() + i, toAdd[i] + line.substring(cursorLocation.getX()));
            cursorLocation.setX(toAdd[i].length());
            cursorLocation.setY(cursorLocation.getY()+i);
        } else {
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()) + text + line.substring(cursorLocation.getX()));
            cursorLocation.setX(cursorLocation.getX() + text.length());
        }
        notifyCursorObservers();
        notifyTextObservers();
    }

    // MANIPULATING THE SELECTION RANGE
    public void addSelectionUp() {
        if (cursorLocation.getY() == 0) return;

        Location end = null;
        if (selectionRange == null) {
            end = new Location(cursorLocation.getX(), cursorLocation.getY());
        }

        Location newLocation = new Location(Math.min(cursorLocation.getX(), lines.get(cursorLocation.getY()-1).length()), cursorLocation.getY()-1);

        if (end == null) {
            if (selectionRange.getStart().getY() < newLocation.getY()) {
                selectionRange.setEnd(newLocation);
            } else if (selectionRange.getStart().getY() > newLocation.getY()) {
                selectionRange.setStart(newLocation);
            } else {
                if (selectionRange.getStart().getX() < newLocation.getX()) {
                    selectionRange.setEnd(newLocation);
                } else if (selectionRange.getStart().getX() > newLocation.getX()) {
                    selectionRange.setStart(newLocation);
                } else {
                    selectionRange = null;
                }
            }
        } else {
            selectionRange = new LocationRange(newLocation, end);
        }
        cursorLocation = newLocation;
        notifyCursorObservers();
    }

    public void addSelectionDown() {
        if (cursorLocation.getY() == lines.size()-1) return;

        Location start = null;
        if (selectionRange == null) {
            start = new Location(cursorLocation.getX(), cursorLocation.getY());
        }

        Location newLocation = new Location(Math.min(cursorLocation.getX(), lines.get(cursorLocation.getY()+1).length()), cursorLocation.getY()+1);

        if (start == null) {
            if (selectionRange.getEnd().getY() > newLocation.getY()) {
                selectionRange.setStart(newLocation);
            } else if (selectionRange.getEnd().getY() < newLocation.getY()) {
                selectionRange.setEnd(newLocation);
            } else {
                if (selectionRange.getEnd().getX() < newLocation.getX()) {
                    selectionRange.setEnd(newLocation);
                } else if (selectionRange.getEnd().getX() > newLocation.getX()) {
                    selectionRange.setStart(newLocation);
                } else {
                    selectionRange = null;
                }
            }
        } else {
            selectionRange = new LocationRange(start, newLocation);
        }
        cursorLocation = newLocation;
        notifyCursorObservers();
    }

    public void addSelectionLeft() {
        Location end = null;
        if (selectionRange == null) {
            end = new Location(cursorLocation.getX(), cursorLocation.getY());
        }

        Location newLocation = null;
        if (cursorLocation.getX() - 1 >= 0) {
            newLocation = new Location(cursorLocation.getX() - 1, cursorLocation.getY());
        } else if (cursorLocation.getY() - 1 >= 0) {
            newLocation = new Location(lines.get(cursorLocation.getY() - 1).length(), cursorLocation.getY() - 1);
        } else {
            return;
        }

        if (end == null) {
            if (selectionRange.getStart().getY() < newLocation.getY()) {
                selectionRange.setEnd(newLocation);
            } else if (selectionRange.getStart().getY() > newLocation.getY()) {
                selectionRange.setStart(newLocation);
            } else {
                if (selectionRange.getStart().getX() < newLocation.getX()) {
                    selectionRange.setEnd(newLocation);
                } else if (selectionRange.getStart().getX() > newLocation.getX()) {
                    selectionRange.setStart(newLocation);
                } else {
                    selectionRange = null;
                }
            }
        } else {
            selectionRange = new LocationRange(newLocation, end);
        }
        cursorLocation = newLocation;
        notifyCursorObservers();
    }

    public void addSelectionRight() {
        Location start = null;
        if (selectionRange == null) {
            start = new Location(cursorLocation.getX(), cursorLocation.getY());
        }

        Location newLocation = null;
        if (cursorLocation.getX() + 1 <= lines.get(cursorLocation.getY()).length()) {
            newLocation = new Location(cursorLocation.getX() + 1, cursorLocation.getY());
        } else if (cursorLocation.getY() + 1 < lines.size()) {
            newLocation = new Location(0, cursorLocation.getY() + 1);
        } else {
            return;
        }

        if (start == null) {
            if (selectionRange.getEnd().getY() > newLocation.getY()) {
                selectionRange.setStart(newLocation);
            } else if (selectionRange.getEnd().getY() < newLocation.getY()) {
                selectionRange.setEnd(newLocation);
            } else {
                if (selectionRange.getEnd().getX() < newLocation.getX()) {
                    selectionRange.setEnd(newLocation);
                } else if (selectionRange.getEnd().getX() > newLocation.getX()) {
                    selectionRange.setStart(newLocation);
                } else {
                    selectionRange = null;
                }
            }
        } else {
            selectionRange = new LocationRange(start, newLocation);
        }
        cursorLocation = newLocation;
        notifyCursorObservers();
    }
}
