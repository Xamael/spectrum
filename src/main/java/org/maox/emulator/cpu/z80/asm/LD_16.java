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
 * Instrucción de Carga Load LD 16 bits
 * 
 * @author Alex Orgaz
 * 
 */
public class LD_16 extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor, dependiendo del opCode se la carga de un registro desde un origen determinado
	 * 
	 * @param cpu
	 * @param destination Registro Destino
	 * @param source Registro Origen
	 * @throws UnknowInstructionException
	 */
	public LD_16(int destination, int source) throws UnknowInstructionException {
		super();

		writeMode = destination;
		readMode = source;
		idxWriteParam = 1;
		idxReadParam = 2;

		assembly = "LD";
		param1 = Z80.DIR_NAMES[writeMode];
		param2 = Z80.DIR_NAMES[readMode];

		switch (writeMode) {

		case Z80.REG_BC:
		case Z80.REG_DE:
		case Z80.REG_HL:
		case Z80.REG_SP:
			if (readMode == Z80.ADDR_NN) {
				cycles = 20;
			} else if (readMode == Z80.REG_HL) {
				cycles = 6;
			} else {
				cycles = 10;
			}
			break;
		case Z80.REG_IX:
		case Z80.REG_IY:
			if (readMode == Z80.DIRECT_16) {
				cycles = 14;
			} else if (readMode == Z80.ADDR_NN) {
				cycles = 16;
			}
			break;
		case Z80.ADDR_NN_16:
			cycles = 20;
			break;
		}

	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		z80.setData16(writeMode, z80.getData16(readMode));
		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
