package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;

/**
 * IM
 * <p>
 * Establece el modo de interrupción de la CPU
 * 
 * @author Alex Orgaz
 * 
 */
public class IM extends Instruction {

	/* CPU espeficica de ejecución */
	private Z80 z80;
	private int mode;

	/**
	 * Constructor
	 * 
	 * @param cpu
	 */
	public IM(int mode) {
		super();
		assembly = "IM " + mode;
		cycles = 8;
	}

	@Override
	public byte execute() {

		z80.setInterruptMode(mode);
		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
