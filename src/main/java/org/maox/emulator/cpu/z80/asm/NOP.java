package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;

/**
 * NOP
 * <p>
 * No realiza nada durante esta instrucción
 * 
 * @author Alex Orgaz
 * 
 */
public class NOP extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor
	 * 
	 * @param cpu
	 */
	public NOP() {
		super();
		assembly = "NOP";
		cycles = 4;
	}

	@Override
	public byte execute() {
		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
