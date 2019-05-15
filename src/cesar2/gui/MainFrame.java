package cesar2.gui;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

import cesar2.cpu.Cpu;
import cesar2.cpu.HaltedException;
import cesar2.cpu.Memory;
import cesar2.gui.display.LedDisplay;
import cesar2.table.DataModel;
import cesar2.table.ProgramModel;
import cesar2.table.Table;
import cesar2.util.Pair;
import cesar2.util.Shorts;

public class MainFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final int MEMORY_SIZE = 1 << 16;
    private boolean isRunning;
    private final JFileChooser fileChooser;

    private final byte[] data;
    private final Memory memory;
    private final Cpu cpu;
    private final MainPanel mainPanel;
    private final Table.ProgramTable programTable;
    private final Table.DataTable dataTable;
    private final RegisterPanel[] registers;
    private final LedDisplay negativeDisplay;
    private final LedDisplay zeroDisplay;
    private final LedDisplay overflowDisplay;
    private final LedDisplay carryDisplay;
    private final SidePanel programPanel;
    private final SidePanel dataPanel;
    private final DisplayPanel displayPanel;
    private final MenuBar menuBar;
    private final JPanel statusBar;
    private final JLabel statusBarText;
    private final JToggleButton btnDecimal;
    private final JToggleButton btnHexadecimal;
    private final JToggleButton btnRun;
    private final JButton btnNext;

    public MainFrame() {
        super("Cesar");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(true);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        setFocusable(true);

        JPanel contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        isRunning = false;
        data = new byte[MEMORY_SIZE];
        Arrays.fill(data, (byte) 0);

        memory = new Memory(data);
        cpu = new Cpu(memory);

        programTable = new Table.ProgramTable(new ProgramModel(data));
        programPanel = new SidePanel(this, programTable);
        programPanel.setTitle("Programa");

        dataTable = new Table.DataTable(new DataModel(data));
        dataPanel = new SidePanel(this, dataTable);
        dataPanel.setTitle("Dados");

        displayPanel = new DisplayPanel(this);
        displayPanel.setTitle("Mostrador");

        statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusBarText = new JLabel("Bem-vindos!");
        statusBar.add(statusBarText);

        mainPanel = new MainPanel();
        registers = mainPanel.getRegisters();
        ConditionsPanel conditions = mainPanel.getConditions();
        negativeDisplay = conditions.getNegative();
        zeroDisplay = conditions.getZero();
        overflowDisplay = conditions.getOverflow();
        carryDisplay = conditions.getCarry();
        ButtonPanel buttons = mainPanel.getButtons();
        btnDecimal = buttons.getBtnDecimal();
        btnHexadecimal = buttons.getBtnHexadecimal();
        btnRun = buttons.getBtnRun();
        btnNext = buttons.getBtnNext();


        menuBar = new MenuBar();
        setJMenuBar(menuBar);

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos de memória do César", "mem"));
        // TODO: Remover
        fileChooser.setCurrentDirectory(new File("/Users/chico/Projects/"));

        addListeners();

        JPanel mainPanelWrapper = new JPanel();
        mainPanelWrapper
            .setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED), new EmptyBorder(4, 4, 4, 4)));
        mainPanelWrapper.setLayout(new BoxLayout(mainPanelWrapper, BoxLayout.Y_AXIS));
        mainPanelWrapper.add(mainPanel);
        contentPane.add(mainPanelWrapper, BorderLayout.CENTER);
        contentPane.add(statusBar, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        center();
        updateSidePanelsPosition();
        programPanel.setVisible(true);
        dataPanel.setVisible(true);
        displayPanel.setVisible(true);

        initEvents();
    }

    private void setDecimal(boolean isDecimal) {
        programPanel.setDecimal(isDecimal);
        dataPanel.setDecimal(isDecimal);
        for (RegisterPanel panel : registers) {
            panel.setDecimal(isDecimal);
        }
    }

    private void center() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        int x = (int) ((screenSize.getWidth() - windowSize.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - windowSize.getHeight()) / 2);
        setLocation(x, y);
    }

    private void updateSidePanelsPosition() {
        int spacing = 4;
        int width = getWidth();
        int height = getHeight();
        Point point = getLocation();
        programPanel.setLocation(point.x - programPanel.getWidth() - spacing, point.y);
        dataPanel.setLocation(point.x + width + spacing, point.y);
        displayPanel.setLocation(point.x - programPanel.getWidth() - spacing, point.y + height + spacing);

        programPanel.setSize(280, height);
        dataPanel.setSize(146, height);
    }

    private void initEvents() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent event) {
                updateSidePanelsPosition();
            }
        });
    }

    private void setStatusText(String text) {
        statusBarText.setText(text);
    }

    private void clearStatusBar() {
        statusBarText.setText("");
    }

    private void updateInterface() {
        for (int i = 0; i < 8; ++i) {
            short value = cpu.getRegister(i);
            registers[i].setValue(value);
        }
        negativeDisplay.setTurnedOn(cpu.isNegative());
        zeroDisplay.setTurnedOn(cpu.isZero());
        overflowDisplay.setTurnedOn(cpu.isOverflow());
        carryDisplay.setTurnedOn(cpu.isCarry());

        if (!isRunning()) {
            updateProgramCounterRow(cpu.getPc());
        }
    }

    private void updateProgramCounterRow(short pc) {
        programTable.setProgramCounterRow(Shorts.toUnsignedInt(pc));
    }

    private void updateDisplay() {
        displayPanel.setValue(memory.getDisplayBytes());
    }

    private synchronized boolean isRunning() {
        return isRunning;
    }

    private synchronized void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    private void executeInstruction() {
        try {
//            clearStatusBar();
            cpu.executeNextInstruction();
            updateInterface();
        }
        catch (HaltedException e) {
            setStatusText(e.getMessage());
            setRunning(false);
        }
    }

    private void run() {
        if (isRunning()) {
            setRunning(false);
        }
        else {
            setRunning(true);
            Thread runThread = new Thread() {
                @Override
                public void run() {
                    while (isRunning()) {
                        executeInstruction();
                    }
                    setRunning(false);
                    updateInterface();
                }

            };
            runThread.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String actionCommand = event.getActionCommand();

        if (actionCommand.equals("next")) {
            executeInstruction();
        }
        else if (actionCommand.equals("run")) {
            run();
        }
        else if (actionCommand.equals("decimal")) {
            setDecimal(true);
        }
        else if (actionCommand.equals("hexadecimal")) {
            setDecimal(false);
        }
    }

    private void addListeners() {
        PropertyChangeListener cpuPropertyListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                String property = event.getPropertyName();
                if ("Cpu.programCounter".equals(property)) {
                    if (!isRunning()) {
                        short pc = cpu.getPc();
                        updateProgramCounterRow(pc);
                        registers[7].setValue(pc);
                    }
                }
            }
        };

        PropertyChangeListener memoryPropertyListener = new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                String property = event.getPropertyName();
                if ("Memory.value".equals(property)) {
                    Pair<Short, Byte> pair = (Pair<Short, Byte>) event.getNewValue();
                    short address = pair.getKey();
                    byte value = pair.getValue();
                    programTable.setValue(address, value);
                    dataTable.setValue(address, value);
                }
                else if ("Memory.displayMemory".equals(property)) {
                    Pair<Integer, Byte> pair = (Pair<Integer, Byte>) event.getNewValue();
                    byte value = pair.getValue();
                    displayPanel.setValueAt(pair.getKey(), (char) value);
                }
                else if ("Memory.data".equals(property)) {
                    // Temos que notificar as tabelas sobre a alteração na memória
                    ((AbstractTableModel) programTable.getModel()).fireTableDataChanged();
                    ((AbstractTableModel) dataTable.getModel()).fireTableDataChanged();
                    updateDisplay();
                }
            }
        };

        MouseAdapter programTableMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    int row = programTable.getSelectedRow();
                    if (row != -1) {
                        programPanel.getSupport().firePropertyChange("ProgramTable.programCounter", null, (short) row);
                        programTable.scrollToRow(row);
                    }
                }
            }
        };

        PropertyChangeListener programTablePropertyListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                String property = event.getPropertyName();
                if ("ProgramTable.programCounter".equals(property)) {
                    cpu.setPc((short) event.getNewValue());
                }
            }
        };

        PropertyChangeListener sidePanelPropertyListener = new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                String property = event.getPropertyName();
                if ("SidePanel.rowValue".equals(property)) {
                    Pair<Short, Byte> pair = (Pair<Short, Byte>) event.getNewValue();
                    short address = pair.getKey();
                    byte value = pair.getValue();
                    memory.setValue(address, value);
                    int index = Shorts.toUnsignedInt(address);
                    programTable.setValue(index, value);
                    dataTable.setValue(index, value);
                }
            }
        };

        PropertyChangeListener mainPanelPropertyListener = new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                String property = event.getPropertyName();
                if ("RegisterPanel.value".equals(property)) {
                    Pair<Integer, Short> pair = (Pair<Integer, Short>) event.getNewValue();
                    int registerNumber = pair.getKey();
                    short newValue = pair.getValue();
                    if (registerNumber == 7) {
                        cpu.setPc(newValue);
                    }
                    else {
                        cpu.setRegister(registerNumber, newValue);
                    }

                }
            }
        };

        ActionListener menuBarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String command = event.getActionCommand();
                if ("load".equals(command)) {
                    loadFile();
                }
                else {
                    System.err.println("Ação não tratada: " + command);
                }
            }
        };

//        Action decimalAction = new AbstractAction() {
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                setDecimal(true);
//            }
//        };
//
//        Action hexadecimalAction = new AbstractAction() {
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                setDecimal(false);
//            }
//        };
//
//        Action runAction = new AbstractAction() {
//            private static final long serialVersionUID = 7734044798870749806L;
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                run();
//            }
//        };
//
//        Action nextAction = new AbstractAction() {
//            private static final long serialVersionUID = 7770416677796535119L;
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                executeInstruction();
//            }
//        };


        cpu.addPropertyChangeListener(cpuPropertyListener);
        memory.addPropertyChangeListener(memoryPropertyListener);
        programTable.addMouseListener(programTableMouseAdapter);
        programPanel.addPropertyChangeListener(programTablePropertyListener);
        programPanel.addPropertyChangeListener(sidePanelPropertyListener);
        dataPanel.addPropertyChangeListener(sidePanelPropertyListener);
        mainPanel.addPropertyChangeListener(mainPanelPropertyListener);
        menuBar.addActionListener(menuBarActionListener);
        btnDecimal.addActionListener(this);
        btnDecimal.setActionCommand("decimal");
        btnHexadecimal.addActionListener(this);
        btnHexadecimal.setActionCommand("hexadecimal");
        btnRun.addActionListener(this);
        btnRun.setActionCommand("run");
        btnNext.addActionListener(this);
        btnNext.setActionCommand("next");

        btnDecimal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control D"), "decimal");
        btnHexadecimal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control H"),
            "hexadecimal");

//        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
//        int mask;
//        try {
//            Method getKeyMask = Toolkit.class.getMethod("getMenuShortcutKeyMaskEx", new Class<?>[] {});
//            mask = (int) getKeyMask.invoke(defaultToolkit);
//        }
//        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
//            | InvocationTargetException e) {
//            mask = defaultToolkit.getMenuShortcutKeyMask();
//        }
//        JPanel contentPane = (JPanel) getContentPane();
//        contentPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, mask), "decimal");
//        contentPane.getActionMap().put("decimal", new AbstractAction() {
//            private static final long serialVersionUID = 7155850655627788214L;
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                btnDecimal.doClick();
//            }
//        });
    }

    private void loadFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (InputStream inputStream = new FileInputStream(file)) {
                int size = (int) file.length();
                byte[] buffer = new byte[size];
                inputStream.read(buffer, 0, size);
                memory.setBytes(buffer);
            }
            catch (IOException e) {
                String message = String.format("Erro ao ler o arquivo %s.", file.getPath());
                JOptionPane.showMessageDialog(this, message, "Erro de leitura", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /*
     * private void saveFile() { }
     * 
     * private void saveFileAs() { }
     */
}
