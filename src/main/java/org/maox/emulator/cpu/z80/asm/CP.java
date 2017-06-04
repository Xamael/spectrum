package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;

/**
 * CP
 * <p>
 * Instrucción Comparación CP
 * 
 * @author Alex Orgaz
 * 
 */
public class CP extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor, dependiendo del opCode se realizará la comparacion entre el Registro Acumulador A
	 * y otro registro especificado por el opCode
	 * 
	 * @param source Registro a Comparar
	 * @throws UnknowInstructionException
	 */
	public CP(int source) throws UnknowInstructionException {
		super();
		readMode = source;
		idxReadParam = 1;
		assembly = "CP";
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

		// Se guarda el valor del registro A
		byte regA = z80.getA();

		// Sería haceer cómo el SUB tirando el resultado
		byte oper = z80.getData8(readMode);
		z80.subWithCarry(oper, (byte) 0);

		// Se restaura el valor del registro A
		z80.setA(regA);

		// Los flags 3 y 5 se copian del operando no del resultado
		// Flags sin documentar
		z80.setFlag35(oper);

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
