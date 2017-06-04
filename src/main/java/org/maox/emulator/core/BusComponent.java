package org.maox.emulator.core;

import org.maox.emulator.exceptions.IllegalAddressException;

/**
 * BusComponent de datos de 8 bits, interfaz para definir las operaciones de lectura y escritura entre los componentes
 * 
 * @author Alex Orgaz
 * 
 */
public interface BusComponent {

	/**
	 * Obtiene el tamaño de la zona de memoria gestionada por el componente
	 * 
	 * @return
	 */
	public int getSize();

	/**
	 * Devuelve la dirección de inicio de la información contenida en el Component
	 * 
	 * @return starAddress dirección de inicio
	 */
	public int getStartAddress();

	/**
	 * Lectura de un byte en una dirección determinada
	 * 
	 * @param address Dirección de Memoria
	 * @return byte leído
	 * @throws IllegalAddressException
	 */
	public byte read(int address) throws IllegalAddressException;

	/**
	 * Escritura de un byte en una dirección determinada
	 * 
	 * @param address Dirección de Memoria
	 * @param data byte a escribir
	 * @throws IllegalAddressException
	 */
	public void write(int address, byte data) throws IllegalAddressException;
}
