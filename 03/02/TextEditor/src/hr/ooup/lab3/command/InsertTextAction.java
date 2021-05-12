package hr.ooup.lab3.command;

import hr.ooup.lab3.model.Location;
import hr.ooup.lab3.model.TextEditorModel;

import java.util.List;

public class InsertTextAction implements EditAction {

    private TextEditorModel model;
    private String text;
    private Location cursorLocation;

    public InsertTextAction(TextEditorModel model, String text) {
        this.model = model;
        this.text = text;
        this.cursorLocation = model.getCursorLocation();
    }

    @Override
    public void executeDo() {
        List<String> lines = model.getLines();

        if (lines.size() == 0) {
            for (String s : text.split("\n")) {
                lines.add(s);
            }
            model.setLines(lines);
            model.notifyCursorObservers();
            model.notifyTextObservers();
            return;
        }

        String line = lines.get(cursorLocation.getY());
        if (text.contains("\n")) {
            String[] toAdd = text.split("\n");
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()) + toAdd[0]);
            int i;
            for (i = 1; i < toAdd.length - 1; i++) {
                lines.add(cursorLocation.getY() + i, toAdd[i]);
            }
            lines.add(cursorLocation.getY() + i, toAdd[i] + line.substring(cursorLocation.getX()));
            model.setCursorLocation(new Location(toAdd[i].length(), cursorLocation.getY()+i));
        } else {
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()) + text + line.substring(cursorLocation.getX()));
            model.setCursorLocation(new Location(cursorLocation.getX() + text.length(), cursorLocation.getY()));
        }
        model.notifyCursorObservers();
        model.notifyTextObservers();
    }

    @Override
    public void executeUndo() {
        List<String> lines = model.getLines();

        if (!text.contains("\n")) {
            String line = lines.get(cursorLocation.getY());
            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()) + line.substring(cursorLocation.getX()+text.length()));
        } else {
            String line = lines.get(cursorLocation.getY());

            lines.set(cursorLocation.getY(), line.substring(0, cursorLocation.getX()));
            int charsToSkip = text.replace("\n", "").length() - line.substring(cursorLocation.getX()).length();
            for (int i = cursorLocation.getY()+1; charsToSkip > 0; i++) {
                int len = lines.get(cursorLocation.getY()+1).length();
                charsToSkip -= len;
                if (charsToSkip < 0) {
                    String lastLine = lines.get(cursorLocation.getY()+1).substring(len + charsToSkip);
                    lines.set(cursorLocation.getY(), lines.get(cursorLocation.getY()) + lastLine);
                }
                lines.remove(cursorLocation.getY()+1);
            }
        }
        model.setCursorLocation(cursorLocation);
        model.notifyCursorObservers();
        model.notifyTextObservers();
    }
}
