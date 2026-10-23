import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class WinPanel extends JPanel {
    private final int WIDTH = 400;
    private final int HEIGHT = 400;

    private int xVel = 5;
    private int yVel = 5;

    private int x = 0;
    private int y = 0;

    private Image winImage;

    public WinPanel () {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        winImage = new ImageIcon("win.png").getImage()
                .getScaledInstance(200, 100, Image.SCALE_SMOOTH);

        Timer timer = new Timer(10, e -> update());

        timer.start();
    }

    public void update() {
        if (x+winImage.getWidth(null) > WIDTH) {
            xVel *= -1;
        } else if (x < 0) {
            xVel *= -1;
        }

        if (y+winImage.getHeight(null) > HEIGHT) {
            yVel *= -1;
        } else if (y < 0) {
            yVel *= -1;
        }

        x += xVel;
        y += yVel;

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;

        g2D.drawImage(winImage, x, y, null);
    }
}