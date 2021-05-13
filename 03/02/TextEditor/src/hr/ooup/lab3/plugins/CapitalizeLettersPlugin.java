package hr.ooup.lab3.plugins;

import hr.ooup.lab3.clipboard.CliboardStack;
import hr.ooup.lab3.command.UndoManager;
import hr.ooup.lab3.model.TextEditorModel;

import java.util.List;

public class CapitalizeLettersPlugin implements Plugin {
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
        List<String> lines = model.getLines();

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
                if (Character.isWhitespace(c)) {
                    whitespace = true;
                } else {
                    whitespace = false;
                }
                sb.append(c);
            }
            lines.set(i++, sb.toString());
        }
        model.notifyTextObservers();
    }
}
