package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;

/**
 * DEC
 * <p>
 * Instrucción de decrementar un byte
 * 
 * @author Alex Orgaz
 * 
 */
public class DEC extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor, dependiendo del opCode se realizará el decremento de uno sobre un byte determinado
	 * 
	 * @param source Registro a Decrementar
	 * @throws UnknowInstructionException
	 */
	public DEC(int source) throws UnknowInstructionException {
		super();
		readMode = source;
		idxReadParam = 1;
		assembly = "DEC";
		param1 = Z80.DIR_NAMES[readMode];

		/* Por defecto la instrucción tarda 4 ciclos */
		cycles = 4;

		if (readMode == Z80.ADDR_HL) {
			cycles = 11;
		} else if (readMode == Z80.IX_D || readMode == Z80.IY_D) {
			cycles = 23;
		}
	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		/* Se optiene el registro origen de la información */
		byte reg = z80.getData8(readMode);

		/* Operación DEC. Decrementar el byte */
		byte res = (byte) (reg + (byte) 0xFF);

		switch (readMode) {
		case Z80.REG_A:
		case Z80.REG_B:
		case Z80.REG_C:
		case Z80.REG_D:
		case Z80.REG_E:
		case Z80.REG_H:
		case Z80.REG_L:
		case Z80.ADDR_HL:
			z80.setData8(readMode, res);
			break;
		case Z80.IX_D:
			z80.write8(z80.getIX() + z80.getIR(), res);
			break;
		case Z80.IY_D:
			z80.write8(z80.getIY() + z80.getIR(), res);
			break;
		}

		/* Establecimiento de flags */
		z80.setFlagSigned(res);
		z80.setFlagZero(res);
		z80.setFlag(Z80.FLAG_H, (reg & 0x0F) < 1);
		z80.setFlag(Z80.FLAG_PV, reg == (byte) 0x80);
		z80.setFlag(Z80.FLAG_N, true);
		z80.setFlag35(res);

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
