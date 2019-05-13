package cesar2.table;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import cesar2.util.Bytes;
import cesar2.util.Shorts;

@SuppressWarnings("serial")
public abstract class Table extends JTable {
    private static final long serialVersionUID = 8957941865210473254L;

    private static DefaultTableCellRenderer bigRenderer;
    private static DefaultTableCellRenderer decimalByteRenderer;
    private static DefaultTableCellRenderer decimalShortRenderer;
    private static DefaultTableCellRenderer hexadecimalByteRenderer;
    private static DefaultTableCellRenderer hexadecimalShortRenderer;
    private static DefaultTableCellRenderer defaultTableRenderer;

    static {
        bigRenderer = new DefaultTableCellRenderer();
        bigRenderer.setHorizontalAlignment(JLabel.CENTER);
        bigRenderer.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));

        decimalByteRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(Integer.toString(Bytes.toUnsignedInt((byte) value)));
            }
        };
        decimalByteRenderer.setHorizontalAlignment(JLabel.RIGHT);

        decimalShortRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(Integer.toString(Shorts.toUnsignedInt((short) value)));
            }
        };
        decimalShortRenderer.setHorizontalAlignment(JLabel.RIGHT);

        hexadecimalByteRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(Integer.toHexString(Bytes.toUnsignedInt((byte) value)));
            }
        };
        hexadecimalByteRenderer.setHorizontalAlignment(JLabel.RIGHT);


        hexadecimalShortRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(Integer.toHexString(Shorts.toUnsignedInt((short) value)));
            }
        };
        hexadecimalShortRenderer.setHorizontalAlignment(JLabel.RIGHT);

        defaultTableRenderer = new DefaultTableCellRenderer();
    }

    private boolean isDecimal;

    private Table(TableModel model) {
        super(model);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        isDecimal = true;
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
            return bigRenderer;
        }

        return defaultTableRenderer;
    }


    public void setDecimal(boolean isDecimal) {
        this.isDecimal = isDecimal;
    }

    public boolean isDecimal() {
        return isDecimal;
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return getModel().getColumnClass(col);
    }

    abstract public byte getValue(int index);

    abstract public void setValue(int index, byte value);

    public static Table of(final TableModel model) {
        Table table = null;

        if (model instanceof ProgramModel) {
            table = new Table(model) {
                @Override
                public byte getValue(int index) {
                    return (byte) getModel().getValueAt(index, 2);
                }

                @Override
                public void setValue(int index, byte value) {
                    getModel().setValueAt(value, index, 2);
                }
            };
        }
        else if (model instanceof DataModel) {
            table = new Table(model) {
                @Override
                public byte getValue(int index) {
                    return (byte) getModel().getValueAt(index, 1);
                }

                @Override
                public void setValue(int index, byte value) {
                    getModel().setValueAt(value, index, 1);
                }
            };
        }
        return table;
    }
}
