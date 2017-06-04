package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;

/**
 * OUT
 * <p>
 * Instrucción OUT: Llamada a un puerto de I/O
 * 
 * @author Alex Orgaz
 * 
 */
public class OUT extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor
	 * 
	 * @throws UnknowInstructionException
	 */
	public OUT() throws UnknowInstructionException {
		super();
		readMode = Z80.DIRECT_8;
		readMode2 = Z80.REG_A;
		idxReadParam = 1;
		assembly = "OUT";
		param1 = Z80.DIR_NAMES[readMode];
		param2 = Z80.DIR_NAMES[readMode2];
		cycles = 11;
	}

	/**
	 * Constructor con registro de entrada
	 * 
	 * @param source Registro a Comparar
	 * @throws UnknowInstructionException
	 */
	public OUT(int source) throws UnknowInstructionException {
		super();
		readMode = Z80.REG_C;
		readMode2 = source;

		idxReadParam = 1;
		assembly = "OUT";
		param1 = Z80.DIR_NAMES[readMode];
		param2 = Z80.DIR_NAMES[readMode2];
		cycles = 12;
	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		/* Se optiene el puerto I/O al que se llamará */
		byte port = z80.getData8(readMode);
		byte reg = z80.getData8(readMode2);

		int address;
		if (readMode == Z80.DIRECT_8) {
			address = (reg << 8 | port);
		} else {
			address = z80.getBC();
		}

		z80.writeIO(address, reg);

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
