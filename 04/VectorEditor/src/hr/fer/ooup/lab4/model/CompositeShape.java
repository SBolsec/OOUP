package hr.fer.ooup.lab4.model;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.geometry.Rectangle;
import hr.fer.ooup.lab4.listeners.GraphicalObjectListener;
import hr.fer.ooup.lab4.renderer.Renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeShape implements GraphicalObject {

    boolean selected;
    private List<GraphicalObject> children;
    private List<GraphicalObjectListener> listeners;

    public CompositeShape() {
        this.selected = false;
        this.children = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public CompositeShape(List<GraphicalObject> children) {
        this.children = children;
        this.listeners = new ArrayList<>();
        this.selected = false;
        for (GraphicalObject go : children) {
            if (go.isSelected()) {
                this.selected = true;
                break;
            }
        }
    }

    public CompositeShape(boolean selected, List<GraphicalObject> children) {
        this.selected = selected;
        this.children = children;
        this.listeners = new ArrayList<>();
    }

    public void addChild(GraphicalObject go) {
        children.add(go);
        notifyListeners();
        notifySelectionListeners();
    }

    public void removeChild(GraphicalObject go) {
        children.remove(go);
        notifyListeners();
        notifySelectionListeners();
    }

    public List<GraphicalObject> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        notifySelectionListeners();
    }

    @Override
    public int getNumberOfHotPoints() {
        return 0;
    }

    @Override
    public Point getHotPoint(int index) {
        return null;
    }

    @Override
    public void setHotPoint(int index, Point point) {

    }

    @Override
    public boolean isHotPointSelected(int index) {
        return false;
    }

    @Override
    public void setHotPointSelected(int index, boolean selected) {

    }

    @Override
    public double getHotPointDistance(int index, Point mousePoint) {
        return Double.MAX_VALUE;
    }

    @Override
    public void translate(Point delta) {
        for (GraphicalObject go : children) {
            go.translate(delta);
        }
        notifyListeners();
    }

    @Override
    public Rectangle getBoundingBox() {
        if (children.isEmpty()) return null;

        Rectangle first = children.get(0).getBoundingBox();
        int x0 = first.getX();
        int y0 = first.getY();
        int x1 = first.getX() + first.getWidth();
        int y1 = first.getY() + first.getHeight();

        for (int i = 1; i < children.size(); i++) {
            Rectangle r = children.get(i).getBoundingBox();
            if (r.getX() < x0) x0 = r.getX();
            if (r.getY() < y0) y0 = r.getY();
            if (r.getX()+r.getWidth() > x1) x1 = r.getX()+r.getWidth();
            if (r.getY()+r.getHeight() > y1) y1 = r.getY()+r.getHeight();
        }

        return new Rectangle(x0, y0, x1-x0, y1-y0);
    }

    @Override
    public double selectionDistance(Point mousePoint) {
        if(children.isEmpty()) {
            return Double.MAX_VALUE;
        }

        double min = Double.MAX_VALUE;
        for (GraphicalObject go : children) {
            double distance = go.selectionDistance(mousePoint);
            if (distance < min) min = distance;
        }

        return min;
    }

    @Override
    public void render(Renderer r) {
        for (GraphicalObject go : children) {
            go.render(r);
        }
        notifyListeners();
    }

    @Override
    public void addGraphicalObjectListener(GraphicalObjectListener l) {
        listeners.add(l);
    }

    @Override
    public void removeGraphicalObjectListener(GraphicalObjectListener l) {
        listeners.remove(l);
    }

    @Override
    public String getShapeName() {
        return "Kompozit";
    }

    @Override
    public GraphicalObject duplicate() {
        List<GraphicalObject> newChildren = new ArrayList<>();
        for (GraphicalObject go : children) {
            newChildren.add(go.duplicate());
        }
        return new CompositeShape(selected, newChildren);
    }

    @Override
    public String getShapeID() {
        return "@COMP";
    }

    @Override
    public void save(List<String> rows) {
        for (GraphicalObject go : children) {
            go.save(rows);
        }
        rows.add(getShapeID() + " " + children.size());
    }

    protected void notifyListeners() {
        for (GraphicalObjectListener l : listeners) {
            l.graphicalObjectChanged(this);
        }
    }

    protected void notifySelectionListeners() {
        for (GraphicalObjectListener l : listeners) {
            l.graphicalObjectSelectionChanged(this);
        }
    }
}
