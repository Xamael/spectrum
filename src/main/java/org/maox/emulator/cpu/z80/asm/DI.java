package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;

/**
 * DI
 * <p>
 * Deshabilita la recepción de interrupciones por parte de la CPU
 * 
 * @author Alex Orgaz
 * 
 */
public class DI extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor
	 * 
	 * @param cpu
	 */
	public DI() {
		super();
		assembly = "DI";
		cycles = 4;
	}

	@Override
	public byte execute() {

		z80.setIFF1(false);
		z80.setIFF2(false);
		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
