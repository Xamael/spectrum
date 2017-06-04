package org.maox.emulator.virtual;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;

import org.maox.emulator.core.Bus16;
import org.maox.emulator.core.Computer;
import org.maox.emulator.core.RAM;
import org.maox.emulator.core.ROM;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;
import org.maox.emulator.peripheral.z80.ULA;

/**
 * Spectrum Z80 virtual
 * 
 * @author Alex Orgaz
 * 
 */
public class Spectrum extends Computer {

	/* Componentes del Ordenador */
	Z80 cpu;
	ULA ula;

	/* Crea un Sinclair Spectrum ZX Virtual */
	public Spectrum() throws IOException, UnknowInstructionException, IllegalAddressException {
		super();
		init();
	}

	@Override
	public String dump() {
		return cpu.dump();
	}

	@Override
	public int execute() throws InstructionException, IllegalAddressException {
		// TODO Auto-generated method stub
		return cpu.fetch();
	}

	@Override
	public BufferedImage getFrame() {
		return ula.getFrame();
	}

	@Override
	public int getFramesPerSecond() {
		return ula.getFramesPerSecond();
	}

	@Override
	public int getHerz() {
		return cpu.getHerz();
	}

	/**
	 * Inicialización del Spectrum
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws UnknowInstructionException
	 * @throws IllegalAddressException
	 */
	private void init() throws IOException, UnknowInstructionException, IllegalAddressException {
		/* Se crea una CPU Z80 */
		cpu = new Z80();
		/* Se le añade la ULA */
		ula = new ULA();

		/* Se crean los módulos de memoria asociados */
		/* Se carga la ROM del Spectrum 48K (48K RAM + 16K ROM) */
		/*
		 * Al segundo bloque de 16 se accederá a traves de la ULA, ya que comparte
		 * esa zona de memoria para generar la señal de video
		 */
		ROM rom = new ROM(16, RAM.KB, 0x0000);
		rom.load("roms/ZX48.rom");
		RAM ram = new RAM(16, RAM.KB, 0x4000);
		ula.addComponent(ram);
		RAM exp = new RAM(32, RAM.KB, 0x8000);

		Bus16 bus = new Bus16();
		bus.addComponent(rom);
		bus.addComponent(ula);
		bus.addComponent(exp);

		/* Se le asigna la memoria a la CPU */
		cpu.setDataBus(bus);

		cpu.setULA(ula);
	}

}
