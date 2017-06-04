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
 * Instrucción de Carga Load LD
 * 
 * @author Alex Orgaz
 * 
 */
public class LD extends Instruction {

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
	public LD(int destination, int source) throws UnknowInstructionException {
		super();

		writeMode = destination;
		readMode = source;
		idxWriteParam = 1;
		idxReadParam = 2;

		assembly = "LD";
		param1 = Z80.DIR_NAMES[writeMode];
		param2 = Z80.DIR_NAMES[readMode];

		switch (writeMode) {
		/* 8 Bits ----- */
		/* Destino registros */
		case Z80.REG_A:
		case Z80.REG_B:
		case Z80.REG_C:
		case Z80.REG_D:
		case Z80.REG_E:
		case Z80.REG_H:
		case Z80.REG_L:

			switch (readMode) {
			case Z80.DIRECT_8:
			case Z80.ADDR_BC:
			case Z80.ADDR_DE:
			case Z80.ADDR_HL:
				cycles = 7;
				break;
			case Z80.REG_I:
			case Z80.REG_R:
				cycles = 9;
			case Z80.ADDR_NN:
				cycles = 13;
				break;
			case Z80.IX_D:
			case Z80.IY_D:
				cycles = 19;
				break;
			default: /* Por defecto la instrucción tarda 4 ciclos */
				cycles = 4;
			}

			break;
		case Z80.REG_I:
		case Z80.REG_R:
			cycles = 9;
			break;
		/* Dirección memoria */
		case Z80.ADDR_BC:
		case Z80.ADDR_DE:
		case Z80.ADDR_HL:
			/* Por defecto la instrucción tarda 7 ciclos */
			cycles = 7;
			if (readMode == Z80.DIRECT_8) {
				cycles = 10;
			}
			break;
		case Z80.ADDR_NN:
			cycles = 13;
			break;
		/* Dirección con desplazaciento */
		case Z80.IX_D:
		case Z80.IY_D:
			cycles = 19;
			break;
		}

	}

	@Override
	public byte execute() throws InstructionException, IllegalAddressException {

		/*
		 * La operación LD (IX+d), n el primer byte contiene el desplazamiento
		 * y el segundo el dato por lo que no podemos seguir el esquema normal, ya
		 * que se leería primero el dato y después el desplazamiento
		 */
		if (readMode == Z80.DIRECT_8 && (writeMode == Z80.IX_D || writeMode == Z80.IY_D)) {
			cpu.loadIR();
			byte offset = z80.getIR();
			byte data = z80.getData8(readMode);
			if (writeMode == Z80.IX_D) {
				z80.write8(z80.getIX() + offset, data);
			} else {
				z80.write8(z80.getIY() + offset, data);
			}
		} else {
			/* Se optiene el registro origen de la información */
			byte data = z80.getData8(readMode);

			/* Escritura de la información */
			z80.setData8(writeMode, data);
		}

		/* Establecimiento de flags */
		/* Sólo se establecen los FLAGS para el LD A, I y LD A, R */
		if (readMode == Z80.REG_I || readMode == Z80.REG_R) {
			z80.setFlagSigned(z80.getA());
			z80.setFlagZero(z80.getA());
			z80.setFlag(Z80.FLAG_H, false);
			z80.setFlag(Z80.FLAG_PV, z80.isIFF2());
			z80.setFlag(Z80.FLAG_N, false);
			z80.setFlag35(z80.getA());
			// TODO Si una excepción ocurre durante estás instrucciones el Parity Flag tendrá un 0.
		}

		return cycles;
	}

	@Override
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		z80 = (Z80) cpu;
	}
}
