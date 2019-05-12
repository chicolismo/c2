package cesar2.gui;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class ButtonPanel extends JPanel {
    private JToggleButton btnHexadecimal;
    private JToggleButton btnDecimal;

    public ButtonPanel() {

        btnDecimal = new JToggleButton("0..9");
        btnDecimal.putClientProperty("JComponent.sizeVariant", "mini");

        btnHexadecimal = new JToggleButton("0..F");
        btnHexadecimal.putClientProperty("JComponent.sizeVariant", "mini");

        final ButtonGroup group = new ButtonGroup();
        group.add(btnDecimal);
        group.add(btnHexadecimal);

        add(btnDecimal);
        add(btnHexadecimal);
    }

}
