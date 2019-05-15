package cesar2.cpu;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

import cesar2.util.Pair;
import cesar2.util.Shorts;


public class Memory {
    private byte[] data;
    private static final int DISPLAY_START_ADDRESS = 65500;
    private static final int DISPLAY_END_ADDRESS = 65535;
    private long bytesRead;
    private long bytesWritten;
    private final PropertyChangeSupport support;
    private boolean hasDisplayChanged;

    public Memory(byte[] data) {
        this.data = data;
        this.bytesRead = 0;
        this.bytesWritten = 0;
        this.support = new PropertyChangeSupport(this);
        this.hasDisplayChanged = true;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public int size() {
        return data.length;
    }

    private static boolean isDisplayAddress(short address) {
        int uAddress = Shorts.toUnsignedInt(address);
        return uAddress >= DISPLAY_START_ADDRESS && uAddress <= DISPLAY_END_ADDRESS;
    }

    public boolean hasDisplayChanged() {
        return hasDisplayChanged;
    }

    public void setBytes(byte[] newData) {
        int dataSize = size();
        int size = Math.min(newData.length, dataSize);
        int offset = newData.length > size() ? newData.length - dataSize : 0;
        for (int i = 0; i < size; ++i) {
            data[i] = newData[i + offset];
        }
        support.firePropertyChange("Memory.data", null, null);
    }

    public void setValue(short address, byte value) {
        int index = Shorts.toUnsignedInt(address);
        data[index] = value;

        if (isDisplayAddress(address)) {
            Pair<Integer, Byte> pair = new Pair<>(index - DISPLAY_START_ADDRESS, value);
            support.firePropertyChange("Memory.displayMemory", null, pair);
        }
    }

    public byte readByte(short address, boolean countAccess) {
        if (countAccess) {
            ++bytesRead;
        }
        return data[Shorts.toUnsignedInt(address)];
    }

    public byte readByte(short address) {
        return readByte(address, true);
    }

    public short readWord(short address) {
        byte msb;
        byte lsb;

        if (isDisplayAddress(address)) {
            msb = (byte) 0;
            lsb = readByte(address);
        }
        else {
            msb = readByte(address);
            lsb = readByte((short) (address + 1));
        }
        return Memory.bytesToShort(msb, lsb);
    }

    public void writeByte(short address, byte value) {
        ++bytesWritten;

        int index = Shorts.toUnsignedInt(address);
        data[index] = value;

        {
            Pair<Short, Byte> pair = new Pair<>(address, value);
            support.firePropertyChange("Memory.value", null, pair);
        }

        if (isDisplayAddress(address)) {
            Pair<Integer, Byte> pair = new Pair<>(index - DISPLAY_START_ADDRESS, value);
            support.firePropertyChange("Memory.displayMemory", null, pair);
        }
    }

    public void writeWord(short address, short value) {
        byte msb = (byte) ((0xFF00 & value) >> 8);
        byte lsb = (byte) (0x00FF & value);

        if (isDisplayAddress(address)) {
            // Se estivermos escrevendo uma palavra num endereço que pertence ao display,
            // apenas o byte menos significativo será escrito no endereço fornecido.
            writeByte(address, lsb);
        }
        else {
            writeByte(address, msb);
            writeByte((short) (address + 1), lsb);
        }
    }

    public byte[] getDisplayBytes() {
        return Arrays.copyOfRange(data, DISPLAY_START_ADDRESS, DISPLAY_END_ADDRESS + 1);
    }

    public long getNumberOfBytesRead() {
        return bytesRead;
    }

    public long getNumberOfBytesWritten() {
        return bytesWritten;
    }

    public long getNumberOfMemoryAccesses() {
        return getNumberOfBytesRead() + getNumberOfBytesWritten();
    }

    public static short bytesToShort(byte a, byte b) {
        return (short) (((0xFF & a) << 8) | (0xFF & b));
    }

}
