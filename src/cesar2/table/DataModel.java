package cesar2.table;

import javax.swing.table.AbstractTableModel;

public class DataModel extends AbstractTableModel {
    private static final long serialVersionUID = -4011299022804124398L;
    private static final Class<?>[] COLUMN_CLASSES = new Class<?>[] { Short.class, Byte.class };
    private static final String[] COLUMN_NAMES = new String[] { "Endereço", "Dado" };

    private final byte[] data;

    public DataModel(byte[] data) {
        this.data = data;
    }

    @Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return COLUMN_CLASSES[col];
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        switch (columnIndex) {
        case 0:
            value = (short) rowIndex;
            break;
        case 1:
            value = data[rowIndex];
        }
        return value;
    }

}
