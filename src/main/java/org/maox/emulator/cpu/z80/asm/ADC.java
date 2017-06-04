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
 * Instrucción de Suma con Accareo
 * 
 * @author Alex Orgaz
 * 
 */
public class ADC extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor, dependiendo del opCode se realizará el operador ADC entre el Registro Acumulador A
	 * y otro registro especificado por el opCode
	 * 
	 * @param source Registro a sumar
	 * @throws UnknowInstructionException
	 */
	public ADC(int source) throws UnknowInstructionException {
		super();
		readMode = source;
		idxReadParam = 1;
		assembly = "ADC";
		param1 = Z80.DIR_NAMES[readMode];

		/* Por defecto la instrucción tarda 4 ciclos */
		cycles = 4;

		if (readMode == Z80.ADDR_HL || readMode == Z80.DIRECT_8) {
			cycles = 7;
		} else if (readMode == Z80.IX_D || readMode == Z80.IY_D) {
			cycles = 19;
		}
	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		/* Operación ADC */
		z80.addWithCarry(z80.getData8(readMode), z80.getCarry());
		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
