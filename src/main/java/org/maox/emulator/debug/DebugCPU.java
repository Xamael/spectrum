package org.maox.emulator.debug;

import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;

/**
 * Interfaz con los métodos necesarios para realizar un debug sobre las instrucciones ejecutadas en la CPU
 * 
 * @author Alex Orgaz
 * 
 */
public interface DebugCPU {

	/**
	 * Lee la instrucción de una dirección de memoria, leyendo en caso necesario las posiciones de memoria necesarias
	 * pero no avanza el PC.
	 * <p>
	 * Usado para ver las instrucciones en debug antes de ejecutarlas
	 * 
	 * @param address dirección donde está la instrucción
	 * @return Ciclos consumidos por la instrucción.
	 * @throws InstructionException
	 * @throws IllegalAddressException
	 */
	public byte debugInstruction(int address) throws InstructionException, IllegalAddressException;

}
