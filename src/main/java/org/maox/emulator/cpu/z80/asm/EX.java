package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;

/**
 * EXX
 * <p>
 * Realiza el intercambio de los resgitros de procesador
 * 
 * @author Alex Orgaz
 * 
 */
public class EX extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor
	 * 
	 * @param cpu
	 */
	public EX(int reg1, int reg2) {
		super();
		assembly = "EX";

		readMode = reg1;
		readMode2 = reg2;
		idxReadParam = 1;
		param1 = Z80.DIR_NAMES[readMode];
		param2 = Z80.DIR_NAMES[readMode2];

		/* Ciclos por defecto */
		cycles = 4;

		if (readMode == Z80.ADDR_SP && readMode == Z80.REG_HL) {
			cycles = 19;
		} else if (readMode == Z80.ADDR_SP) {
			cycles = 23;
		}
	}

	@Override
	public byte execute() {

		switch (readMode2) {
		case Z80.REG_HL:
			if (readMode == Z80.REG_DE) {
				z80.exchangeDEHL();
			} else {
				z80.exchangeSP(Z80.REG_HL);
			}
			break;
		case Z80.REG_AF_ALT:
			z80.exchangeAF();
			break;
		case Z80.REG_IX:
			z80.exchangeSP(Z80.REG_IX);
			break;
		case Z80.REG_IY:
			z80.exchangeSP(Z80.REG_IY);
			break;

		}

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
