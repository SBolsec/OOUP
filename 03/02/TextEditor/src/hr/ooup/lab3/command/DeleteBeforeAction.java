package hr.ooup.lab3.command;

import hr.ooup.lab3.model.Location;
import hr.ooup.lab3.model.TextEditorModel;

import java.util.List;

public class DeleteBeforeAction implements EditAction {

    private TextEditorModel model;
    private Character c;
    private Location cursorLocation;

    public DeleteBeforeAction(TextEditorModel model) {
        this.model = model;
        this.cursorLocation = model.getCursorLocation();
    }

    @Override
    public void executeDo() {
        List<String> lines = model.getLines();
        if (cursorLocation.getY() == 0 && cursorLocation.getX() == 0) {
            c = null;
            return;
        };
        if (cursorLocation.getX() == 0) {
            this.c = '\n';
            lines.set(cursorLocation.getY()-1, lines.get(cursorLocation.getY()-1) + lines.get(cursorLocation.getY()));
            int len = lines.get(cursorLocation.getY()).length();
            lines.remove(cursorLocation.getY());
            Location newLoc = new Location(lines.get(cursorLocation.getY()-1).length() - len, cursorLocation.getY()-1);
            model.setCursorLocation(newLoc);
            this.cursorLocation = newLoc;
        } else {
            String line = lines.get(cursorLocation.getY());
            this.c = line.charAt(cursorLocation.getX()-1);
            String newLine = line.substring(0, cursorLocation.getX()-1) + line.substring(cursorLocation.getX());
            lines.set(cursorLocation.getY(), newLine);
            Location newLoc = new Location(cursorLocation.getX()-1, cursorLocation.getY());
            model.setCursorLocation(newLoc);
        }

        model.notifyCursorObservers();
        model.notifyTextObservers();
    }

    @Override
    public void executeUndo() {
        if (c == null) return;

        List<String> lines = model.getLines();
        String line = lines.get(cursorLocation.getY());
        if (c == '\n') {
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()));
            lines.add(cursorLocation.getY()+1, line.substring(cursorLocation.getX()));
            model.setCursorLocation(new Location(0, cursorLocation.getY()+1));
        } else {
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()-1) + c + line.substring(cursorLocation.getX()-1));
            model.setCursorLocation(cursorLocation);
        }

        model.notifyCursorObservers();
        model.notifyTextObservers();
    }
}
