package hr.fer.ooup.lab4.state;

import hr.fer.ooup.lab4.DocumentModel;
import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.model.GraphicalObject;

public class AddShapeState extends IdleState {

    private GraphicalObject prototype;
    private DocumentModel model;

    public AddShapeState(DocumentModel model, GraphicalObject prototype) {
        this.prototype = prototype;
        this.model = model;
    }

    @Override
    public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        GraphicalObject newObject = prototype.duplicate();
        newObject.translate(mousePoint);
        model.addGraphicalObject(newObject);
    }
}
