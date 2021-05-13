package hr.ooup.lab3.plugins;

import hr.ooup.lab3.clipboard.CliboardStack;
import hr.ooup.lab3.command.UndoManager;
import hr.ooup.lab3.model.TextEditorModel;

public interface Plugin {
    String getName();
    String getDescription();
    void execute(TextEditorModel model, UndoManager undoManager, CliboardStack clipboardStack);
}
