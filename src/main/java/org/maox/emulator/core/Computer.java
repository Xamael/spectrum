package org.maox.emulator.core;

import java.awt.image.BufferedImage;

import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;

/**
 * Maquina Virtual
 * 
 * @author Alex Orgaz
 * 
 */
public abstract class Computer {

	/**
	 * Vuelca la información para debug
	 * 
	 * @return
	 */
	abstract public String dump();

	/**
	 * Ejecuta una instrucción en la maquina virtual
	 * 
	 * @return número de ciclos usados
	 * @throws InstructionException
	 * @throws IllegalAddressException
	 */
	abstract public int execute() throws InstructionException, IllegalAddressException;

	/**
	 * Devuelve un frame generado por la unidad gráfica
	 * 
	 * @return Frame dibujado
	 */
	abstract public BufferedImage getFrame();

	/**
	 * Número de frames por segundo que usa la maquina para el refresco de la pantalla
	 * 
	 * @return
	 */
	abstract public int getFramesPerSecond();

	/**
	 * Velocidad a la que funciona la maquina virtual
	 * 
	 * @return
	 */
	abstract public int getHerz();
}
