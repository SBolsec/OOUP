package hr.fer.ooup.lab4.model;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.geometry.Rectangle;
import hr.fer.ooup.lab4.util.GeometryUtil;

public class LineSegment extends AbstractGraphicalObject {

    public LineSegment() {
        super(new Point[]{ new Point(0,0), new Point(10,0) });
    }

    public LineSegment(Point start, Point end) {
        super( new Point[]{ start, end });
    }

    @Override
    public Rectangle getBoundingBox() {
        Point a = getHotPoint(0);
        Point b = getHotPoint(1);

        int x = Math.min(a.getX(), b.getX());
        int y = Math.min(a.getY(), b.getY());

        int width = Math.abs(a.getX() - b.getX());
        int height = Math.abs(a.getY() - b.getY());

        return new Rectangle(x, y, width, height);
    }

    @Override
    public double selectionDistance(Point mousePoint) {
        return GeometryUtil.distanceFromLineSegment(getHotPoint(0), getHotPoint(1), mousePoint);
    }

    @Override
    public String getShapeName() {
        return "Linija";
    }

    @Override
    public GraphicalObject duplicate() {
        Point a = getHotPoint(0);
        Point b = getHotPoint(1);

        return new LineSegment(
                new Point(a.getX(), a.getY()),
                new Point(b.getX(), b.getY())
        );
    }
}
