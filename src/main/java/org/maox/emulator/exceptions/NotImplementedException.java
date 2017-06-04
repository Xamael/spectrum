package org.maox.emulator.exceptions;

/**
 * Excepcion de Instrucción Desconocida
 * 
 * @author Alex Orgaz
 * 
 */
@SuppressWarnings("serial")
public class NotImplementedException extends InstructionException {

	/**
	 * Constructor general
	 */
	public NotImplementedException() {
		super();
	}

	/**
	 * Constructor con mensaje de error
	 * 
	 * @param message
	 */
	public NotImplementedException(String message) {
		super(message);
	}

}
