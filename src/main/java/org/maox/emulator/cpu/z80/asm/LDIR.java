package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;

/**
 * LDIR
 * <p>
 * Load and increment con Repetición.
 * 
 * @author Alex Orgaz
 * 
 */
public class LDIR extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor
	 * 
	 * @param cpu
	 */
	public LDIR() {
		super();
		assembly = "LDIR";
		cycles = 16;
	}

	@Override
	public byte execute() throws IllegalAddressException, InstructionException {

		// Primero se transfiere un byte desde una posición de memoria (HL) a otra (DE)
		z80.setData8(Z80.ADDR_DE, z80.getData8(Z80.ADDR_HL));

		// Se incrementan los punteros
		z80.setData16(Z80.REG_DE, z80.getData16(Z80.REG_DE) + 1);
		z80.setData16(Z80.REG_HL, z80.getData16(Z80.REG_HL) + 1);

		// Se decrementa el Byte Counter
		z80.setData16(Z80.REG_BC, z80.getData16(Z80.REG_BC) - 1);

		// Se establecen los flags
		z80.setFlag(Z80.FLAG_H, false);
		z80.setFlag(Z80.FLAG_N, false);
		z80.setFlag(Z80.FLAG_PV, false);

		// Flag no documentados
		// TODO
		// F5 is bit 1 of (transferred byte + A)
		// F3 is bit 3 of (transferred byte + A)

		// Se aplica la repetición, si BC != 0 se retrocede el PC 2 posiciones
		if (z80.getData16(Z80.REG_BC) != 0) {
			z80.setPC(z80.getPC() - 2);
			return 21;
		}

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
