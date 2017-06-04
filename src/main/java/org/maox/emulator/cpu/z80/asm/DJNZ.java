package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;

/**
 * DJNZ
 * <p>
 * Instrucción de Control de Flujo Jump DJNZ
 * 
 * @author Alex Orgaz
 * 
 */
public class DJNZ extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor de salto
	 * 
	 * @param cpu
	 * @throws UnknowInstructionException
	 */
	public DJNZ(int source) throws UnknowInstructionException {
		super();
		readMode = source;
		idxReadParam = 1;

		assembly = "DJNZ";
		param1 = Z80.DIR_NAMES[readMode];

		cycles = 13;
	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		// Se lee la dirección destino
		int address = z80.getData16(readMode);

		// Si se cumple la condición se carga el PC con el nuevo dato
		if (z80.getB() != 0) {
			z80.setPC(address);
		} else
			return 8; /* cycles */

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
