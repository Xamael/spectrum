package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;

/**
 * EI
 * <p>
 * Habilita la recepción de interrupciones por parte de la CPU
 * 
 * @author Alex Orgaz
 * 
 */
public class EI extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor
	 * 
	 * @param cpu
	 */
	public EI() {
		super();
		assembly = "EI";
		cycles = 4;
	}

	@Override
	public byte execute() {

		z80.setIFF1(true);
		z80.setIFF2(true);
		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
