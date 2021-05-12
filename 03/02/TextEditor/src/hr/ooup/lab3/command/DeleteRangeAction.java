package hr.ooup.lab3.command;

import hr.ooup.lab3.model.Location;
import hr.ooup.lab3.model.LocationRange;
import hr.ooup.lab3.model.TextEditorModel;

import java.util.ArrayList;
import java.util.List;

public class DeleteRangeAction implements EditAction {

    private TextEditorModel model;
    private LocationRange range;
    private List<String> deletedText;
    private Location cursor;

    public DeleteRangeAction(TextEditorModel model, LocationRange range) {
        this.model = model;
        this.range = range;
        if (range.getStart().compareTo(range.getEnd()) == 1) {
            this.range = new LocationRange(range.getEnd(), range.getStart());
        }
        this.cursor = model.getCursorLocation();
    }

    @Override
    public void executeDo() {
        if (range == null) return;
        deletedText = new ArrayList<>();

        List<String> lines = model.getLines();
        if (range.getStart().getY() != range.getEnd().getY()) {
            String last = lines.get(range.getEnd().getY()).substring(0, range.getEnd().getX());
            deletedText.add(lines.get(range.getStart().getY()).substring(range.getStart().getX()));
            lines.set(range.getStart().getY(), lines.get(range.getStart().getY()).substring(0, range.getStart().getX()) + lines.get(range.getEnd().getY()).substring(range.getEnd().getX()));
            for (int i = range.getStart().getY() + 1; i <= range.getEnd().getY(); i++) {
                if (i != range.getEnd().getY())
                    deletedText.add(lines.get(range.getStart().getY()+1));
                lines.remove(range.getStart().getY()+1);
            }
            if (lines.get(range.getStart().getY()).isBlank()) {
                lines.remove(range.getStart().getY());
            }
            deletedText.add(last);
        } else {
            deletedText.add(lines.get(range.getStart().getY()).substring(range.getStart().getX(), range.getEnd().getX()));
            lines.set(range.getStart().getY(), lines.get(range.getStart().getY()).substring(0, range.getStart().getX()) + lines.get(range.getStart().getY()).substring(range.getEnd().getX()));
        }

        model.setCursorLocation(range.getStart());
        model.setSelectionRange(null);

        model.notifyTextObservers();
        model.notifyCursorObservers();
    }

    @Override
    public void executeUndo() {
        if (range == null || deletedText.size() == 0) return;

        List<String> lines = model.getLines();

        if (range.getStart().getY() == range.getEnd().getY()) {
            int y = range.getStart().getY();
            lines.set(y, lines.get(y).substring(0, range.getStart().getX()) + deletedText.get(0) + lines.get(y).substring(range.getStart().getX()));
        } else if (lines.isEmpty()) {
            deletedText.forEach(l -> lines.add(l));
        } else {
            int i = 0;
            String firstLine = lines.get(range.getStart().getY());
            for (String line : deletedText) {
                int y = range.getStart().getY()+i;

                if (y == range.getStart().getY()) {
                    lines.set(y, firstLine.substring(0, range.getStart().getX()) + line);
                } else if (y == range.getEnd().getY()) {
                    lines.add(y, line + firstLine.substring(range.getStart().getX()));
                } else {
                    lines.add(y, line);
                }
                i++;
            }
        }

        model.setCursorLocation(cursor);

        model.notifyTextObservers();
        model.notifyCursorObservers();
    }
}
