package org.maox.emulator.core;

import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.IllegalAddressException;

/**
 * Mapeo de Códigos de Instrucción Maquina OPC a clases
 * 
 * @author Alex Orgaz
 * 
 */
public abstract class InstructionMap {

	/**
	 * Devuelve la instrucción asociada a un código máquina OPC
	 * 
	 * @param opCode Código en hexadecimal de la instrucción
	 * @return Implementación de la instrucción
	 * @throws InstructionException
	 * @throws IllegalAddressException
	 */
	public abstract Instruction getInstruction(byte opcode) throws InstructionException, IllegalAddressException;

	/**
	 * Devuelve la instrucción asociada a un código máquina OPC, leyende los bytes extras necesarios para completarla.
	 * EL PC no debe alterarse en esta lectura de bytes extra
	 * <p>
	 * Usado para debugear las isntrucciones en tiempo de ejecución.
	 * 
	 * @param address dirección donde se encuentra la instrucción
	 * @return Implementación de la instrucción
	 * @throws InstructionException
	 * @throws IllegalAddressException
	 */
	public abstract Instruction getInstructionComplete(int address) throws InstructionException, IllegalAddressException;
}
