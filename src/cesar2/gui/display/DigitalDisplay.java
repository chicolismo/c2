package cesar2.gui.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class DigitalDisplay extends JPanel {
    public static final long serialVersionUID = -527728462654031001L;

    private static final int DIGIT_WIDTH = 12;
    private static final int DIGIT_HEIGHT = 17;
    private static final int DIGIT_OFFSET = DIGIT_WIDTH + 2;
    private static final int WIDTH = 78;
    private static final int HEIGHT = 27;
    private static final int START_X = 61;
    private static final int START_Y = 5;

    private static final BufferedImage[] displayImages;
    private static final BufferedImage displayNull;
    private int value;
    private int base;
    private int nDigits;

    static {
        BufferedImage[] digits = new BufferedImage[16];
        BufferedImage emptyDigit = null;
        try {
            String pathFormat = "/cesar2/images/cesar_%1x.bmp";
            for (int i = 0; i < 16; ++i) {
                digits[i] = ImageIO.read(DigitalDisplay.class.getResourceAsStream(String.format(pathFormat, i)));
            }
            emptyDigit = ImageIO.read(DigitalDisplay.class.getResourceAsStream("/cesar2/images/cesar_null.bmp"));
        }
        catch (IllegalArgumentException | IOException e) {
            System.err.println("Erro ao tentar ler as imagens do DigitalDisplay.");
            e.printStackTrace();
            System.exit(1);
        }
        displayImages = digits;
        displayNull = emptyDigit;
    }

    public DigitalDisplay() {
        super(true);
        nDigits = 5;
        value = 0;
        base = 10;
        Dimension dim = new Dimension(WIDTH, HEIGHT);
        setSize(dim);
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    public void setValue(int value) {
        if (this.value != value) {
            this.value = value;
            this.repaint();
        }
    }

    public void setDecimal(boolean isDecimal) {
        base = isDecimal ? 10 : 16;
        nDigits = isDecimal ? 5 : 4;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        int x = START_X;
        int currentDigit = 0;
        int n = value;
        do {
            int digit = n % base;
            g.drawImage(displayImages[digit], x, START_Y, DIGIT_WIDTH, DIGIT_HEIGHT, null);
            x -= DIGIT_OFFSET;
            ++currentDigit;
            n /= base;
        } while (n > 0);

        while (currentDigit < nDigits) {
            g.drawImage(displayNull, x, START_Y, DIGIT_WIDTH, DIGIT_HEIGHT, null);
            x -= DIGIT_OFFSET;
            ++currentDigit;
        }
    }
}
