package cesar2.gui.display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class BinaryDisplay extends JPanel {
    private static final long serialVersionUID = -1075254681402812190L;

    private static BufferedImage[] displayImages;
    private static final int IMAGE_WIDTH = 5;
    private static final int IMAGE_HEIGHT = 5;
    private static final int IMAGE_OFFSET = 5;
    private static final int WIDTH = 5 * 16;
    private static final int HEIGHT = 5;
    private static final int START_X = 75;
    private static final int START_Y = 0;
    private static final int BITS = 16;
    private int value;

    static {
        displayImages = new BufferedImage[2];
        try {
            displayImages[0] = ImageIO.read(BinaryDisplay.class.getResourceAsStream("/cesar2/images/mini_led_0.png"));
            displayImages[1] = ImageIO.read(BinaryDisplay.class.getResourceAsStream("/cesar2/images/mini_led_1.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public BinaryDisplay() {
        super(true);
        Dimension dim = new Dimension(WIDTH, HEIGHT);
        setSize(dim);
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);
        value = 0;
    }

    public void setValue(int value) {
        if (this.value != value) {
            this.value = value;
            this.repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int n = value;
        int x = START_X;
        int currentImage = 0;
        while (n != 0) {
            ++currentImage;
            int index = (n & 1);
            g.drawImage(displayImages[index], x, START_Y, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            x -= IMAGE_OFFSET;
            n >>= 1;
        }
        while (currentImage < BITS) {
            ++currentImage;
            g.drawImage(displayImages[0], x, START_Y, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            x -= IMAGE_OFFSET;
        }
    }

}
