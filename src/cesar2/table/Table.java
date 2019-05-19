package cesar2.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import cesar2.util.Bytes;
import cesar2.util.Shorts;

@SuppressWarnings("serial")
public abstract class Table extends JTable {
    private static final long serialVersionUID = 8957941865210473254L;

    private static DefaultTableCellRenderer pcColumnRenderer;
    private static DefaultTableCellRenderer decimalByteRenderer;
    private static DefaultTableCellRenderer decimalShortRenderer;
    private static DefaultTableCellRenderer hexadecimalByteRenderer;
    private static DefaultTableCellRenderer hexadecimalShortRenderer;
    private static DefaultTableCellRenderer defaultTableRenderer;

    static {
        pcColumnRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 4346935574861281970L;

            private final Color selectedColor = new Color(0x00FF00);
            private final Color unselectedColor = new Color(0x007F00);
            private final Font font = new Font(Font.MONOSPACED, Font.BOLD, 16);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(font);
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(isSelected ? selectedColor : unselectedColor);
                return this;
            }
        };
        pcColumnRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        pcColumnRenderer.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));

        decimalByteRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(Integer.toString(Bytes.toUnsignedInt((byte) value)));
            }
        };
        decimalByteRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        decimalShortRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(Integer.toString(Shorts.toUnsignedInt((short) value)));
            }
        };
        decimalShortRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        hexadecimalByteRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(Integer.toHexString(Bytes.toUnsignedInt((byte) value)));
            }
        };
        hexadecimalByteRenderer.setHorizontalAlignment(SwingConstants.RIGHT);


        hexadecimalShortRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(Integer.toHexString(Shorts.toUnsignedInt((short) value)));
            }
        };
        hexadecimalShortRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        defaultTableRenderer = new DefaultTableCellRenderer();
    }

    private boolean isDecimal;

    private Table(TableModel model) {
        super(model);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        isDecimal = true;

        Font headerFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

        JTableHeader header = getTableHeader();
        header.setFont(headerFont);
        header.setReorderingAllowed(false);
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int col) {
        Class<?> c = getColumnClass(col);

        if (c == Byte.class) {
            return isDecimal() ? decimalByteRenderer : hexadecimalByteRenderer;
        }

        if (c == Short.class) {
            return isDecimal() ? decimalShortRenderer : hexadecimalShortRenderer;
        }

        if (c == Character.class) {
            return pcColumnRenderer;
        }

        return defaultTableRenderer;
    }


    public void setDecimal(boolean isDecimal) {
        this.isDecimal = isDecimal;
        ((AbstractTableModel) getModel()).fireTableDataChanged();
    }

    public boolean isDecimal() {
        return isDecimal;
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return getModel().getColumnClass(col);
    }

    public void scrollToRow(int row) {
        scrollToRow(row, false);
    }

    public void scrollToRow(int rowNumber, boolean top) {
        int rowHeight = getRowHeight();
        Rectangle rect;
        if (top) {
            int parentHeight = getParent().getHeight();
            rect = new Rectangle(0, (rowNumber - 1) * rowHeight + parentHeight, getWidth(), rowHeight);
        }
        else {
            rect = new Rectangle(0, rowNumber * rowHeight, getWidth(), rowHeight);
        }
        scrollRectToVisible(rect);
    }

    abstract public byte getValue(int index);

    abstract public void setValue(int index, byte value);

    public static class ProgramTable extends Table {
        ProgramModel model;

        public ProgramTable(ProgramModel model) {
            super(model);
            this.model = model;
            TableColumnModel cModel = getColumnModel();
            TableColumn col;
            col = cModel.getColumn(0);
            col.setMaxWidth(22);
            col.setResizable(false);

            col = cModel.getColumn(1);
            col.setMaxWidth(60);
            col.setResizable(false);

            col = cModel.getColumn(2);
            col.setMaxWidth(40);
            col.setResizable(false);
        }

        public void setProgramCounterRow(int rowIndex) {
            model.setProgramCounterRow(rowIndex);
            scrollToRow(rowIndex);
            setRowSelectionInterval(rowIndex, rowIndex);
        }

        @Override
        public byte getValue(int rowIndex) {
            return (byte) model.getValueAt(rowIndex, 2);
        }

        @Override
        public void setValue(int rowIndex, byte value) {
            model.setValueAt(value, rowIndex, 2);
            model.fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }

    public static class DataTable extends Table {
        DataModel model;

        public DataTable(DataModel model) {
            super(model);
            this.model = model;
            TableColumnModel cModel = getColumnModel();
            TableColumn col;
            col = cModel.getColumn(0);
            col.setMaxWidth(62);
//            col.setResizable(false);

            col = cModel.getColumn(1);
            col.setMaxWidth(62);
//            col.setResizable(false);
        }

        @Override
        public byte getValue(int index) {
            return (byte) getModel().getValueAt(index, 1);
        }

        @Override
        public void setValue(int rowIndex, byte value) {
            AbstractTableModel model = (AbstractTableModel) getModel();
            model.setValueAt(value, rowIndex, 1);
            model.fireTableRowsUpdated(rowIndex, rowIndex);
        }

    }
}
