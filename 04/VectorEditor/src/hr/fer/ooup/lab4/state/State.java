package hr.fer.ooup.lab4.state;

import hr.fer.ooup.lab4.geometry.Point;
import hr.fer.ooup.lab4.model.GraphicalObject;
import hr.fer.ooup.lab4.renderer.Renderer;

public interface State {
    // Poziva se kad progam registrira da je pritisnuta lijeva tipka miša
    void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown);
    // Poziva se kad progam registrira da je otpuštena lijeva tipka miša
    void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown);
    // Poziva se kad progam registrira da korisnik pomiče miš dok je tipka pritisnuta
    void mouseDragged(Point mousePoint);
    // Poziva se kad progam registrira da je korisnik pritisnuo tipku na tipkovnici
    void keyPressed(int keyCode);

    // Poziva se nakon što je platno nacrtalo grafički objekt predan kao argument
    void afterDraw(Renderer r, GraphicalObject go);
    // Poziva se nakon što je platno nacrtalo čitav crtež
    void afterDraw(Renderer r);

    // Poziva se kada program napušta ovo stanje kako bi prešlo u neko drugo
    void onLeaving();
}
