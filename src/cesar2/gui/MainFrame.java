package cesar2.gui;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import cesar2.cpu.Cpu;
import cesar2.cpu.Memory;
import cesar2.table.DataModel;
import cesar2.table.ProgramModel;
import cesar2.table.Table;
import cesar2.util.Pair;

public class MainFrame extends JFrame implements PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    private static final int MEMORY_SIZE = 1 << 16;

    private final byte[] data;
    private final Memory memory;
    private final Cpu cpu;
    private final Table programTable;
    private final Table dataTable;
    private final SidePanel programPanel;
    private final SidePanel dataPanel;

    public MainFrame() {
        super("Cesar");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(true);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);

        data = new byte[MEMORY_SIZE];
        Arrays.fill(data, (byte) 0);

        memory = new Memory(data);
        cpu = new Cpu(memory);

        programTable = Table.of(new ProgramModel(data));
        dataTable = Table.of(new DataModel(data));

        programPanel = new SidePanel(this, programTable);
        dataPanel = new SidePanel(this, dataTable);

        programTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    int row = programTable.getSelectedRow();
                    if (row != -1) {
                        programPanel.getSupport().firePropertyChange("ProgramTable.programCounter", null, (short) row);
                    }
                }
            }
        });

        cpu.addPropertyChangeListener(this);
        memory.addPropertyChangeListener(this);
        programPanel.addPropertyChangeListener(this);
        dataPanel.addPropertyChangeListener(this);

        MainPanel mainPanel = new MainPanel();
        for (RegisterPanel panel : mainPanel.getRegisters()) {
            panel.addPropertyChangeListener(this);
        }

        add(mainPanel);

        pack();
        center();
        updatePanels();
        programPanel.setVisible(true);
        dataPanel.setVisible(true);

        programPanel.setFocusableWindowState(false);
        dataPanel.setFocusableWindowState(false);
        initEvents();
    }

    private void center() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        int x = (int) ((screenSize.getWidth() - windowSize.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - windowSize.getHeight()) / 2);
        setLocation(x, y);
    }

    private void updatePanels() {
        int spacing = 20;
        int width = getWidth();
        Point point = getLocation();

        programPanel.setLocation(point.x - programPanel.getWidth() - spacing, point.y);
        dataPanel.setLocation(point.x + width + spacing, point.y);
    }

    private void initEvents() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent event) {
                updatePanels();
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String name = event.getPropertyName();

        if ("ProgramTable.programCounter".equals(name)) {
            // Uma linha da tabela de programa sofreu clique duplo e temos que alterar o
            // valor do PC na CPU
            cpu.setPc((short) event.getNewValue());
        }
        else if ("Cpu.programCounter".equals(name)) {
            ((ProgramModel) programTable.getModel()).setProgramCounterRow((int) event.getNewValue());
        }
        else if ("Memory.data".equals(name)) {
            // Um byte foi escrito na memória
            Pair<Short, Byte> pair = (Pair<Short, Byte>) event.getNewValue();
            short address = pair.getKey();
            byte value = pair.getValue();
            programTable.setValue(address, value);
            dataTable.setValue(address, value);
        }
        else if ("RegisterPanel.value".equals(name)) {
            Pair<Integer, Short> pair = (Pair<Integer, Short>) event.getNewValue();
            cpu.setRegister(pair.getKey(), pair.getValue());
        }
    }

}
