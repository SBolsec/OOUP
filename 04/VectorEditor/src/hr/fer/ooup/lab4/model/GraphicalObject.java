package hr.fer.ooup.lab4.model;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.geometry.Rectangle;
import hr.fer.ooup.lab4.listeners.GraphicalObjectListener;
import hr.fer.ooup.lab4.renderer.Renderer;

public interface GraphicalObject {

    // Podrška za uređivanje objekta
    boolean isSelected();
    void setSelected(boolean selected);
    int getNumberOfHotPoints();
    Point getHotPoint(int index);
    void setHotPoint(int index, Point point);
    boolean isHotPointSelected(int index);
    void setHotPointSelected(int index, boolean selected);
    double getHotPointDistance(int index, Point mousePoint);

    // Geometrijska operacija nad oblikom
    void translate(Point delta);
    Rectangle getBoundingBox();
    double selectionDistance(Point mousePoint);

    // Podrška za crtanje (dio mosta)
    void render(Renderer r);

    // Observer za dojavu promjena modelu
    void addGraphicalObjectListener(GraphicalObjectListener l);
    void removeGraphicalObjectListener(GraphicalObjectListener l);

    // Podrška za prototip (alatna traka, stvaranje objekata u crtežu, ...)
    String getShapeName();
    GraphicalObject duplicate();

    // Podrška za snimanje i učitavanje
//    String getShapeID();
//    void load(Stack<GraphicalObject> stack, String data);
//    void save(List<String> rows);
}
