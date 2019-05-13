package cesar2;

import java.util.Objects;

import cesar2.util.Shorts;

public class Alu {
    private static final short MSB = (short) 0b1000_0000_0000_0000;
    private static final short LSB = 1;

    private final ConditionRegister register;


    public Alu(ConditionRegister register) {
        this.register = Objects.requireNonNull(register);
    }

    private boolean isOverflow(short a, short b, short c) {
        return (a > 0 && b > 0 && c < 0) || (a < 0 && b < 0 && c > 0);
    }

    private boolean isCarryAdd(short a, short b) {
        return Shorts.toUnsignedInt(a) + Shorts.toUnsignedInt(b) > 0xFFFF;
    }

    private boolean isCarrySub(short a, short b) {
        return Shorts.toUnsignedInt(a) - Shorts.toUnsignedInt(b) < 0;
    }

    /**
     * Zera o operando.
     * <p>
     * N = t, Z = t, V = 0, C = 0
     * <p>
     * Testa as condições N e Z, e zera as condições V e C.
     *
     * @param operand
     * @return 0
     */
    private short clr(short operand) {
        register.setNegative(false);
        register.setZero(true);
        register.setCarry(false);
        register.setOverflow(false);
        return (short) 0;
    }

    /**
     * Nega o operando.
     * <p>
     * N = t, Z = t, C = 1, O = 0
     *
     * @param operand
     * @return ~(operand)
     */
    private short not(short operand) {
        operand = (short) ~operand;
        register.setNegative(operand < 0);
        register.setZero(operand == 0);
        register.setCarry(true);
        register.setOverflow(false);
        return operand;
    }

    /**
     * Incrementa o operando.
     *
     * @param operand
     * @return operand + 1
     */
    private short inc(short operand) {
        short newValue = (short) (operand + 1);
        register.setNegative(newValue < 0);
        register.setZero(newValue == 0);
        register.setCarry(isCarryAdd(operand, (short) 1));
        register.setOverflow(isOverflow(operand, (short) 1, newValue));
        return newValue;
    }

    private short dec(short operand) {
        short newValue = (short) (operand - 1);
        register.setNegative(newValue < 0);
        register.setZero(newValue == 0);
        register.setCarry(isCarrySub(operand, (short) 1));
        register.setOverflow(isOverflow(operand, (short) -1, newValue));
        return newValue;
    }

    private short neg(short operand) {
        short newValue = (short) (~operand + 1);
        register.setZero(newValue == 0);
        register.setNegative(newValue < 0);
        register.setCarry(isCarrySub((short) ~operand, (short) 1));
        register.setOverflow(isOverflow((short) ~operand, (short) 1, newValue));
        return newValue;
    }

    private void tst(short operand) {
        register.setZero(operand == 0);
        register.setNegative(operand < 0);
        register.setCarry(false);
        register.setOverflow(false);
    }

    private short ror(short operand) {
        int lsb = operand & LSB;
        operand = (short) (operand >> 1);
        boolean carry = lsb == 1;
        if (carry) {
            operand = (short) (MSB | operand);
        }
        boolean negative = operand < 0;
        register.setZero(operand == 0);
        register.setNegative(negative);
        register.setCarry(carry);
        register.setOverflow(negative ^ carry);
        return operand;
    }

    private short rol(short operand) {
        int msb = operand & MSB;
        operand = (short) (operand << 1);
        boolean carry = msb == MSB;
        if (carry) {
            operand = (short) (LSB | operand);
        }
        boolean negative = operand < 0;
        register.setZero(operand == 0);
        register.setNegative(negative);
        register.setCarry(carry);
        register.setOverflow(negative ^ carry);
        return operand;
    }

    private short asr(short operand) {
        short msb = (short) (MSB & operand);
        short lsb = (short) (LSB & operand);
        boolean carry = lsb == LSB;
        boolean negative = msb == MSB;
        operand = (short) (operand >> 1);
        if (negative) {
            operand = (short) (MSB | operand);
        }
        register.setZero(operand == 0);
        register.setNegative(negative);
        register.setCarry(carry);
        register.setOverflow(negative ^ carry);
        return operand;
    }

    // TODO: Perguntar sobre isto.
    private short asl(short operand) {
        operand = (short) (operand << 1);
        short msb = (short) (MSB & operand);
        boolean negative = msb == MSB;
        boolean carry = negative;
        /*
         * if (negative) { operand = (short) (MSB | operand); }
         */
        register.setZero(operand == 0);
        register.setNegative(negative);
        register.setCarry(carry);
        register.setOverflow(negative ^ carry);
        return operand;
    }

    private short adc(short operand) {
        boolean carry = register.isCarry();
        short newValue = operand;
        if (carry) {
            newValue = (short) (operand + 1);
        }
        register.setZero(newValue == 0);
        register.setNegative(newValue < 0);
        register.setCarry(carry && isCarryAdd(operand, (short) 1));
        register.setOverflow(carry && isOverflow(operand, (short) 1, newValue));
        return newValue;
    }

    private short sbc(short operand) {
        boolean carry = register.isCarry();
        short newValue = operand;
        if (carry) {
            newValue = (short) (operand - 1);
        }
        register.setZero(newValue == 0);
        register.setNegative(newValue < 0);
        register.setCarry(carry && isCarrySub(operand, (short) 1));
        register.setOverflow(carry && isOverflow(operand, (short) -1, newValue));
        return newValue;
    }

    public short executeInstruction(int instructionCode, short operand) {
        switch (instructionCode) {
        case 0: /* CLR: op <- 0 */
            operand = clr(operand);
            break;
        case 1: /* NOT: op <- NOT(op) */
            operand = not(operand);
            break;
        case 2: /* INC: op <- op + 1 */
            operand = inc(operand);
            break;
        case 3: /* DEC: op <- op - 1 */
            operand = dec(operand);
            break;
        case 4: /* NEG: op <- -op */
            operand = neg(operand);
            break;
        case 5: /* TST: op <- op */
            tst(operand);
            break;
        case 6: /* ROR: op <- SHR(c & op) */
            operand = ror(operand);
            break;
        case 7: /* ROL: op <- SHL(op & c) */
            operand = rol(operand);
            break;
        case 8: /* ASR: op <- SHR(msb & op) */
            operand = asr(operand);
            break;
        case 9: /* ASL: op <- SHL(op & 0) */
            operand = asl(operand);
            break;
        case 10: /* ADC: op <- op + c */
            operand = adc(operand);
            break;
        case 11: /* SBC: op <- op - c */
            operand = sbc(operand);
            break;
        default:
            System.err.println("Operação inválida");
        }
        return operand;
    }

    public short executeInstruction(int code, short src, short dst) {
        short result = 0;
        switch (code) {
        case 1: /* mov */
            result = mov(src, dst);
            break;
        case 2: /* add */
            result = add(src, dst);
            break;
        case 3: /* sub */
            result = sub(src, dst);
            break;
        case 4: /* cmp */
            cmp(src, dst);
            break;
        case 5: /* and */
            result = and(src, dst);
            break;
        case 6: /* or */
            result = or(src, dst);
            break;
        default:
            // TODO: Avisar sobre operação inválida
            System.err.println("Operação inválida");
        }
        return result;
    }

    /**
     * Envia o valor de dst para src N = t Z = t V = 0 C = -
     *
     * @param src
     * @param dst
     * @return src
     */
    private short mov(short src, short dst) {
        register.setZero(src == 0);
        register.setNegative(src < 0);
        register.setOverflow(false);
        return src;
    }

    /**
     * Soma dst + src
     * <p>
     * N = t, Z = t, V = t, C = t
     *
     * @param src
     * @param dst
     * @return dst + src
     */
    private short add(short src, short dst) {
        short result = (short) (dst + src);
        register.setNegative(result < 0);
        register.setZero(result == 0);
        register.setCarry(isCarryAdd(dst, src));
        register.setOverflow(isOverflow(dst, src, result));
        return result;
    }

    /**
     * Subtrai dst - src
     * <p>
     * N = t, Z = t, V = t, C = not(t)
     *
     * @param src
     * @param dst
     * @return dst - src;
     */
    private short sub(short src, short dst) {
        short result = (short) (dst - src);
        register.setNegative(result < 0);
        register.setZero(result == 0);
        register.setCarry(isCarrySub(dst, src));
        register.setOverflow(isOverflow(dst, (short) -src, result));
        return result;
    }

    /**
     * Compara src - dst
     * <p>
     * N = t, Z = t, V = t, C = not(t)
     *
     * @param src
     * @param dst
     */
    private void cmp(short src, short dst) {
        short result = (short) (src - dst);
        register.setNegative(result < 0);
        register.setZero(result == 0);
        register.setCarry(isCarrySub(src, dst));
        register.setOverflow(isOverflow(src, (short) -dst, result));
    }

    /**
     * Realiza AND entre os bits de src e dst.
     * <p>
     * N = t, Z = t, V = 0, C = -
     *
     * @param src
     * @param dst
     * @return dst & src
     */
    private short and(short src, short dst) {
        short result = (short) (dst & src);
        register.setNegative(result < 0);
        register.setZero(result == 0);
        register.setOverflow(false);
        return result;
    }

    /**
     * Realiza OR entre os bits de src e dst.
     * <p>
     * N = t, Z = t, V = 0, C = -
     *
     * @param src
     * @param dst
     * @return dst | src
     */
    private short or(short src, short dst) {
        short result = (short) (dst | src);
        register.setNegative(result < 0);
        register.setZero(result == 0);
        register.setOverflow(false);
        return result;
    }
}
