package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;

/**
 * ADD
 * <p>
 * Instrucción de Suma 16 bits sin Accareo
 * 
 * @author Alex Orgaz
 * 
 */
public class ADD_16 extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor, dependiendo del opCode se realizará el operador ADD entre el Registro HL
	 * y otro registro especificado por el opCode
	 * 
	 * @param source Registro destino a sumar
	 * @param source Registro a sumar
	 * @throws UnknowInstructionException
	 */
	public ADD_16(int dest, int source) throws UnknowInstructionException {
		super();
		assembly = "ADD";

		writeMode = dest;
		readMode = source;
		idxWriteParam = 1;
		idxReadParam = 2;

		param1 = Z80.DIR_NAMES[writeMode];
		param2 = Z80.DIR_NAMES[readMode];

		/* Por defecto la instrucción tarda 11 ciclos */
		if (writeMode == Z80.REG_HL) {
			cycles = 11;
		} else {
			cycles = 15;
		}
	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		/* Operación ADD */
		z80.setData16(writeMode, z80.addWithCarry16(z80.getData16(writeMode), z80.getData16(readMode), (byte) 0));
		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
