package hr.ooup.lab3.command;

import hr.ooup.lab3.model.TextEditorModel;

public class DeleteAfterAction implements EditAction {

    private TextEditorModel model;

    public DeleteAfterAction(TextEditorModel model) {
        this.model = model;
    }

    @Override
    public void executeDo() {

    }

    @Override
    public void executeUndo() {

    }
}
