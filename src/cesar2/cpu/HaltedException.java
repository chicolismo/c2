package cesar2.cpu;

public class HaltedException extends Exception {

    private static final long serialVersionUID = 5278555095892751972L;

    private HaltedException(String message) {
        super(message);
    }

    public static HaltedException withInvalidAddressMode(int code) {
        String message = String.format("Modo de endereçamento inválido: %d (0b%s)", code, Integer.toBinaryString(code));
        return new HaltedException(message);
    }

    public static HaltedException withInvalidOpCode(int code) {
        String message = String.format("Opcode inválido: %d (0b%s)", code, Integer.toBinaryString(code));
        return new HaltedException(message);
    }

    public static HaltedException withHalt() {
        String message = "Atingiu HLT";
        return new HaltedException(message);
    }

}
