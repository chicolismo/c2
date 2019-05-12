package cesar2.gui;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import cesar2.table.DataModel;
import cesar2.table.ProgramModel;
import cesar2.table.Table;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final int MEMORY_SIZE = 1 << 16;

    private final byte[] memory;
    private final Table programTable;
    private final Table dataTable;
    private final SidePanel programPanel;
    private final SidePanel dataPanel;

    public MainFrame() {
        super("Cesar");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(true);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);

        memory = new byte[MEMORY_SIZE];
        Arrays.fill(memory, (byte) 0);

        programTable = Table.of(new ProgramModel(memory));
        dataTable = Table.of(new DataModel(memory));

        programPanel = new SidePanel(this, programTable);
        dataPanel = new SidePanel(this, dataTable);

        MainPanel mainPanel = new MainPanel();
        add(mainPanel);

        pack();
        center();
        updatePanels();
        programPanel.setVisible(true);
        dataPanel.setVisible(true);
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
}
