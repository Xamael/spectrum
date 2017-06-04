package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;

/**
 * LD
 * <p>
 * Instrucción de Control de Flujo Jump JP
 * 
 * @author Alex Orgaz
 * 
 */
public class JP extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor de salto incondicional
	 * 
	 * @param cpu
	 * @param dest Longitud de salto
	 * @throws UnknowInstructionException
	 */
	public JP(int source) throws UnknowInstructionException {
		super();
		readMode = source;
		idxReadParam = 1;

		assembly = "JP";
		param1 = Z80.DIR_NAMES[readMode];

		switch (readMode) {
		case Z80.DIRECT_16:
			cycles = 10;
			break;
		case Z80.REG_HL:
			cycles = 4;
			break;
		case Z80.REG_IX:
		case Z80.REG_IY:
			cycles = 8;
			break;
		}
	}

	/**
	 * Constructor de salto condicional
	 * 
	 * @param cpu
	 * @param condition Condición de salto
	 * @param dest Longitud de salto
	 * @throws UnknowInstructionException
	 */
	public JP(int condition, int source) throws UnknowInstructionException {
		super();

		condMode = condition;
		readMode = source;

		assembly = "JP";
		param1 = Z80.COND_NAMES[condMode];
		param2 = Z80.DIR_NAMES[readMode];

		cycles = 10;
		idxReadParam = 2;

		/*
		 * Los saltos condicionales con desplazamiento variarán el número de ciclos dependiendo
		 * de si la condición se cumple o no
		 */
	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {
		boolean bCond = false;

		// Se lee la dirección destino
		int address = z80.getData16(readMode);

		// Si se cumple la condición se carga el PC con el nuevo dato
		switch (condMode) {
		case -1:
			z80.setPC(address);
			break;
		case Z80.COND_NZ:
			if (!z80.isFlagZero()) {
				bCond = true;
				z80.setPC(address);
			}
			break;
		case Z80.COND_Z:
			if (z80.isFlagZero()) {
				bCond = true;
				z80.setPC(address);
			}
			break;
		case Z80.COND_NC:
			if (!z80.isFlagCarry()) {
				bCond = true;
				z80.setPC(address);
			}
			break;
		case Z80.COND_C:
			if (z80.isFlagCarry()) {
				bCond = true;
				z80.setPC(address);
			}
			break;
		case Z80.COND_PO:
			if (z80.isFlagOdd()) {
				z80.setPC(address);
			}
			break;
		case Z80.COND_PE:
			if (z80.isFlagEven()) {
				z80.setPC(address);
			}
			break;
		case Z80.COND_P:
			if (z80.isFlagPositive()) {
				z80.setPC(address);
			}
			break;
		case Z80.COND_M:
			if (z80.isFlagNegative()) {
				z80.setPC(address);
			}
			break;
		}

		if (readMode == Z80.DESPLACEMENT) {
			if (condMode == -1 || bCond) {
				cycles = 12;
			} else {
				cycles = 7;
			}
		}

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
