package org.maox.emulator.exceptions;

/**
 * Excepcion General de Instrucción
 * 
 * @author Alex Orgaz
 * 
 */
@SuppressWarnings("serial")
public class InstructionException extends Exception {

	/**
	 * Constructor general
	 */
	public InstructionException() {
		super();
	}

	/**
	 * Constructor con mensaje de error
	 * 
	 * @param message
	 */
	public InstructionException(String message) {
		super(message);
	}

}
