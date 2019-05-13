package cesar2.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class ButtonPanel extends JPanel implements ActionListener {
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
        super(true);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        btnDecimal = new JToggleButton("0..9");
//        btnDecimal.putClientProperty("JButton.buttonType", "square");
        btnDecimal.putClientProperty("JButton.buttonType", "segmented");
        btnDecimal.putClientProperty("JButton.segmentPosition", "first");
        btnDecimal.putClientProperty("JComponent.sizeVariant", "small");
        btnDecimal.setActionCommand("decimal");

        btnHexadecimal = new JToggleButton("0..F");
//        btnHexadecimal.putClientProperty("JButton.buttonType", "square");
        btnHexadecimal.putClientProperty("JButton.buttonType", "segmented");
        btnHexadecimal.putClientProperty("JButton.segmentPosition", "last");
        btnHexadecimal.putClientProperty("JComponent.sizeVariant", "small");
        btnHexadecimal.setActionCommand("hexadecimal");

        btnRun = new JToggleButton(CONFIG_ICON);
        btnRun.putClientProperty("JButton.buttonType", "square");
        btnRun.putClientProperty("JComponent.sizeVariant", "small");

        btnNext = new JButton(TOOLS_ICON);
        btnNext.putClientProperty("JButton.buttonType", "square");
        btnNext.putClientProperty("JComponent.sizeVariant", "small");

        final ButtonGroup group = new ButtonGroup();
        group.add(btnDecimal);
        group.add(btnHexadecimal);

        add(btnDecimal);
        add(btnHexadecimal);
        add(btnRun);
        add(btnNext);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }
}
