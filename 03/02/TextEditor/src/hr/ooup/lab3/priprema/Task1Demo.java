package hr.ooup.lab3.priprema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Task1Demo extends JFrame {
    public Task1Demo() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Task1 Demo");
        setSize(800,600);

        initGUI();
    }

    private void initGUI() {
        Container cp = this.getContentPane();
        cp.setLayout(new BorderLayout());

        Task1 task = new Task1("This is the first line", "Some other text");
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dispose();
                }
            }
        });
        cp.add(task, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Task1Demo().setVisible(true));
    }
}
