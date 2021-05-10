package hr.ooup.lab3.priprema;

import javax.swing.*;
import java.awt.*;

public class Task1 extends JComponent {
    /** Serial version UID */
    private static final long serialVersionUID = 1;

    private String firstLine;
    private String secondLine;

    public Task1(String firstLine, String secondLine) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
    }

    @Override
    public void paintComponent(Graphics g) {
        drawLines(g);
        drawText(g);
    }

    private void drawLines(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED); // set color to red

        // draw the lines
        Rectangle window = this.getBounds();
        int x0 = window.x;
        int y0 = window.y;
        g2d.drawLine(x0+5, y0+5, window.width-10, 5);
        g2d.drawLine(x0+5, y0+5, 5, window.height-10);

        g2d.setColor(Color.BLACK); // revert the color to black
    }

    private void drawText(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g.drawString(firstLine, 25, 35);
        g.drawString(secondLine, 25, 60);
    }

}
