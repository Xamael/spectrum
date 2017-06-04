package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;

/**
 * ADC
 * <p>
 * Instrucción de Suma 16 bits con Accareo
 * 
 * @author Alex Orgaz
 * 
 */
public class ADC_16 extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor, dependiendo del opCode se realizará el operador ADC entre el Registro HL
	 * y otro registro especificado por el opCode
	 * 
	 * @param dest Registro destino a sumar
	 * @param source Registro a sumar
	 * @throws UnknowInstructionException
	 */
	public ADC_16(int dest, int source) throws UnknowInstructionException {
		super();
		assembly = "ADC";

		writeMode = dest;
		readMode = source;
		idxWriteParam = 1;
		idxReadParam = 2;

		param1 = Z80.DIR_NAMES[writeMode];
		param2 = Z80.DIR_NAMES[readMode];

		/* Por defecto la instrucción tarda 15 ciclos */
		cycles = 15;
	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		/* Operación ADD */
		z80.setData16(writeMode, z80.addWithCarry16(z80.getData16(writeMode), z80.getData16(readMode), z80.getCarry()));
		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
