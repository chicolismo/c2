package cesar2.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class MainPanel extends JPanel {
    private static final long serialVersionUID = 1679403374822922152L;

    private final RegisterPanel[] registers;
    private final InterruptionPanel interruption;
    private final JPanel executionWrapper;
    private final ExecutionPanel executionPanel;
    private final ButtonPanel buttons;
    private final ConditionsPanel conditionsPanel;

    public MainPanel() {
        interruption = new InterruptionPanel();
        conditionsPanel = new ConditionsPanel();
        executionPanel = new ExecutionPanel();
        executionWrapper = wrap(executionPanel, "Execuções");
        executionWrapper.setPreferredSize(executionWrapper.getPreferredSize());
        buttons = new ButtonPanel();

        RegisterPanel r0 = new RegisterPanel(0, "R0");
        RegisterPanel r1 = new RegisterPanel(1, "R1");
        RegisterPanel r2 = new RegisterPanel(2, "R2");
        RegisterPanel r3 = new RegisterPanel(3, "R3");
        RegisterPanel r4 = new RegisterPanel(4, "R4");
        RegisterPanel r5 = new RegisterPanel(5, "R5");
        RegisterPanel r6 = new RegisterPanel(6, "R6 (SP)");
        RegisterPanel r7 = new RegisterPanel(7, "R7 (PC)");

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

        JPanel innerPanel = createInnerPanel(registersPanel, executionWrapper,
            createRightPanel(conditionsPanel, buttons));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(innerPanel);

        Dimension size = getPreferredSize();
        setPreferredSize(new Dimension(314, size.height));
    }

    private JPanel createInnerPanel(JPanel registersPanel, JPanel executionWrapper2, JPanel rightPanel) {
        JPanel panel = new JPanel();
        GridBagLayout grid = new GridBagLayout();
        panel.setLayout(grid);
        grid.columnWidths = new int[] { 177, 0 };
        grid.columnWeights = new double[] { 0.0, 0.0 };
        grid.rowHeights = new int[] { 0, 0 };
        grid.rowWeights = new double[] { 0.0, 0.0 };

        GridBagConstraints c_0 = new GridBagConstraints();
        c_0.gridwidth = 2;
        c_0.gridx = 0;
        c_0.gridy = 0;
        c_0.insets = new Insets(0, 0, 4, 0);
        c_0.anchor = GridBagConstraints.CENTER;
        c_0.fill = GridBagConstraints.HORIZONTAL;
        panel.add(registersPanel, c_0);

        GridBagConstraints c_1 = new GridBagConstraints();
        c_1.gridx = 0;
        c_1.gridy = 1;
        c_1.anchor = GridBagConstraints.WEST;
        c_1.fill = GridBagConstraints.BOTH;
        panel.add(executionWrapper, c_1);

        GridBagConstraints c_2 = new GridBagConstraints();
        c_2.gridx = 1;
        c_2.gridy = 1;
        c_2.insets = new Insets(0, 3, 0, 0);
        c_2.anchor = GridBagConstraints.NORTHWEST;
        c_2.fill = GridBagConstraints.BOTH;
        panel.add(rightPanel, c_2);
        return panel;
    }

    private JPanel createRightPanel(ConditionsPanel conditionsPanel2, ButtonPanel buttons2) {
        JPanel panel = new JPanel();
        GridBagLayout innerGrid = new GridBagLayout();
        panel.setLayout(innerGrid);
        innerGrid.columnWeights = new double[] { 1.0 };
        innerGrid.columnWidths = new int[] { 0 };
        innerGrid.rowWeights = new double[] { 1.0, 1.0 };
        innerGrid.rowHeights = new int[] { 0, 0 };
        GridBagConstraints ic_0 = new GridBagConstraints();
        ic_0.gridx = 0;
        ic_0.gridy = 0;
        ic_0.anchor = GridBagConstraints.NORTH;
        ic_0.fill = GridBagConstraints.BOTH;
        panel.add(conditionsPanel, ic_0);
        GridBagConstraints ic_1 = new GridBagConstraints();
        ic_1.gridx = 0;
        ic_1.gridy = 1;
        ic_1.anchor = GridBagConstraints.SOUTH;
        ic_1.fill = GridBagConstraints.BOTH;
        panel.add(buttons, ic_1);
        return panel;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        for (RegisterPanel panel : getRegisters()) {
            panel.addPropertyChangeListener(listener);
        }
    }

    public void addActionListener(ActionListener listener) {
        buttons.addActionListener(listener);
    }

    public RegisterPanel[] getRegisters() {
        return registers;
    }

    public ConditionsPanel getConditions() {
        return conditionsPanel;
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
