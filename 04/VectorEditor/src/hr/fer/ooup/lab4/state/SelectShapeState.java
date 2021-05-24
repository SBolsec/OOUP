package hr.fer.ooup.lab4.state;

import hr.fer.ooup.lab4.DocumentModel;
import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.geometry.Rectangle;
import hr.fer.ooup.lab4.model.CompositeShape;
import hr.fer.ooup.lab4.model.GraphicalObject;
import hr.fer.ooup.lab4.renderer.Renderer;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SelectShapeState extends IdleState {

    private static final int HOTPOINT_OFFSET = 3;

    private DocumentModel model;
    private GraphicalObject selectedObject;
    private int indexOfSelectedHotPoint;

    public SelectShapeState(DocumentModel model) {
        this.model = model;
        this.selectedObject = null;
        this.indexOfSelectedHotPoint = -1;
    }

    @Override
    public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        GraphicalObject go = model.findSelectedGraphicalObject(mousePoint);

        if (ctrlDown) {
            if (go == null) return;
            go.setSelected(!go.isSelected());
            selectedObject = null;
        } else {
            model.deselectAll();
            selectedObject = go;
            if (selectedObject != null)
                selectedObject.setSelected(true);
        }
    }

    @Override
    public void mouseDragged(Point mousePoint) {
        indexOfSelectedHotPoint = model.findSelectedHotPoint(selectedObject, mousePoint);
        if (indexOfSelectedHotPoint == -1) return;

        selectedObject.setHotPointSelected(indexOfSelectedHotPoint, true);
        selectedObject.setHotPoint(indexOfSelectedHotPoint, mousePoint);
    }

    @Override
    public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        if (selectedObject != null && indexOfSelectedHotPoint >= 0) {
            selectedObject.setHotPointSelected(indexOfSelectedHotPoint, false);
            indexOfSelectedHotPoint = -1;
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        List<GraphicalObject> selectedObjects = model.getSelectedObjects();

        switch (keyCode) {
            case KeyEvent.VK_UP:
                for (GraphicalObject go : selectedObjects) {
                    go.translate(new Point(0, -1));
                }
                break;
            case KeyEvent.VK_DOWN:
                for (GraphicalObject go : selectedObjects) {
                    go.translate(new Point(0, 1));
                }
                break;
            case KeyEvent.VK_LEFT:
                for (GraphicalObject go : selectedObjects) {
                    go.translate(new Point(-1, 0));
                }
                break;
            case KeyEvent.VK_RIGHT:
                for (GraphicalObject go : selectedObjects) {
                    go.translate(new Point(1, 0));
                }
                break;
            case KeyEvent.VK_ADD:
            case KeyEvent.VK_PLUS:
                for (GraphicalObject go : selectedObjects) {
                    model.increaseZ(go);
                }
                break;
            case KeyEvent.VK_SUBTRACT:
            case KeyEvent.VK_MINUS:
                for (GraphicalObject go : selectedObjects) {
                    model.decreaseZ(go);
                }
                break;
            case KeyEvent.VK_G:
                List<GraphicalObject> children = new ArrayList<>(selectedObjects);
                GraphicalObject composite = new CompositeShape(true, children);
                while (!model.getSelectedObjects().isEmpty()) {
                    model.removeGraphicalObject(model.getSelectedObjects().get(0));
                }
                composite.setSelected(true);
                selectedObject = composite;
                model.addGraphicalObject(composite);
                break;
            case KeyEvent.VK_U:
                if (selectedObject != null && selectedObject instanceof CompositeShape) {
                    CompositeShape compositeShape = (CompositeShape) selectedObject;
                    List<GraphicalObject> objects = new ArrayList<>(compositeShape.getChildren());
                    selectedObject = null;
                    model.removeGraphicalObject(compositeShape);
                    for (GraphicalObject go : objects) {
                        go.setSelected(true);
                        model.addGraphicalObject(go);
                    }
                }
                break;
        }
    }

    @Override
    public void afterDraw(Renderer r, GraphicalObject go) {
        if (!go.isSelected()) return;

        Rectangle box = go.getBoundingBox();

        // Draw main bounding box
        Point a = new Point(box.getX(), box.getY());
        Point b = new Point(box.getX() + box.getWidth(), box.getY());
        Point c = new Point(box.getX() + box.getWidth(), box.getY() + box.getHeight());
        Point d = new Point(box.getX(), box.getY() + box.getHeight());
        r.drawLine(a, b);
        r.drawLine(b, c);
        r.drawLine(c, d);
        r.drawLine(d, a);

        if (model.getSelectedObjects().size() > 1) return;
        // Draw hot points
        for (int i = 0, n = go.getNumberOfHotPoints(); i < n; i++) {
            Point p = go.getHotPoint(i);

            Point a1 = new Point(p.getX()-HOTPOINT_OFFSET, p.getY()-HOTPOINT_OFFSET);
            Point b1 = new Point(p.getX()+HOTPOINT_OFFSET, p.getY()-HOTPOINT_OFFSET);
            Point c1 = new Point(p.getX()+HOTPOINT_OFFSET, p.getY()+HOTPOINT_OFFSET);
            Point d1 = new Point(p.getX()-HOTPOINT_OFFSET, p.getY()+HOTPOINT_OFFSET);
            r.drawLine(a1, b1);
            r.drawLine(b1, c1);
            r.drawLine(c1, d1);
            r.drawLine(d1, a1);
        }
    }

    @Override
    public void onLeaving() {
        model.deselectAll();
    }
}
