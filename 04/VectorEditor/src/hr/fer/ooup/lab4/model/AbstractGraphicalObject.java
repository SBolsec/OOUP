package hr.fer.ooup.lab4.model;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.listeners.GraphicalObjectListener;
import hr.fer.ooup.lab4.util.GeometryUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGraphicalObject implements GraphicalObject {
    private Point[] hotPoints;
    private boolean[] hotPointSelected;
    private boolean selected;
    List<GraphicalObjectListener> listeners = new ArrayList<>();

    protected AbstractGraphicalObject(Point[] hotPoints) {
        this.hotPoints = hotPoints;
        this.hotPointSelected = new boolean[hotPoints.length];
    }

    @Override
    public Point getHotPoint(int index) {
        if (index < 0 || index >= hotPoints.length)
            throw new IndexOutOfBoundsException("Valid indexes are from 0 to " + hotPoints.length + ", index was: " + index);

        return hotPoints[index];
    }

    @Override
    public void setHotPoint(int index, Point point) {
        if (index < 0 || index >= hotPoints.length)
            throw new IndexOutOfBoundsException("Valid indexes are from 0 to " + hotPoints.length + ", index was: " + index);

        hotPoints[index] = point;
        notifyListeners();
    }

    @Override
    public int getNumberOfHotPoints() {
        return hotPoints.length;
    }

    @Override
    public double getHotPointDistance(int index, Point mousePoint) {
        return GeometryUtil.distanceFromPoint(getHotPoint(index), mousePoint);
    }

    @Override
    public boolean isHotPointSelected(int index) {
        return hotPointSelected[index];
    }

    @Override
    public void setHotPointSelected(int index, boolean selected) {
        hotPointSelected[index] = selected;
        notifySelectionListeners();
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
    public void translate(Point delta) {
        for (int i = 0, n = getNumberOfHotPoints(); i < n; i++) {
            setHotPoint(i, getHotPoint(i).translate(delta));
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
