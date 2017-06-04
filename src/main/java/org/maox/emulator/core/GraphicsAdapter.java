package org.maox.emulator.core;

import java.awt.image.BufferedImage;

/**
 * Dispositivo que genera un gráfico para mostrarlo por pantalla
 * 
 * @author Alex Orgaz
 * 
 */
public interface GraphicsAdapter {

	/**
	 * Genera un gráfico para pintarlo por pantalla
	 * 
	 * @return Frame
	 */
	public BufferedImage getFrame();
}
