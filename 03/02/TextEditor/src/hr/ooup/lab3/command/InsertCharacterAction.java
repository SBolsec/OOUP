package hr.ooup.lab3.command;

import hr.ooup.lab3.model.Location;
import hr.ooup.lab3.model.TextEditorModel;

import java.util.List;

public class InsertCharacterAction implements EditAction {

    private TextEditorModel model;
    private char c;
    private Location cursorLocation;

    public InsertCharacterAction(TextEditorModel model, char c) {
        this.model = model;
        this.c = c;
        this.cursorLocation = model.getCursorLocation();
    }

    @Override
    public void executeDo() {
        List<String> lines = model.getLines();
        if (lines.size() == 0) {
            lines.add(Character.toString(c));
            return;
        }
        String line = lines.get(cursorLocation.getY());
        if (c == 10) { // new line
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()));
            lines.add(cursorLocation.getY()+1, line.substring(cursorLocation.getX()));
            model.setCursorLocation(new Location(0, cursorLocation.getY()+1));
        } else {
            String newLine = line.substring(0, cursorLocation.getX()) + c + line.substring(cursorLocation.getX());
            lines.set(cursorLocation.getY(), newLine);
            model.setCursorLocation(new Location(cursorLocation.getX()+1, cursorLocation.getY()));
        }
    }

    @Override
    public void executeUndo() {
        List<String> lines = model.getLines();
        if (c == '\n') {
            lines.set(cursorLocation.getY(), lines.get(cursorLocation.getY()) + lines.get(cursorLocation.getY()+1));
            lines.remove(cursorLocation.getY()+1);
        } else {
            String line = lines.get(cursorLocation.getY());
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()) + line.substring(cursorLocation.getX()+1));
        }
        model.setCursorLocation(cursorLocation);
        model.notifyCursorObservers();
        model.notifyTextObservers();
    }
}
