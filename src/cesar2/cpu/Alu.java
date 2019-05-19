package cesar2.cpu;

import cesar2.util.Shorts;

public class Alu {
    private static final short MSB = (short) 0b1000_0000_0000_0000;
    private static final short LSB = 1;

    private final Cpu cpu;
    private final ConditionRegister conditionRegister;

    public Alu(Cpu cpu) {
        this.cpu = cpu;
        // this.conditionRegister = Objects.requireNonNull(cpu.getConditionRegister());
        this.conditionRegister = cpu.getConditionRegister();
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
    short clr(short operand) {
        conditionRegister.setNegative(false);
        conditionRegister.setZero(true);
        conditionRegister.setCarry(false);
        conditionRegister.setOverflow(false);
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
    short not(short operand) {
        operand = (short) ~operand;
        conditionRegister.setNegative(operand < 0);
        conditionRegister.setZero(operand == 0);
        conditionRegister.setCarry(true);
        conditionRegister.setOverflow(false);
        return operand;
    }

    /**
     * Incrementa o operando.
     *
     * @param operand
     * @return operand + 1
     */
    short inc(short operand) {
        short newValue = (short) (operand + 1);
        conditionRegister.setNegative(newValue < 0);
        conditionRegister.setZero(newValue == 0);
        conditionRegister.setCarry(isCarryAdd(operand, (short) 1));
        conditionRegister.setOverflow(isOverflow(operand, (short) 1, newValue));
        return newValue;
    }

    short dec(short operand) {
        short newValue = (short) (operand - 1);
        conditionRegister.setNegative(newValue < 0);
        conditionRegister.setZero(newValue == 0);
        conditionRegister.setCarry(isCarrySub(operand, (short) 1));
        conditionRegister.setOverflow(isOverflow(operand, (short) -1, newValue));
        return newValue;
    }

    short neg(short operand) {
        short newValue = (short) (~operand + 1);
        conditionRegister.setZero(newValue == 0);
        conditionRegister.setNegative(newValue < 0);
        conditionRegister.setCarry(isCarrySub((short) ~operand, (short) 1));
        conditionRegister.setOverflow(isOverflow((short) ~operand, (short) 1, newValue));
        return newValue;
    }

    void tst(short operand) {
        conditionRegister.setZero(operand == 0);
        conditionRegister.setNegative(operand < 0);
        conditionRegister.setCarry(false);
        conditionRegister.setOverflow(false);
    }

    short ror(short operand) {
        int lsb = operand & LSB;
        operand = (short) (operand >> 1);
        boolean carry = lsb == 1;
        if (carry) {
            operand = (short) (MSB | operand);
        }
        boolean negative = operand < 0;
        conditionRegister.setZero(operand == 0);
        conditionRegister.setNegative(negative);
        conditionRegister.setCarry(carry);
        conditionRegister.setOverflow(negative ^ carry);
        return operand;
    }

    short rol(short operand) {
        int msb = operand & MSB;
        operand = (short) (operand << 1);
        boolean carry = msb == MSB;
        if (carry) {
            operand = (short) (LSB | operand);
        }
        boolean negative = operand < 0;
        conditionRegister.setZero(operand == 0);
        conditionRegister.setNegative(negative);
        conditionRegister.setCarry(carry);
        conditionRegister.setOverflow(negative ^ carry);
        return operand;
    }

    short asr(short operand) {
        short msb = (short) (MSB & operand);
        short lsb = (short) (LSB & operand);
        boolean carry = lsb == LSB;
        boolean negative = msb == MSB;
        operand = (short) (operand >> 1);
        if (negative) {
            operand = (short) (MSB | operand);
        }
        conditionRegister.setZero(operand == 0);
        conditionRegister.setNegative(negative);
        conditionRegister.setCarry(carry);
        conditionRegister.setOverflow(negative ^ carry);
        return operand;
    }

    // TODO: Perguntar sobre isto.
    short asl(short operand) {
        operand = (short) (operand << 1);
        short msb = (short) (MSB & operand);
        boolean negative = msb == MSB;
        boolean carry = negative;
        /*
         * if (negative) { operand = (short) (MSB | operand); }
         */
        conditionRegister.setZero(operand == 0);
        conditionRegister.setNegative(negative);
        conditionRegister.setCarry(carry);
        conditionRegister.setOverflow(negative ^ carry);
        return operand;
    }

    short adc(short operand) {
        boolean carry = conditionRegister.isCarry();
        short newValue = operand;
        if (carry) {
            newValue = (short) (operand + 1);
        }
        conditionRegister.setZero(newValue == 0);
        conditionRegister.setNegative(newValue < 0);
        conditionRegister.setCarry(carry && isCarryAdd(operand, (short) 1));
        conditionRegister.setOverflow(carry && isOverflow(operand, (short) 1, newValue));
        return newValue;
    }

    short sbc(short operand) {
        boolean carry = conditionRegister.isCarry();
        short newValue = operand;
        if (carry) {
            newValue = (short) (operand - 1);
        }
        conditionRegister.setZero(newValue == 0);
        conditionRegister.setNegative(newValue < 0);
        conditionRegister.setCarry(carry && isCarrySub(operand, (short) 1));
        conditionRegister.setOverflow(carry && isOverflow(operand, (short) -1, newValue));
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
    short mov(short src, short dst) {
        conditionRegister.setZero(src == 0);
        conditionRegister.setNegative(src < 0);
        conditionRegister.setOverflow(false);
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
    short add(short src, short dst) {
        short result = (short) (dst + src);
        conditionRegister.setNegative(result < 0);
        conditionRegister.setZero(result == 0);
        conditionRegister.setCarry(isCarryAdd(dst, src));
        conditionRegister.setOverflow(isOverflow(dst, src, result));
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
    short sub(short src, short dst) {
        short result = (short) (dst - src);
        conditionRegister.setNegative(result < 0);
        conditionRegister.setZero(result == 0);
        conditionRegister.setCarry(isCarrySub(dst, src));
        conditionRegister.setOverflow(isOverflow(dst, (short) -src, result));
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
    void cmp(short src, short dst) {
        short result = (short) (src - dst);
        conditionRegister.setNegative(result < 0);
        conditionRegister.setZero(result == 0);
        conditionRegister.setCarry(isCarrySub(src, dst));
        conditionRegister.setOverflow(isOverflow(src, (short) -dst, result));
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
    short and(short src, short dst) {
        short result = (short) (dst & src);
        conditionRegister.setNegative(result < 0);
        conditionRegister.setZero(result == 0);
        conditionRegister.setOverflow(false);
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
    short or(short src, short dst) {
        short result = (short) (dst | src);
        conditionRegister.setNegative(result < 0);
        conditionRegister.setZero(result == 0);
        conditionRegister.setOverflow(false);
        return result;
    }


    /**
     * Liga os códigos de condição contidos nos 4 bits menos significativos da
     * palavra fornecida. Os quatros bits representam as condições n z v c.
     *
     * @param value
     */
    void scc(byte value) {
        conditionRegister.setConditions(value);
    }

    /**
     * Desliga os códigos de condição contidos nos 4 bits menos significativos da
     * palavra fornecida. Os quatros bits representam as condições n z v c.
     *
     * @param value
     */
    void ccc(byte value) {
        conditionRegister.clearConditions(value);
    }

    /* Instruções */
    void jmp(short word) throws HaltedException {
        // 54 3210
        // 0b0100_XXXX_XXMM_MRRR

        // Modo de endereçamentos são os bits 3, 4, e 5.
        int addressMode = (0b0000_0000_0011_1000 & word) >> 3;

        // O número do registrador são os bits 0, 1 e 2
        int registerNumber = 0b0000_0000_0000_0111 & word;

        cpu.setPc(cpu.getOperand(addressMode, registerNumber));
    }

    void sob(short instruction) {
        int registerNumber = (0b0000_0111_0000_0000 & instruction) >> 8;
        short offset = (short) (0b0000_0000_1111_1111 & instruction);

        cpu.decrementRegister(registerNumber, 1);
        if (cpu.getRegister(registerNumber) != 0) {
            cpu.setPc((short) (cpu.getPc() - offset));
        }
    }

    /**
     * Os bits rrr da primeira palavra indicam um registrador, enquanto a segunda
     * palavra é utilizada para calcular o endereço da sub-rotina, de modo idêntico
     * à instrução de desvio incondicional (JMP). O desvio para a sub-rotina é
     * realizado conforme a sequência de operações a seguir:
     * 
     * <pre>
     *      temporário  <- endereço da subrotina
     *      pilha       <- registrador
     *      registrador <- R7
     *      R7          <- temporário
     * </pre>
     * 
     * Com isso, o registrador indicado na primeira palavra é colocado na pilha, a
     * seguir o PC atual é salvo neste registrador e, finalmente, o PC recebe o
     * enderço da sub-rotina. Noe que, se o registrador for o próprio PC, o efeito
     * do desvio resume-se a salvar o PC atual na pilha e, então, desviar para a
     * sub-rotina. Assim como na instrução de JMP, o uso do modo zero para o cálculo
     * do endereço da sub-rotina é tratado como NOP.
     * 
     * @throws HaltedException
     */
    void jsr(short instruction) throws HaltedException {
        // 0110_xrrr_xxmm_mrrr

        int reg = (0b0000_0111_0000_0000 & instruction) >> 8;
        int addressMode = (0b0000_0000_0011_1000 & instruction) >> 3;
        int registerNumber = (0b0000_0000_0000_0111 & instruction);
        // o endereço da sub-rotina é lido no valor temporário
        short temp = cpu.getOperand(addressMode, registerNumber);
        cpu.pushStack(cpu.getRegister(reg));
        cpu.setRegister(reg, cpu.getPc());
        cpu.setPc(temp);
    }

    /**
     * A instrução de retorno de sub-rotina (RTS) ocupa 1 único byte e tem o formato
     * 0111_xrrr. O bit x pode ser qualquer valor, e <code>rrr</code> indica o
     * registrador de retorno. A instrução realiza as seguintes operações
     * necessárias para desfazer o efeito de uma instrução de desvio para a
     * sub-rotina:
     * 
     * <pre>
     *      R7          <- registrador
     *      registrador <- topo da pilha
     * </pre>
     * 
     * Ou seja, o PC é atualizado com o conteúdo do registrador indicado e, a
     * seguir, este registrador é carregado com o conteúdo do topo da pilha. Se o
     * registrador for o próprio PC, a instrução resume-se a atualizar o PC com o
     * conteúdo do topo da pilha.
     * 
     * @param instruction
     */
    void rts(byte instruction) {
        // 0111_xrrr
        int returnRegister = (0b0000_0111 & instruction);
        cpu.setPc(cpu.getRegister(returnRegister));
        cpu.setRegister(returnRegister, cpu.popStack());
    }

}
