package hr.fer.ooup.lab4.model;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.geometry.Rectangle;
import hr.fer.ooup.lab4.util.GeometryUtil;

public class Oval extends AbstractGraphicalObject {

    private static final int NUMBER_OF_POINTS = 180;

    private Point center;

    public Oval() {
        super(new Point[]{
                new Point(0,10),
                new Point(10,0)
        });
        center = new Point(5,5);
    }

    public Oval(Point downHotPoint, Point rightHotPoint) {
        super(new Point[]{rightHotPoint, downHotPoint});

        center = new Point(downHotPoint.getX(), rightHotPoint.getY());
    }

    @Override
    public Rectangle getBoundingBox() {
        Point down = getHotPoint(0);
        Point right = getHotPoint(1);

        int x = down.getX() - (right.getX() - down.getX());
        int y = right.getY() - (down.getY() - right.getY());

        int width = 2 * (right.getX() - down.getX());
        int height = 2 * (down.getY() - right.getY());

        return new Rectangle(x, y, width, height);
    }

    @Override
    public double selectionDistance(Point mousePoint) {
        Point down = getHotPoint(0);
        Point right = getHotPoint(1);

        int a = right.getX() - down.getX();
        int b = down.getY() - right.getY();
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

    private Point[] getPoints(int n) {
        Point down = getHotPoint(0);
        Point right = getHotPoint(1);

        int a = right.getX() - down.getX();
        int b = down.getY() - right.getY();

        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            double t = (2 * Math.PI / n) * i;
            int x = (int)(a * Math.cos(t)) + center.getX();
            int y = (int)(b * Math.sin(t)) + center.getY();
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
        Point a = getHotPoint(0);
        Point b = getHotPoint(1);

        return new Oval(
                new Point(a.getX(), a.getY()),
                new Point(b.getX(), b.getY())
        );
    }
}
