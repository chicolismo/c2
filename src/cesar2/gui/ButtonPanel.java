package cesar2.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class ButtonPanel extends JPanel {
    private static final long serialVersionUID = 8297489232573934877L;

    private static final ImageIcon TOOLS_ICON;
    private static final ImageIcon CONFIG_ICON;

    static {
        BufferedImage tools = null;
        BufferedImage config = null;
        try {
            tools = ImageIO.read(ButtonPanel.class.getResourceAsStream("/cesar2/images/tools.bmp"));
            config = ImageIO.read(ButtonPanel.class.getResourceAsStream("/cesar2/images/config.bmp"));
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao ler os ícones dos botões");
            System.exit(1);
        }
        TOOLS_ICON = new ImageIcon(tools);
        CONFIG_ICON = new ImageIcon(config);
    }

    private final JToggleButton btnHexadecimal;
    private final JToggleButton btnDecimal;
    private final JToggleButton btnRun;
    private final JButton btnNext;

    public ButtonPanel() {
        super();

        setBorder(new EmptyBorder(3, 3, 3, 3));

        btnDecimal = new Buttons.BevelToggleButton("0..9");
        btnHexadecimal = new Buttons.BevelToggleButton("0..F");
        btnRun = new Buttons.BevelToggleButton(CONFIG_ICON);
        btnNext = new Buttons.BevelButton(TOOLS_ICON);

        final ButtonGroup group = new ButtonGroup();
        group.add(btnDecimal);
        group.add(btnHexadecimal);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.add(btnDecimal);
        leftPanel.add(btnHexadecimal);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.add(btnRun);
        rightPanel.add(btnNext);

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0 };
        gridBagLayout.rowWeights = new double[] { 0.0 };
        setLayout(gridBagLayout);

        GridBagConstraints c_0 = new GridBagConstraints();
        c_0.fill = GridBagConstraints.VERTICAL;
        c_0.insets = new Insets(0, 0, 0, 5);
        c_0.gridx = 0;
        c_0.gridy = 0;
        add(leftPanel, c_0);

        GridBagConstraints c_1 = new GridBagConstraints();
        c_1.fill = GridBagConstraints.VERTICAL;
        c_1.gridx = 2;
        c_1.gridy = 0;
        add(rightPanel, c_1);
    }

    public JToggleButton getBtnHexadecimal() {
        return btnHexadecimal;
    }

    public JToggleButton getBtnDecimal() {
        return btnDecimal;
    }

    public JToggleButton getBtnRun() {
        return btnRun;
    }

    public JButton getBtnNext() {
        return btnNext;
    }

    private static class Buttons {
        private static final Insets CUSTOM_INSETS = new Insets(4, 4, 4, 4);

        private static final Border raisedBorder = new BevelBorder(BevelBorder.RAISED, null, null, null, null) {
            private static final long serialVersionUID = -1249034374067912355L;

            @Override
            public Insets getBorderInsets(Component c) {
                return CUSTOM_INSETS;
            }
        };

        private static final Border loweredBorder = new BevelBorder(BevelBorder.LOWERED, null, null, null, null) {
            private static final long serialVersionUID = -2126676772246091808L;

            @Override
            public Insets getBorderInsets(Component c) {
                return CUSTOM_INSETS;
            }
        };

        public static class BevelButton extends JButton {
            private static final long serialVersionUID = 7072719102086837786L;

            public BevelButton(ImageIcon icon) {
                super(icon);
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isArmed()) {
                    g.setColor(getBackground());
                    setBorder(loweredBorder);
                }
                else {
                    g.setColor(Color.lightGray);
                    setBorder(raisedBorder);
                }
                super.paintComponent(g);
            }
        }

        public static class BevelToggleButton extends JToggleButton {
            private static final long serialVersionUID = 7072719102086837786L;

            public BevelToggleButton(String text) {
                super(text);
            }

            public BevelToggleButton(ImageIcon icon) {
                super(icon);
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isSelected()) {
                    g.setColor(getBackground());
                    setBorder(loweredBorder);
                }
                else {
                    g.setColor(Color.lightGray);
                    setBorder(raisedBorder);
                }
                super.paintComponent(g);
            }
        }
    }
}
