package cesar2.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import cesar2.gui.display.LedDisplay;

public class ConditionsPanel extends JPanel {
    private static final long serialVersionUID = -8590783164128876307L;

    private final LedDisplay negative;
    private final LedDisplay zero;
    private final LedDisplay overflow;
    private final LedDisplay carry;

    public ConditionsPanel() {
        super();
        setLayout(new GridLayout(1, 4, 4, 4));

        negative = new LedDisplay();
        zero = new LedDisplay();
        overflow = new LedDisplay();
        carry = new LedDisplay();

        add(LedDisplay.wrap(negative, "N"));
        add(LedDisplay.wrap(zero, "Z"));
        add(LedDisplay.wrap(overflow, "V"));
        add(LedDisplay.wrap(carry, "C"));

        setPreferredSize(getPreferredSize());
    }

    public LedDisplay getNegative() {
        return negative;
    }

    public LedDisplay getZero() {
        return zero;
    }

    public LedDisplay getOverflow() {
        return overflow;
    }

    public LedDisplay getCarry() {
        return carry;
    }
}
