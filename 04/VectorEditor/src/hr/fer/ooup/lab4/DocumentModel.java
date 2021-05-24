package hr.fer.ooup.lab4;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.listeners.DocumentModelListener;
import hr.fer.ooup.lab4.listeners.GraphicalObjectListener;
import hr.fer.ooup.lab4.model.GraphicalObject;
import hr.fer.ooup.lab4.util.GeometryUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocumentModel {

    public static final double SELECTION_PROXIMITY = 10;

    // Kolekcija svih grafičkih objekata
    private List<GraphicalObject> objects = new ArrayList<>();
    // Read-Only proxy oko kolekcije grafičkih objekata
    private List<GraphicalObject> roObjects = Collections.unmodifiableList(objects);
    // Kolekcija prijavljenih promatrača
    private List<DocumentModelListener> listeners = new ArrayList<>();
    // Kolekcija selektiranih objekata
    private List<GraphicalObject> selectedObjects = new ArrayList<>();
    // Read-Only proxy oko kolekcije selektiranih objekata
    private List<GraphicalObject> roSelectedObjects = Collections.unmodifiableList(selectedObjects);

    // Promatrač koji će biti registriran nad svim objektima crteža
    private final GraphicalObjectListener goListener = new GraphicalObjectListener() {
        @Override
        public void graphicalObjectChanged(GraphicalObject go) {
            notifyListeners();
        }

        @Override
        public void graphicalObjectSelectionChanged(GraphicalObject go) {
            int index = selectedObjects.indexOf(go);
            if (go.isSelected()) {
                if (index == -1) selectedObjects.add(go);
                else return;
            } else {
                if (index == -1) return;
                else selectedObjects.remove(go);
            }
            notifyListeners();
        }
    };

    // Konstruktor
    public DocumentModel() {
    }

    // Brisanje svih objekata iz modela (pazite da se sve potrebno odregistrira)
    // i potom obavijeste svi promatrači modela
    public void clear() {
        for (GraphicalObject g : objects) {
            g.removeGraphicalObjectListener(goListener);
        }
        objects.clear();
        selectedObjects.clear();
        notifyListeners();
    }

    // Dodavanje objekta u dokument (pazite je li već selektiran; registrirajte model kao promatrača)
    public void addGraphicalObject(GraphicalObject obj) {
        if (obj.isSelected())
            selectedObjects.add(obj);
        objects.add(obj);
        obj.addGraphicalObjectListener(goListener);
        notifyListeners();
    }

    // Uklanjanje objekta iz dokumenta (pazite je li već selektiran; odregistrirajte model kao promatrača)
    public void removeGraphicalObject(GraphicalObject obj) {
        obj.removeGraphicalObjectListener(goListener);
        if (obj.isSelected())
            selectedObjects.remove(obj);
        objects.remove(obj);
        notifyListeners();
    }

    // Vrati nepromjenjivu listu postojećih objekata (izmjene smiju ići samo kroz metode modela)
    public List<GraphicalObject> list() {
        return roObjects;
    }

    // Prijava...
    public void addDocumentModelListener(DocumentModelListener l) {
        listeners.add(l);
    }

    // Odjava...
    public void removeDocumentModelListener(DocumentModelListener l) {
        listeners.remove(l);
    }

    // Obavještavanje...
    public void notifyListeners() {
        for (DocumentModelListener l : listeners) {
            l.documentChange();
        }
    }

    // Vrati nepromjenjivu listu selektiranih objekata
    public List<GraphicalObject> getSelectedObjects() {
        return roSelectedObjects;
    }

    // Pomakni predani objekt u listi objekata na jedno mjesto kasnije...
    // Time će se on iscrtati kasnije (pa će time možda veći dio biti vidljiv)
    public void increaseZ(GraphicalObject go) {
        int index = objects.indexOf(go);
        if (index == -1 || index == objects.size() - 1) return;

        objects.set(index, objects.get(index + 1));
        objects.set(index + 1, go);
        notifyListeners();
    }

    // Pomakni predani objekt u listi objekata na jedno mjesto ranije...
    public void decreaseZ(GraphicalObject go) {
        int index = objects.indexOf(go);
        if (index <= 0) return;

        objects.set(index, objects.get(index - 1));
        objects.set(index - 1, go);
        notifyListeners();
    }

    // Pronađi postoji li u modelu neki objekt koji klik na točku koja je
    // predana kao argument selektira i vrati ga ili vrati null. Točka selektira
    // objekt kojemu je najbliža uz uvjet da ta udaljenost nije veća od
    // SELECTION_PROXIMITY. Status selektiranosti objekta ova metoda NE dira.
    public GraphicalObject findSelectedGraphicalObject(Point mousePoint) {
        GraphicalObject res = null;
        double min = Double.MAX_VALUE;
        for (GraphicalObject go : objects) {
            double distance = go.selectionDistance(mousePoint);
            if (distance < SELECTION_PROXIMITY && distance < min) {
                min = distance;
                res = go;
            }
        }
        return res;
    }

    // Pronađi da li u predanom objektu predana točka miša selektira neki hot-point.
    // Točka miša selektira onaj hot-point objekta kojemu je najbliža uz uvjet da ta
    // udaljenost nije veća od SELECTION_PROXIMITY. Vraća se indeks hot-pointa
    // kojeg bi predana točka selektirala ili -1 ako takve nema. Status selekcije
    // se pri tome NE dira.
    public int findSelectedHotPoint(GraphicalObject object, Point mousePoint) {
        for (int i = 0, n = object.getNumberOfHotPoints(); i < n; i++) {
            Point p = object.getHotPoint(i);
            if (GeometryUtil.distanceFromPoint(p, mousePoint) < SELECTION_PROXIMITY)
                return i;
        }
        return -1;
    }

    public void deselectAll() {
        while (selectedObjects.size() > 0) {
            selectedObjects.get(0).setSelected(false);
        }
    }
}
