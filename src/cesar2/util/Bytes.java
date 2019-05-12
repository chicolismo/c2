package cesar2.util;

public class Bytes {

    private Bytes() {
    }

    public static int toUnsignedInt(byte value) {
        return 0xFF & (value);
    }

}
