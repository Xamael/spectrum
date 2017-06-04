package org.maox.emulator.core;

import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.debug.Hex;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;

/**
 * Encapsulación de una instrucción maquina
 * 
 * @author Alex Orgaz
 * 
 */
public abstract class Instruction {

	/* CPU donde se ejecutará la instrucción */
	protected CPU cpu;

	/* Código Emsamblador */
	protected String assembly; // Código mnemonico
	protected String param1; // Parametro Izquierdo (1)
	protected String param2; // Parametro Derecho (2)

	protected String value1; // Valor Parametro Izquierdo (1)
	protected String value2; // Valor Parametro Derecho (2)

	/* Código de instrucción */
	protected byte opCode = 0;
	/* Código de prefijo de instrucción */
	protected boolean bPrefix = false;
	protected byte prefix = 0;
	/* Código de 2º prefijo de instrucción */
	protected boolean bPrefix2 = false;
	protected byte prefix2 = 0;
	/* Ciclos que tarda en ejecutar */
	protected byte cycles;
	/* Código de prefijo de instrucción */
	protected boolean bSufix1 = false;
	protected byte sufix1 = 0;
	/* Código de prefijo de instrucción */
	protected boolean bSufix2 = false;
	protected byte sufix2 = 0;

	/* Modo de direccionamiento de lectura */
	protected int readMode = -1;
	protected int readMode2 = -1; // Si tiene 2 parametros de lectura
	/* Modo de direccionamiento de escritura */
	protected int writeMode = -1;
	/* Modos de condición */
	protected int condMode = -1;
	/* Posiciones de los parametros (para el modo debug) */
	protected int idxReadParam = 0;
	protected int idxWriteParam = 0;
	protected int idxCondParam = 0;

	/**
	 * Completa los parametros de la instrucción, sin avanzar el PC de la CPU
	 * 
	 * @param address dirección de memoria donde está el inicio de la instrucción
	 * @throws IllegalAddressException
	 */
	public void completeValues(int address) throws IllegalAddressException {

		resetValues();

		switch (writeMode) {
		case Z80.DIRECT_8:
		case Z80.IX_D:
		case Z80.IY_D:
			recoverSufix(address, 1);
			createValue(idxWriteParam, writeMode);
			break;
		case Z80.DIRECT_16:
		case Z80.ADDR_NN:
			recoverSufix(address, 1);
			recoverSufix(address, 2);
			createValue(idxWriteParam, writeMode);
			break;
		}

		switch (readMode) {
		case Z80.DIRECT_8:
		case Z80.IX_D:
		case Z80.IY_D:
			recoverSufix(address, 1);
			createValue(idxReadParam, readMode);
			break;
		case Z80.DIRECT_16:
		case Z80.ADDR_NN:
			recoverSufix(address, 1);
			recoverSufix(address, 2);
			createValue(idxReadParam, readMode);
			break;
		case Z80.DESPLACEMENT:
			recoverSufix(address, 1);
			createValue(idxReadParam, readMode);
			break;
		}

	}

	/**
	 * Crea la cadena con el valor de parametro.
	 * <p>
	 * Para debug
	 * 
	 * @param idx
	 * @param mode
	 */
	protected void createValue(int idx, int mode) {
		String value = null;

		switch (mode) {
		case Z80.DIRECT_8:
			value = "$" + Hex.byteToHex(sufix1);
			break;
		case Z80.DIRECT_16:
			value = "$" + Hex.byteToHex(sufix2) + Hex.byteToHex(sufix1);
			break;
		case Z80.ADDR_NN:
			value = "($" + Hex.byteToHex(sufix2) + Hex.byteToHex(sufix1) + ")";
			break;
		case Z80.DESPLACEMENT:
			value = "+" + (sufix1 + 2);
			break;
		}

		if (idx == 1) {
			value1 = value;
		} else {
			value2 = value;
		}
	}

	/**
	 * Ejecución de la instrucción
	 * 
	 * @return número de ciclos de reloj consumidos
	 */
	public abstract byte execute() throws InstructionException, IllegalAddressException;

	/**
	 * Devuelve el código mnemónico emsamblador de la instrucción
	 * 
	 * @return código mnemonico
	 */
	public String getAssembly() {
		return assembly + (param1 != null ? " " + param1 : "") + (param1 != null && param2 != null ? "," : "")
				+ (param2 != null ? " " + param2 : "");
	}

	/**
	 * Devuelve el código mnemónico emsamblador de la instrucción sustituyendo los parametros (no registro) por sus
	 * valores en memoria.
	 * <P>
	 * Sólo para debug
	 * 
	 * @return código mnemonico
	 */
	public String getAssemblyDecoded() {
		String v1 = (param1 != null ? " " + (value1 != null ? value1 : param1) : "");
		String v2 = (param2 != null ? " " + (value2 != null ? value2 : param2) : "");

		return assembly + v1 + (param1 != null && param2 != null ? "," : "") + v2;
	}

	/**
	 * @return número de ciclos de CPU usados en la ejecución de la instrucción
	 */
	public byte getCycles() {
		return cycles;
	}

	/**
	 * Devuelve el número de opcodes la instrucción.
	 * Deben haberse cargado los parametros con completeValue() antes.
	 * 
	 * @return
	 */
	public int getNumOpcodes() {
		return 1 + (bPrefix ? 1 : 0) + (bPrefix2 ? 1 : 0) + (bSufix1 ? 1 : 0) + (bSufix2 ? 1 : 0);
	}

	/**
	 * Devuelve una cadena con los opcodes que componen la instrucción
	 * 
	 * @return
	 */
	public String getStringOpCodes() {
		StringBuilder str = new StringBuilder();
		if (prefix != 0) {
			str.append(Hex.byteToHex(prefix) + " ");
		}

		/* Si tiene el segundo prefijo, el opcode va al final, el sufijo que siempre es de desaplazamiento al principio */
		if (prefix2 != 0) {
			str.append(Hex.byteToHex(prefix2) + " ");

			if (bSufix1) {
				str.append(Hex.byteToHex(sufix1) + " ");
			}
			str.append(Hex.byteToHex(opCode));

		} else {
			str.append(Hex.byteToHex(opCode));

			if (bSufix1) {
				str.append(" " + Hex.byteToHex(sufix1));
			}

		}

		if (bSufix2) {
			str.append(" " + Hex.byteToHex(sufix2));
		}

		return str.toString();
	}

	/**
	 * Recupera el opcode de un sufijo determinado, no avanza el PC
	 * 
	 * @param address dirección de memoria donde está el inicio de la instrucción
	 * @param idx Sufijo 1 0 2
	 * @return opcode
	 * @throws IllegalAddressException
	 */
	protected byte recoverSufix(int address, int idx) throws IllegalAddressException {
		byte opc;
		if (idx == 1) {
			bSufix1 = true;
			sufix1 = (byte) (cpu.read8(address + 1 + (bPrefix ? 1 : 0)) & 0x000000FF);
			opc = sufix1;
		} else {
			bSufix2 = true;
			sufix2 = (byte) (cpu.read8(address + 2 + (bPrefix ? 1 : 0)) & 0x000000FF);
			opc = sufix2;
		}

		return opc;
	}

	/**
	 * Resetea los parametros de la instrucción junto con sus opcodes sufijos
	 */
	public void resetValues() {
		bSufix1 = false;
		bSufix2 = false;
		value1 = null;
		value2 = null;
	}

	/**
	 * Establece la CPU donde se ejecutará la instrucción
	 * 
	 * @param cpu
	 */
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
	}

	/**
	 * Establece los ciclos de la instrucción
	 * 
	 * @param cycles the cycles to set
	 */
	public void setCycles(byte cycles) {
		this.cycles = cycles;
	}

	/**
	 * Establece el OpCode principal de la instrucción
	 * 
	 * @param opCode
	 */
	public void setOpCode(byte opCode) {
		this.opCode = opCode;
	}

	/**
	 * Establece el opCode prefijo de la instrucción
	 * 
	 * @param opCode
	 */
	public void setPrefix(byte opCode) {
		bPrefix = true;
		this.prefix = opCode;
	}

	/**
	 * Establece el opCode prefijo 2 de la instrucción
	 * 
	 * @param opCode
	 */
	public void setPrefix2(byte opCode) {
		bPrefix2 = true;
		this.prefix2 = opCode;
	}

	/**
	 * Establece el opCode primer sufijo de la instrucción
	 * 
	 * @param opCode
	 */
	public void setSufix_1(byte opCode) {
		bSufix1 = true;
		this.sufix1 = opCode;
	}

	/**
	 * Establece el opCode segundo sufijo de la instrucción
	 * 
	 * @param opCode
	 */
	public void setSufix_2(byte opCode) {
		bSufix2 = true;
		this.sufix2 = opCode;
	}

	@Override
	public String toString() {
		return getAssembly();
	}
}
