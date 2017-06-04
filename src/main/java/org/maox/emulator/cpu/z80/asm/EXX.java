package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;

/**
 * EXX
 * <p>
 * Realiza el intercambio de los resgitros de procesador por sus alternativos
 * 
 * @author Alex Orgaz
 * 
 */
public class EXX extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor
	 * 
	 * @param cpu
	 */
	public EXX() {
		super();
		assembly = "EXX";
		cycles = 4;
	}

	@Override
	public byte execute() {

		z80.exchangeX();
		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
