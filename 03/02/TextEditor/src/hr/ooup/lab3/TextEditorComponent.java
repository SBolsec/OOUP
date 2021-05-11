package hr.ooup.lab3;

import hr.ooup.lab3.model.Location;
import hr.ooup.lab3.model.LocationRange;
import hr.ooup.lab3.model.TextEditorModel;
import hr.ooup.lab3.observer.CursorObserver;
import hr.ooup.lab3.observer.TextObserver;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class TextEditorComponent extends JComponent implements CursorObserver, TextObserver {
    private static final long serialVersionUID = 1;
    private static final int x0 = 10;
    private static final int y0 = 16;

    private TextEditorModel model;

    public TextEditorComponent(TextEditorModel model) {
        this.model = model;
        model.attachCursorObserver(this);
        model.attachTextObserver(this);

        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fontMetrics = getFontMetrics(getFont());

        LocationRange selectedRange = model.getSelectionRange();
        LocationRange selectedText = null; // sorting so the start is first for easier drawing
        if (selectedRange != null) {
            if (selectedRange.getStart().compareTo(selectedRange.getEnd()) == -1) {
                selectedText = selectedRange;
            } else if (selectedRange.getStart().compareTo(selectedRange.getEnd()) == 1) {
                selectedText = new LocationRange(selectedRange.getEnd(), selectedRange.getStart());
            }
        }

        Location cursorLocation = model.getCursorLocation();

        Iterator<String> it = model.allLines();
        int index = 0;
        int y = y0;
        while (it.hasNext()) {
            String line = it.next();

            // draw the highlight on the selected text
            if (selectedText != null && index >= selectedText.getStart().getY() && index <= selectedText.getEnd().getY()) {
                g2d.setColor(Color.YELLOW);
                if (index == selectedText.getStart().getY()) {
                    int charsToSkip = selectedText.getStart().getX();
                    int offset = x0;
                    int width = 0;
                    int count = 0;
                    for (char c : line.toCharArray()) {
                        if (charsToSkip > 0) {
                            offset += fontMetrics.charWidth(c);
                            charsToSkip--;
                        } else {
                            if (selectedText.getEnd().getY() == selectedText.getStart().getY()) {
                                if (count < selectedText.getEnd().getX()) {
                                    width += fontMetrics.charWidth(c);
                                } else {
                                    break;
                                }
                            } else {
                                width += fontMetrics.charWidth(c);
                            }
                        }
                        count++;
                    }
                    g2d.fillRect(offset, y - fontMetrics.getAscent(), width, fontMetrics.getHeight());
                }
                else if (index == selectedText.getEnd().getY()) {
                    int charsToFill = selectedText.getEnd().getX();
                    int offset = x0;
                    int width = 0;
                    for (char c : line.toCharArray()) {
                        if (charsToFill > 0) {
                            width += fontMetrics.charWidth(c);
                            charsToFill--;
                        } else {
                            break;
                        }
                    }
                    g2d.fillRect(offset, y - fontMetrics.getAscent(), width, fontMetrics.getHeight());
                }
                else {
                    int offset = x0;
                    int width = 0;
                    for (char c : line.toCharArray()) {
                        width += fontMetrics.charWidth(c);
                    }
                    g2d.fillRect(offset, y - fontMetrics.getAscent(), width, fontMetrics.getHeight());
                }

                g2d.setColor(Color.BLACK);
            }

            // draw the string
            g2d.drawString(line, x0, y);

            // draw the cursor
            if (cursorLocation != null && index == cursorLocation.getY()) {
                int charsLeft = cursorLocation.getX();
                int offset = x0;
                for (char c : line.toCharArray()) {
                    if (charsLeft > 0) {
                        offset += fontMetrics.charWidth(c);
                        charsLeft--;
                    } else {
                        break;
                    }
                }
                offset -= 1; // push one bit to left so that it does not override character
                g2d.setColor(Color.BLUE);
                g2d.drawLine(offset, y + fontMetrics.getDescent(), offset, y - fontMetrics.getAscent());
                g2d.setColor(Color.BLACK);
            }

            index++;
            y += fontMetrics.getHeight();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fontMetrics = getFontMetrics(getFont());

        int x = 0;
        int y = y0;
        Iterator<String> it = model.allLines();
        while (it.hasNext()) {
            String line = it.next();
            x = Math.max(x, fontMetrics.stringWidth(line));
            y += fontMetrics.getHeight();
        }

        return new Dimension(2*x0 + x, y);
    }

    @Override
    public void updateCursorLocation(Location loc) {
        revalidate();
        repaint();
    }

    @Override
    public void updateText() {
        revalidate();
        repaint();
    }
}