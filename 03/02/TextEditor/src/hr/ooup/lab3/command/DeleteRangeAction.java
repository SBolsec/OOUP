package hr.ooup.lab3.command;

import hr.ooup.lab3.model.TextEditorModel;

public class DeleteRangeAction implements EditAction {

    private TextEditorModel model;

    public DeleteRangeAction(TextEditorModel model) {
        this.model = model;
    }

    @Override
    public void executeDo() {

    }

    @Override
    public void executeUndo() {

    }
}
