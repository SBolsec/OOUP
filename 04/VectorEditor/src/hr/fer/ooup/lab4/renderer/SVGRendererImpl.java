package hr.fer.ooup.lab4.renderer;

import hr.fer.ooup.lab4.geometry.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SVGRendererImpl implements Renderer {

    private List<String> lines = new ArrayList<>();
    private String fileName;

    public SVGRendererImpl(String fileName) {
        this.fileName = fileName;
        lines.add("<svg  xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
    }

    public void close() throws IOException {
        lines.add("</svg>");
        FileWriter fw = new FileWriter(fileName);
        for (String line : lines) {
            fw.write(line);
        }
        fw.flush();
        fw.close();
    }

    @Override
    public void drawLine(Point s, Point e) {
        lines.add(String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"stroke:#0000ff;\" />",
                s.getX(), s.getY(), e.getX(), e.getY()));
    }

    @Override
    public void fillPolygon(Point[] points) {
        StringBuilder sb = new StringBuilder();
        sb.append("<polygon points=\"");
        for (int i = 0, n = points.length; i < n; i++) {
            sb.append(points[i].getX()).append(",").append(points[i].getY());
            if (i != n-1) sb.append("  ");
        }
        sb.append("\" style=\"stroke:#ff0000; fill:#0000ff;\" />");
        lines.add(sb.toString());
    }
}
