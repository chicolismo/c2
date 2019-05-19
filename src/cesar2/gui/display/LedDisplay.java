package cesar2.gui.display;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class LedDisplay extends JPanel {
    private static final long serialVersionUID = 3909006919802599588L;

    private static final int WIDTH = 15;
    private static final int HEIGHT = 15;
    private static final BufferedImage[] lights;

    private boolean isOn;

    static {
        lights = new BufferedImage[2];
        try {
            lights[0] = ImageIO.read(LedDisplay.class.getResourceAsStream("/cesar2/images/light_off.png"));
            lights[1] = ImageIO.read(LedDisplay.class.getResourceAsStream("/cesar2/images/light_on.png"));
        }
        catch (IOException e) {
            System.err.println("Erro ao ler as imagens do LED.");
            e.printStackTrace();
        }
    }

    public LedDisplay() {
        super(true);
        Dimension size = new Dimension(WIDTH, HEIGHT);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        isOn = false;
    }

    public void setTurnedOn(boolean isOn) {
        this.isOn = isOn;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(isOn ? lights[1] : lights[0], 0, 0, WIDTH, HEIGHT, null);
    }

    public static JPanel wrap(LedDisplay display, String title, Insets insets) {
        JPanel panel = new JPanel(true);
        panel.setLayout(new GridBagLayout());
        panel.add(display);
        Dimension size;
        size = panel.getPreferredSize();
        panel.setSize(size);
        panel.setPreferredSize(size);
        panel.setMaximumSize(size);
        panel.setMinimumSize(size);
        JPanel outerPanel = new JPanel(true);
        outerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        outerPanel.setBorder(getBorder(title, insets));
        outerPanel.add(panel);
        size = outerPanel.getPreferredSize();
        outerPanel.setSize(size);
        outerPanel.setPreferredSize(size);
        outerPanel.setMaximumSize(size);
        outerPanel.setMinimumSize(size);
        return outerPanel;
    }

    public static JPanel wrap(LedDisplay display, String title) {
        return wrap(display, title, new Insets(12, 2, 4, 2));
    }

    private static TitledBorder getBorder(String title, final Insets insets) {
        TitledBorder border = new TitledBorder(title) {
            private static final long serialVersionUID = 7188130539443375885L;

            @Override
            public Insets getBorderInsets(Component c) {
                Insets customInsets = insets;
                return customInsets;
            }
        };
        border.setTitleJustification(TitledBorder.CENTER);
        return border;
    }
}
