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
 * Instrucción de decrementar un dato de 16 bits
 * 
 * @author Alex Orgaz
 * 
 */
public class DEC_16 extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor, dependiendo del opCode se realizará el decremento de uno sobre un dato determinado
	 * 
	 * @param source Registro a Decrementar
	 * @throws UnknowInstructionException
	 */
	public DEC_16(int source) throws UnknowInstructionException {
		super();
		readMode = source;
		idxReadParam = 1;
		assembly = "DEC";
		param1 = Z80.DIR_NAMES[readMode];

		/* Por defecto la instrucción tarda 6 ciclos */
		cycles = 6;

		if (readMode == Z80.REG_IX || readMode == Z80.REG_IY) {
			cycles = 10;
		}
	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		/* Se optiene el registro origen de la información */
		int reg = z80.getData16(readMode);

		/* Operación DEC. Decrementar el byte */
		short res = (short) (reg + (short) 0xFFFF);
		z80.setData16(readMode, res);

		/* Establecimiento de flags */
		// No hay

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
