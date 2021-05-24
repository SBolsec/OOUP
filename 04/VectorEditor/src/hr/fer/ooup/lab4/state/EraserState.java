package hr.fer.ooup.lab4.state;

import hr.fer.ooup.lab4.DocumentModel;
import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.model.GraphicalObject;
import hr.fer.ooup.lab4.model.LineSegment;
import hr.fer.ooup.lab4.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public class EraserState extends IdleState {

    private DocumentModel model;
    private List<LineSegment> lines = new ArrayList<>();
    private Point lastPoint = null;

    public EraserState(DocumentModel model) {
        this.model = model;
    }

    @Override
    public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        List<GraphicalObject> toRemove = new ArrayList<>();
        for (LineSegment line : lines) {
            for (int i = 0, n = line.getNumberOfHotPoints(); i < n; i++) {
                GraphicalObject go = model.findSelectedGraphicalObject(line.getHotPoint(i));
                if (go != null) {
                    toRemove.add(go);
                }
            }
        }

        for (GraphicalObject o : toRemove) {
            model.removeGraphicalObject(o);
        }

        lines.clear();
        lastPoint = null;
        model.notifyListeners();
    }

    @Override
    public void mouseDragged(Point mousePoint) {
        if (lastPoint == null) {
            lastPoint = mousePoint;
            return;
        }

        lines.add(new LineSegment(lastPoint, mousePoint));
        lastPoint = mousePoint;
        model.notifyListeners();
    }

    @Override
    public void afterDraw(Renderer r) {
        for (LineSegment line : lines) {
            r.drawLine(line.getHotPoint(0), line.getHotPoint(1));
        }
    }
}
