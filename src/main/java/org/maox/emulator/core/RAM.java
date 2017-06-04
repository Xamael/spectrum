package org.maox.emulator.core;

import org.maox.emulator.debug.Hex;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memoria RAM, de una capacidad determinada con sus funciones de lectura y escritura con 8 Bits de bus de Datos
 * 
 * @author Alex Orgaz
 * 
 */
public class RAM implements BusComponent {

	/* Log */
	final private static Logger logger = LoggerFactory.getLogger(RAM.class);

	/* Constantes */
	public final static int KB = 1024;
	public final static int MB = 1048576;

	/* Array de bytes de la memoria */
	protected byte memory[] = null;

	/* Tamaño de la memoria */
	final private int size;
	final private int startAddress;

	/**
	 * Constructor de una memoria con un tamaño en bytes definido
	 * 
	 * @param size
	 */
	public RAM(int size) {
		this(size, 1, 0);
	}

	/**
	 * Constructor de una memoria con un tamaño en bytes definido
	 * 
	 * @param size
	 * @param startAddress dirección de memoria inicial
	 */
	public RAM(int size, int startAddress) {
		this(size, 1, startAddress);
	}

	/**
	 * Constructor de una memoria con un tamaño definido
	 * 
	 * @param size tamaño
	 * @param type KB, MB
	 * @param startAddress dirección de memoria inicial
	 */
	public RAM(int size, int type, int startAddress) {
		this.size = size * type;
		this.startAddress = startAddress;

		/* Se reserva el espacio de memoria */
		memory = new byte[size * type];
		logger.info("Created Memory {} KBytes", getSize(KB));
	}

	/**
	 * Vuelca la memoria a un Stream
	 */
	public String dump() {
		return dump(0, getSize());
	}

	/**
	 * Vuelca una región de memoria a un Stream
	 */
	public String dump(int beginIndex, int endIndex) {
		StringBuilder dump = new StringBuilder();
		char[] space = new char[3];
		space[0] = 'h';
		space[1] = ':';
		space[2] = ' ';

		for (int base = beginIndex; base < endIndex; base = base + 16) {
			/* Dirección de Memoria */
			dump.append(Hex.addressToChar(base + startAddress));
			dump.append(space);

			char[] line = new char[16 * 3];
			for (int idx = 0; idx < +16; idx++) {
				char[] hexbyte;
				try {
					hexbyte = Hex.byteToChar(read(base + idx + startAddress));
					line[idx * 3] = hexbyte[0];
					line[idx * 3 + 1] = hexbyte[1];
					line[idx * 3 + 2] = ' ';
				} catch (IllegalAddressException e) {
				}
			}

			line[47] = '\n';
			dump.append(line);
		}

		dump.trimToSize();
		return dump.toString();
	}

	/* Tamaño de la memoria en bytes */
	/**
	 * Obtiene el tamaño máximo de la memoria
	 * 
	 * @return tamaño en bytes
	 */
	@Override
	public int getSize() {
		return size;
	}

	/**
	 * Obtiene el tamaño máximo de la memoria en KB o MB
	 * 
	 * @return tamaño en MB
	 */
	public int getSize(int mode) {
		return size / mode;
	}

	@Override
	public int getStartAddress() {
		return startAddress;
	}

	/**
	 * Lectura de un byte de una dirección determinada
	 * 
	 * @param address
	 * @return
	 * @throws IllegalAddressException
	 */
	@Override
	public byte read(int address) throws IllegalAddressException {
		/* Comprobación si la dirección de memoria es valida */
		if (address - startAddress >= getSize())
			throw new IllegalAddressException("Error reading at " + Hex.addressToString(address) + "h. Max memory: "
					+ Hex.addressToString(getSize() - 1) + "h");

		return memory[address - startAddress];
	}

	/**
	 * Escritura de un byte de una dirección determinada
	 * 
	 * @param address
	 * @param data
	 * @throws IllegalAddressException
	 */
	@Override
	public void write(int address, byte data) throws IllegalAddressException {
		/* Comprobación si la dirección de memoria es valida */
		if (address - startAddress >= getSize())
			throw new IllegalAddressException("Error writing at " + Hex.addressToString(address) + "h. Max memory: "
					+ Hex.addressToString(getSize() - 1) + "h");

		memory[address - startAddress] = data;
	}
}
