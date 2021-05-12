package hr.ooup.lab3.command;

import hr.ooup.lab3.model.TextEditorModel;

public class InsertCharacterAction implements EditAction {

    private TextEditorModel model;

    public InsertCharacterAction(TextEditorModel model) {
        this.model = model;
    }

    @Override
    public void executeDo() {

    }

    @Override
    public void executeUndo() {

    }
}
