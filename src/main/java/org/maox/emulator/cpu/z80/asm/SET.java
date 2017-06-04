package org.maox.emulator.cpu.z80.asm;

import org.maox.emulator.core.CPU;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;

/**
 * SET
 * <p>
 * Establece un bit a 1 de un byte determinado
 * 
 * @author Alex Orgaz
 * 
 */
public class SET extends Instruction {

	/* CPU espeficica de ejecución */
	private Z80 z80;
	private int bit;

	/**
	 * Constructor, dependiendo del opCode se establecerá el bit de un registro determinado
	 * 
	 * @param bit Bit a establecer a 1
	 * @param source Registro a Actualizar
	 * @throws UnknowInstructionException
	 */
	public SET(int bit, int source) throws UnknowInstructionException {
		super();
		this.bit = bit;
		readMode = source;
		idxReadParam = 1;
		assembly = "SET " + bit + ",";
		param1 = Z80.DIR_NAMES[readMode];

		/* Por defecto la instrucción tarda 4 ciclos */
		cycles = 8;

		if (readMode == Z80.ADDR_HL) {
			cycles = 15;
		} else if (readMode == Z80.IX_D || readMode == Z80.IY_D) {
			cycles = 23;
		}
	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		/* Se optiene el registro origen de la información */
		byte reg;
		byte dest = 0;

		/*
		 * En esta instrucción, como caso especial, el desaplazamiento no está en el
		 * siguiente byte, sino que se ha leido en el anterior
		 */
		if (readMode == Z80.IX_D) {
			dest = z80.read8(z80.getPC() - 2);
			reg = z80.read8(z80.getIX() + dest);
		} else if (readMode == Z80.IY_D) {
			dest = z80.read8(z80.getPC() - 2);
			reg = z80.read8(z80.getIY() + dest);
		} else {
			reg = z80.getData8(readMode);
		}

		/* Se establece el bit */
		reg |= (1 << bit);

		/* Se guarda el byte */
		switch (readMode) {
		case Z80.REG_A:
		case Z80.REG_B:
		case Z80.REG_C:
		case Z80.REG_D:
		case Z80.REG_E:
		case Z80.REG_H:
		case Z80.REG_L:
		case Z80.ADDR_HL:
			z80.setData8(readMode, reg);
			break;
		case Z80.IX_D:
			z80.write8(z80.getIX() + dest, reg);
			break;
		case Z80.IY_D:
			z80.write8(z80.getIY() + dest, reg);
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
