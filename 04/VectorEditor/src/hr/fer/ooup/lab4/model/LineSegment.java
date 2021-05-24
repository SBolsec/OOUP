package hr.fer.ooup.lab4.model;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.geometry.Rectangle;
import hr.fer.ooup.lab4.renderer.Renderer;
import hr.fer.ooup.lab4.util.GeometryUtil;

import java.util.List;
import java.util.Stack;

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
    public void render(Renderer r) {
        r.drawLine(getHotPoint(0), getHotPoint(1));
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

    @Override
    public String getShapeID() {
        return "@LINE";
    }

    @Override
    public void load(Stack<GraphicalObject> stack, String data) {
        String[] p = data.trim().split("\s+");
        stack.push(new LineSegment(
                new Point(Integer.parseInt(p[0]), Integer.parseInt(p[1])),
                new Point(Integer.parseInt(p[2]), Integer.parseInt(p[3]))
        ));
    }

    @Override
    public void save(List<String> rows) {
        Point a = getHotPoint(0);
        Point b = getHotPoint(1);
        rows.add(getShapeID()+" "+a.getX()+" "+a.getY()+" "+b.getX()+" "+b.getY());
    }
}
