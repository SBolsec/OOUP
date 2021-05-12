package hr.ooup.lab3.command;

import hr.ooup.lab3.model.Location;
import hr.ooup.lab3.model.TextEditorModel;

import java.util.List;

public class DeleteAfterAction implements EditAction {

    private TextEditorModel model;
    private Character c;
    private Location cursorLocation;

    public DeleteAfterAction(TextEditorModel model) {
        this.model = model;
        this.cursorLocation = model.getCursorLocation();
    }

    @Override
    public void executeDo() {
        List<String> lines = model.getLines();
        String line = lines.get(cursorLocation.getY());

        if (cursorLocation.getX() == line.length() && cursorLocation.getY() == lines.size()-1) {
            c = null;
            return;
        }

        if (cursorLocation.getX() == line.length()) {
            this.c = '\n';
            lines.set(cursorLocation.getY(), line + lines.get(cursorLocation.getY()+1));
            lines.remove(cursorLocation.getY()+1);
        } else {
            this.c = line.charAt(cursorLocation.getX());
            String newLine = line.substring(0, cursorLocation.getX()) + line.substring(cursorLocation.getX()+1);
            lines.set(cursorLocation.getY(), newLine);
        }

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
        } else {
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()) + c + line.substring(cursorLocation.getX()));
        }

        model.notifyTextObservers();
    }
}
