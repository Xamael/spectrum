package org.maox.emulator.exceptions;

/**
 * Excepcion de Acceso a una posición de memoria fuera de rango
 * 
 * @author Alex Orgaz
 * 
 */
@SuppressWarnings("serial")
public class IllegalAddressException extends Exception {

	/**
	 * Constructor general
	 */
	public IllegalAddressException() {
		super();
	}

	/**
	 * Constructor con mensaje de error
	 * 
	 * @param message
	 */
	public IllegalAddressException(String message) {
		super(message);
	}

}
