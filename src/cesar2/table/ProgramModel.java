package cesar2.table;

import javax.swing.table.AbstractTableModel;

public class ProgramModel extends AbstractTableModel {
    private static final long serialVersionUID = 5876130526129925091L;
    private static final Class<?>[] COLUMN_CLASSES = new Class<?>[] { Character.class, Short.class, Byte.class,
        String.class };
    private static final String[] COLUMN_NAMES = new String[] { "PC", "Endereço", "Dado", "Mnemônico" };
    private int pcRow;
    private final byte[] data;

    private static final Character PC_CHAR = '➔';
    private static final Character NOT_PC_CHAR = ' ';

    public ProgramModel(byte[] data) {
        this.pcRow = 0;
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
        return 4;
    }

    public void setProgramCounterRow(int row) {
        int oldValue = pcRow;
        pcRow = row;
        fireTableRowsUpdated(oldValue, oldValue);
        fireTableRowsUpdated(row, row);
    }

    public int getProgramCounterRow() {
        return pcRow;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        switch (columnIndex) {
        case 0:
            value = rowIndex == pcRow ? PC_CHAR : NOT_PC_CHAR;
            break;
        case 1:
            value = (short) rowIndex;
            break;
        case 2:
            value = data[rowIndex];
            break;
        case 3:
            value = "";
        }
        return value;
    }

}
