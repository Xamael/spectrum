package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;

/**
 * XOR
 * <p>
 * Instrucción Lógica XOR
 * 
 * @author Alex Orgaz
 * 
 */
public class XOR extends Instruction {

	/* CPU espeficica de ejecución */
	Z80 z80;

	/**
	 * Constructor, dependiendo del opCode se realizará el operador XOR entre el Registro Acumulador A
	 * y otro registro especificado por el opCode
	 * 
	 * @param cpu
	 * @param source Registro a Comparar
	 * @throws UnknowInstructionException
	 */
	public XOR(int source) throws UnknowInstructionException {
		super();

		readMode = source;
		idxReadParam = 1;

		/* Por defecto la instrucción tarda 4 ciclos */
		cycles = 4;

		assembly = "XOR";
		param1 = Z80.DIR_NAMES[readMode];

		if (readMode == Z80.ADDR_HL || readMode == Z80.DIRECT_8) {
			cycles = 7;
		} else if (readMode == Z80.IX_D || readMode == Z80.IY_D) {
			cycles = 19;
		}
	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		/* Se optiene el registro origen de la información */
		byte reg = z80.getData8(readMode);

		/* Operación OR */
		byte res = (byte) (z80.getA() ^ reg);
		z80.setA(res);

		/* Establecimiento de flags */
		z80.setFlagSigned(res);
		z80.setFlagZero(res);
		z80.setFlag(Z80.FLAG_H, false);
		z80.setFlagParity(res);
		z80.setFlag(Z80.FLAG_N, false);
		z80.setFlag(Z80.FLAG_C, false);
		z80.setFlag35(res);

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
