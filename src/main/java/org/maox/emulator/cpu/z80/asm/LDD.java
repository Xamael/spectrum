package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;

/**
 * LDD
 * <p>
 * Load and decrement.
 * 
 * @author Alex Orgaz
 * 
 */
public class LDD extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor
	 * 
	 * @param cpu
	 */
	public LDD() {
		super();
		assembly = "LDD";
		cycles = 16;
	}

	@Override
	public byte execute() throws IllegalAddressException, InstructionException {

		// Primero se transfiere un byte desde una posición de memoria (HL) a otra (DE)
		z80.setData8(Z80.ADDR_DE, z80.getData8(Z80.ADDR_HL));

		// Se decrementan los punteros
		z80.setData16(Z80.REG_DE, z80.getData16(Z80.REG_DE) - 1);
		z80.setData16(Z80.REG_HL, z80.getData16(Z80.REG_HL) - 1);

		// Se decrementa el Byte Counter
		z80.setData16(Z80.REG_BC, z80.getData16(Z80.REG_BC) - 1);

		// Se establecen los flags
		z80.setFlag(Z80.FLAG_H, false);
		z80.setFlag(Z80.FLAG_N, false);
		z80.setFlag(Z80.FLAG_PV, z80.getData16(Z80.REG_BC) != 0);

		// Flag no documentados
		// TODO
		// F5 is bit 1 of (transferred byte + A)
		// F3 is bit 3 of (transferred byte + A)

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
