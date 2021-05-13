package hr.ooup.lab3.plugins;

import hr.ooup.lab3.clipboard.CliboardStack;
import hr.ooup.lab3.command.UndoManager;
import hr.ooup.lab3.model.TextEditorModel;

import javax.swing.*;
import java.util.Iterator;

public class StatisticsPlugin implements Plugin {
    @Override
    public String getName() {
        return "Statistics";
    }

    @Override
    public String getDescription() {
        return "Counts lines, words and letters in the document.";
    }

    @Override
    public void execute(TextEditorModel model, UndoManager undoManager, CliboardStack clipboardStack) {
            Iterator<String> it = model.allLines();
            int lines = 0;
            int words = 0;
            int letters = 0;
            while (it.hasNext()) {
                String line = it.next();
                lines++;
                words += line.isBlank() ? 0 : line.split("\s+").length;
                for (char c : line.toCharArray()) {
                    if (Character.isLetter(c))
                        letters++;
                }
            }

            JOptionPane.showMessageDialog(null,
                    "Lines: " + lines + ", Words: " + words + ", Letters: " + letters,
                    "Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
}
