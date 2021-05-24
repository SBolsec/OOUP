package hr.fer.ooup.lab4.model;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.geometry.Rectangle;
import hr.fer.ooup.lab4.renderer.Renderer;
import hr.fer.ooup.lab4.util.GeometryUtil;

public class Oval extends AbstractGraphicalObject {

    private static final int NUMBER_OF_POINTS = 180;

    public Oval() {
        super(new Point[]{
                new Point(0,10),
                new Point(10,0)
        });
    }

    public Oval(Point downHotPoint, Point rightHotPoint) {
        super(new Point[]{downHotPoint, rightHotPoint});
    }

    @Override
    public Rectangle getBoundingBox() {
        Point down = getHotPoint(0);
        Point right = getHotPoint(1);

        int x, y, width, height;

        if (down.getX() <= right.getX()) {
            x = down.getX() - (right.getX() - down.getX());
            width = 2 * (right.getX() - down.getX());
        } else {
            x = right.getX();
            width = 2 * (down.getX() - right.getX());
        }

        if (right.getY() <= down.getY()) {
            y = right.getY() - (down.getY() - right.getY());
            height = 2 * (down.getY() - right.getY());
        } else {
            y = down.getY();
            height = 2 * (right.getY() - down.getY());
        }

        return new Rectangle(x, y, width, height);
    }

    @Override
    public double selectionDistance(Point mousePoint) {
        Point down = getHotPoint(0);
        Point right = getHotPoint(1);

        int a = Math.abs(right.getX() - down.getX());
        int b = Math.abs(down.getY() - right.getY());
        int p = down.getX();
        int q = right.getY();

        double e = Math.pow(mousePoint.getX() - p, 2) / Math.pow(a, 2) + Math.pow(mousePoint.getY() - q, 2) / Math.pow(b, 2);
        if (e <= 1) { // Clicked inside oval
            return 0;
        }

        Point[] points = getPoints(NUMBER_OF_POINTS);
        double min = GeometryUtil.distanceFromPoint(points[0], mousePoint);
        for (int i = 1; i < points.length; i++) {
            double distance = GeometryUtil.distanceFromPoint(points[i], mousePoint);
            if (distance < min)
                min = distance;
        }
        return min;
    }

    @Override
    public void render(Renderer r) {
        r.fillPolygon(getPoints(NUMBER_OF_POINTS));
    }

    private Point[] getPoints(int n) {
        Point down = getHotPoint(0);
        Point right = getHotPoint(1);

        int a = Math.abs(right.getX() - down.getX());
        int b = Math.abs(down.getY() - right.getY());

        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            double t = (2 * Math.PI / n) * i;
            int x = (int)(a * Math.cos(t)) + down.getX();
            int y = (int)(b * Math.sin(t)) + right.getY();
            points[i] = new Point(x, y);
        }
        return points;
    }

    @Override
    public String getShapeName() {
        return "Oval";
    }

    @Override
    public GraphicalObject duplicate() {
        Point down = getHotPoint(0);
        Point right = getHotPoint(1);

        return new Oval(
                new Point(down.getX(), down.getY()),
                new Point(right.getX(), right.getY())
        );
    }

    @Override
    public void translate(Point delta) {
        Point down = getHotPoint(0).translate(delta);
        Point right = getHotPoint(1).translate(delta);

        setHotPoint(0, down);
        setHotPoint(1, right);
    }
}
