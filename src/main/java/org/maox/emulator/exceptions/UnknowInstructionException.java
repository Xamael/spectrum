package org.maox.emulator.exceptions;

/**
 * Excepcion de Instrucción Desconocida
 * 
 * @author Alex Orgaz
 * 
 */
@SuppressWarnings("serial")
public class UnknowInstructionException extends InstructionException {

	/**
	 * Constructor general
	 */
	public UnknowInstructionException() {
		super();
	}

	/**
	 * Constructor con mensaje de error
	 * 
	 * @param message
	 */
	public UnknowInstructionException(String message) {
		super(message);
	}

}
