package cesar2.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cesar2.gui.display.DigitalDisplay;

public class ExecutionPanel extends JPanel {
    private static final long serialVersionUID = 5547262475928173278L;

    private DigitalDisplay accessCount;
    private DigitalDisplay instructionCount;

    public ExecutionPanel() {
        accessCount = new DigitalDisplay();
        instructionCount = new DigitalDisplay();

        JLabel accessLabel = new JLabel("Acessos:");
        JLabel instructionLabel = new JLabel("Instruções:");

        GridBagLayout grid = new GridBagLayout();
        setLayout(grid);

        grid.columnWidths = new int[] { 0, 4, 0 };
        grid.columnWeights = new double[] { 1.0, 0.0, 0.0 };
        grid.rowWeights = new double[] { 0.0, 0.0 };

        GridBagConstraints c_0 = new GridBagConstraints();
        c_0.gridx = 0;
        c_0.gridy = 0;
        c_0.anchor = GridBagConstraints.EAST;

        add(accessLabel, c_0);

        GridBagConstraints c_1 = new GridBagConstraints();
        c_1.gridx = 0;
        c_1.gridy = 1;
        c_1.anchor = GridBagConstraints.EAST;
        add(instructionLabel, c_1);

        GridBagConstraints c_2 = new GridBagConstraints();
        c_2.gridx = 2;
        c_2.gridy = 0;
        c_2.anchor = GridBagConstraints.WEST;
        add(accessCount, c_2);

        GridBagConstraints c_3 = new GridBagConstraints();
        c_3.gridx = 2;
        c_3.gridy = 1;
        c_3.anchor = GridBagConstraints.WEST;
        add(instructionCount, c_3);

    }

}
