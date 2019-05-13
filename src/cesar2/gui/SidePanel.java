package cesar2.gui;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cesar2.table.Table;
import cesar2.util.Pair;

public class SidePanel extends JDialog {
    private static final long serialVersionUID = 598118447667354371L;

    private boolean isDecimal;
    private short address;
    private byte value;

    private final PropertyChangeSupport support;
    private final Table table;
    private final JLabel label;
    private final JTextField textField;

    public SidePanel(JFrame owner, Table table) {
        super(owner);
        setType(Window.Type.UTILITY);
        setModalityType(Dialog.ModalityType.MODELESS);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        this.support = new PropertyChangeSupport(this);

        this.table = Objects.requireNonNull(table);
        isDecimal = true;
        address = 0;
        value = 0;
        label = new JLabel("0");
        textField = new JTextField("0", 6);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);

        JPanel panel = new JPanel(true);
        panel.setBorder(new EmptyBorder(4, 4, 4, 4));
        setContentPane(panel);

        JPanel innerPanel = new JPanel(true);
        innerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        innerPanel.add(label);
        innerPanel.add(textField);


        GridBagLayout grid = new GridBagLayout();
        grid.columnWidths = new int[] { 0 };
        grid.rowHeights = new int[] { 0, 0 };
        grid.columnWeights = new double[] { 1.0 };
        grid.rowWeights = new double[] { 1.0, 0.0 };

        panel.setLayout(grid);

        GridBagConstraints c_0 = new GridBagConstraints();
        c_0.gridx = 0;
        c_0.gridy = 0;
        c_0.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, c_0);

        GridBagConstraints c_1 = new GridBagConstraints();
        c_1.anchor = GridBagConstraints.EAST;
        c_1.fill = GridBagConstraints.HORIZONTAL;
        c_1.gridx = 0;
        c_1.gridy = 3;
        panel.add(innerPanel, c_1);

        initEvents();
        pack();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public PropertyChangeSupport getSupport() {
        return support;
    }

    private void initEvents() {
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    value = table.getValue(row);
                    address = (short) row;
                    label.setText(Integer.toString(row, isDecimal ? 10 : 16));
                    textField.setText(Integer.toString(value, isDecimal ? 10 : 16));
                }
            }

        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                byte newValue;
                try {
                    newValue = (byte) Integer.parseInt(textField.getText(), isDecimal ? 10 : 16);
                }
                catch (NumberFormatException e) {
                    newValue = (byte) 0;
                    textField.setText("0");
                }
                Pair<Short, Byte> oldPair = new Pair<>(address, value);
                Pair<Short, Byte> newPair = new Pair<>(address, newValue);
                support.firePropertyChange("rowValue", oldPair, newPair);
            }
        });
    }

}
