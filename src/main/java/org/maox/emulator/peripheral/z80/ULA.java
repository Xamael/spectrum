package org.maox.emulator.peripheral.z80;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.maox.emulator.core.Bus;
import org.maox.emulator.core.BusComponent;
import org.maox.emulator.core.GraphicsAdapter;
import org.maox.emulator.core.RAM;
import org.maox.emulator.exceptions.IllegalAddressException;

/**
 * Unidad auxiliar del Spectrum encargada de la I/O, sonido y gráficos
 * <p>
 * Se le asginará un bloque de memoria de 16 KB para que lo gestione y pueda generar la señal de vídeo desde el.
 * 
 * @author Alex Orgaz
 * 
 */
public class ULA implements Bus, BusComponent, GraphicsAdapter {

	// Imagen de pantalla
	private BufferedImage screen;
	// Memoria 16KB asignada a la ULA
	private BusComponent ram;

	/**
	 * Constructor
	 */
	public ULA() throws IOException {
		super();

		// Eliminar la excpeción
		screen = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/test.png"));
	}

	@Override
	public void addComponent(BusComponent component) throws IllegalAddressException {
		if (component.getSize() != 16 * RAM.KB)
			throw new IllegalAddressException("ULA Memory block not valid. Must be 16KB.");

		if (ram != null)
			throw new IllegalAddressException("ULA Memory block  is already asigned.");

		ram = component;
	}

	/**
	 * EL ZX tiene una pantalla de 320 x 240 de los cuales sólo
	 * una parte de 256 x 192 pixeles es el bitmap y el resto es el borde
	 * <p>
	 * El bitmap se encuentra en la zona de memoria 0x4000 a 0x57FF cada byte define una secuencia de 8 pixeles
	 * horizontales.
	 * <p>
	 * Una segunda sección desde la 0x5800 a 0x5AFF contiene un byte de atributo por cada bloque de 8x8 pixles.
	 */
	@Override
	public BufferedImage getFrame() {
		return screen;
	}

	/**
	 * Devuelve el número de frames por segundo de refresco de la pantalla
	 * 
	 * @return
	 */
	public int getFramesPerSecond() {
		return 50;
	}

	@Override
	public int getSize() {
		return ram.getSize();
	}

	@Override
	public int getStartAddress() {
		return ram.getStartAddress();
	}

	@Override
	public byte read(int address) throws IllegalAddressException {
		return ram.read(address);
	}

	@Override
	public void removeComponent(BusComponent component) {
		ram = null;
	}

	@Override
	public void write(int address, byte data) throws IllegalAddressException {
		ram.write(address, data);
	}

	/**
	 * Procesa una petición de escritura de I/O
	 * 
	 * @param address
	 * @param data
	 * @throws IllegalAddressException
	 */
	public void writeIO(int address, byte data) throws IllegalAddressException {
		// TODO
	}

}
