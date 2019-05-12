package cesar2.gui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cesar2.gui.display.LedDisplay;

public class MainPanel extends JPanel {
    private static final long serialVersionUID = 1679403374822922152L;

    private final RegisterPanel[] registers;
    private final InterruptionPanel interruption;
    private final ExecutionPanel executionPanel;
    private final LedDisplay negative;
    private final LedDisplay zero;
    private final LedDisplay overflow;
    private final LedDisplay carry;
    private final ButtonPanel buttons;

    public MainPanel() {
        interruption = new InterruptionPanel();
        executionPanel = new ExecutionPanel();
        buttons = new ButtonPanel();

        RegisterPanel r0 = new RegisterPanel("R0");
        RegisterPanel r1 = new RegisterPanel("R1");
        RegisterPanel r2 = new RegisterPanel("R2");
        RegisterPanel r3 = new RegisterPanel("R3");
        RegisterPanel r4 = new RegisterPanel("R4");
        RegisterPanel r5 = new RegisterPanel("R5");
        RegisterPanel r6 = new RegisterPanel("R6 (SP)");
        RegisterPanel r7 = new RegisterPanel("R7 (PC)");

        registers = new RegisterPanel[] { r0, r1, r2, r3, r4, r5, r6, r7 };

        JPanel registersPanel = new JPanel(true);
        registersPanel.setLayout(new GridLayout(3, 3, 4, 4));

        registersPanel.add(wrap(r0, "R0:"));
        registersPanel.add(wrap(r1, "R1:"));
        registersPanel.add(wrap(r2, "R2"));
        registersPanel.add(wrap(r3, "R3"));
        registersPanel.add(wrap(r4, "R4"));
        registersPanel.add(wrap(r5, "R5"));
        registersPanel.add(wrap(r6, "R6 (SP):"));
        registersPanel.add(interruption);
        registersPanel.add(wrap(r7, "R7 (PC):"));
        registersPanel.setPreferredSize(registersPanel.getPreferredSize());
        registersPanel.setMaximumSize(registersPanel.getPreferredSize());


        negative = new LedDisplay();
        zero = new LedDisplay();
        overflow = new LedDisplay();
        carry = new LedDisplay();

        JPanel conditionPanel = new JPanel(true);
        conditionPanel.add(LedDisplay.wrap(negative, "N"));
        conditionPanel.add(LedDisplay.wrap(zero, "Z"));
        conditionPanel.add(LedDisplay.wrap(overflow, "V"));
        conditionPanel.add(LedDisplay.wrap(carry, "C"));


//        GridBagLayout grid = new GridBagLayout();
//        setLayout(grid);


        add(registersPanel);
        add(wrap(executionPanel, "Execuções"));
        add(conditionPanel);
        add(buttons);
        setPreferredSize(getPreferredSize());
    }

    public RegisterPanel[] getRegisters() {
        return registers;
    }

    private static JPanel wrap(JComponent component, String title, boolean centerTitle) {
        JPanel panel = new JPanel(true);
        TitledBorder border = new TitledBorder(title);
        if (centerTitle) {
            border.setTitleJustification(TitledBorder.TOP);
        }
        panel.setBorder(border);
        panel.add(component);
        return panel;
    }

    private static JPanel wrap(JComponent component, String title) {
        return wrap(component, title, false);
    }
}
