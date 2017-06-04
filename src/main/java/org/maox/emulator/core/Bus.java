package org.maox.emulator.core;

import org.maox.emulator.exceptions.IllegalAddressException;

/**
 * Bus donde se pueden insertar BusComponents como subbuses
 * 
 * @author Alex Orgaz
 * 
 */
public interface Bus {

	/**
	 * Inserta un Componente a partir de una dirección determinada
	 * 
	 * @param component Componente a insertar
	 */
	public void addComponent(BusComponent component) throws IllegalAddressException;

	/**
	 * Elimina un Componente del Bus
	 * 
	 * @param component
	 */
	public void removeComponent(BusComponent component);

}
