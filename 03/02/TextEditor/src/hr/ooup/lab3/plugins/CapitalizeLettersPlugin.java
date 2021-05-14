package hr.ooup.lab3.plugins;

import hr.ooup.lab3.clipboard.CliboardStack;
import hr.ooup.lab3.command.EditAction;
import hr.ooup.lab3.command.UndoManager;
import hr.ooup.lab3.model.TextEditorModel;

import java.util.ArrayList;
import java.util.List;

public class CapitalizeLettersPlugin implements Plugin {

    private TextEditorModel model;

    @Override
    public String getName() {
        return "Capitalize Letters";
    }

    @Override
    public String getDescription() {
        return "Capitalizes the first letter of every word.";
    }

    @Override
    public void execute(TextEditorModel model, UndoManager undoManager, CliboardStack clipboardStack) {
        this.model = model;
        EditAction action = new CapitalizeAction();
        action.executeDo();
        undoManager.push(action);
    }

    private class CapitalizeAction implements EditAction {
        private List<String> oldLines;

        @Override
        public void executeDo() {
            List<String> lines = model.getLines();
            oldLines = new ArrayList<>(lines);

            int i = 0;
            for (String line : lines) {
                StringBuilder sb = new StringBuilder();
                boolean whitespace = false;
                boolean first = true;
                for (char c : line.toCharArray()) {
                    if (first) {
                        c = Character.toUpperCase(c);
                        first = false;
                    }
                    if (whitespace) {
                        c = Character.toUpperCase(c);
                    }
                    whitespace = Character.isWhitespace(c);
                    sb.append(c);
                }
                lines.set(i++, sb.toString());
            }

            model.notifyTextObservers();
        }

        @Override
        public void executeUndo() {
            model.setLines(oldLines);
            model.notifyTextObservers();
        }
    }
}
