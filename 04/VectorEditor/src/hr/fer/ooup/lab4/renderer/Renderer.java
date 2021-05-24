package hr.fer.ooup.lab4.renderer;

import hr.fer.ooup.lab4.geometry.Point;

public interface Renderer {
    void drawLine(Point s, Point e);
    void fillPolygon(Point[] points);
}
