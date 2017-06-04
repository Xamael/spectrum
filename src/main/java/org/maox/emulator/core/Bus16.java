package org.maox.emulator.core;

import org.maox.emulator.debug.Hex;
import org.maox.emulator.exceptions.IllegalAddressException;

/**
 * Bus de 16 Bits de direccionamiento (esto permite hasta 64 KB de capacidad), 8 bits de datos
 * <p>
 * Permite la inclusión de hasta 16 componetes de un mínimo de 1 KB
 * 
 * @author Alex Orgaz
 * 
 */
public class Bus16 implements Bus, BusComponent {

	// Constantes
	final private static int MAX_COMPONENTS = 16;

	/* Componentes enganchados al Bus */
	private BusComponent components[];
	/* Atributos suma de los componentes */
	private int size;
	private int startAddress;

	/**
	 * Constructor, inicializa el array de componentes
	 */
	public Bus16() {
		super();
		components = new BusComponent[MAX_COMPONENTS];
		size = 0;
		startAddress = 0xFFFF;
	}

	@Override
	public void addComponent(BusComponent component) throws IllegalAddressException {
		// La asignación se realizará insertando el componente en el array de componentes
		// a partir de la dirección de memoria incial y tamaño del componente
		int begin = component.getStartAddress() >> 12 & 0x000F;
		int end = (component.getStartAddress() + component.getSize() - 1) >> 12 & 0x000F;

		for (int idx = begin; idx <= end; idx++) {
			if (components[idx] != null)
				throw new IllegalAddressException("Memory block  " + Hex.byteToHex((byte) idx) + " is already asigned.");
			components[idx] = component;
		}

		size += component.getSize();
		if (startAddress > component.getStartAddress()) {
			startAddress = component.getStartAddress();
		}
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int getStartAddress() {
		return startAddress;
	}

	@Override
	public byte read(int address) throws IllegalAddressException {
		int idx = address >> 12 & 0x000F;
		return components[idx].read(address);
	}

	@Override
	public void removeComponent(BusComponent component) {
		int begin = component.getStartAddress() >> 12 & 0x000F;
		int end = (component.getStartAddress() + component.getSize() - 1) >> 12 & 0x000F;

		for (int idx = begin; idx <= end; idx++) {
			components[idx] = null;
		}

		size -= component.getSize();

		int idx = 0;
		do {
			idx++;
		} while (components[idx] != null || idx == MAX_COMPONENTS);

		if (idx == MAX_COMPONENTS) {
			startAddress = 0xFFFF;
		} else {
			startAddress = components[idx].getStartAddress();
		}
	}

	@Override
	public void write(int address, byte data) throws IllegalAddressException {
		int idx = address >> 12 & 0x000F;
		components[idx].write(address, data);
	}

}
