package org.maox.emulator.core;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.maox.emulator.debug.Hex;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memoria ROM, de una capacidad determinada con sus funciones de lectura y escritura con 8 Bits de bus de Datos
 * 
 * @author Alex Orgaz
 * 
 */
public class ROM extends RAM {

	/* Log */
	final private static Logger logger = LoggerFactory.getLogger(ROM.class);

	/**
	 * Constructor de una memoria con un tamaño en bytes definido
	 * 
	 * @param size
	 */
	public ROM(int size) {
		super(size);
		logger.info("ROM Memory");
	}

	/**
	 * Constructor de una memoria con un tamaño definido
	 * 
	 * @param size
	 * @param type
	 * @param startAddress
	 */
	public ROM(int size, int type, int startAddress) {
		super(size, type, startAddress);
		logger.info("ROM Memory");
	}

	/**
	 * Carga en memoria el contenido de un fichero de entrada
	 * 
	 * @param fileROM Fichero ROM
	 * @return número de bytes leídos
	 * @throws IOException
	 */
	public int load(File fileROM) throws IOException {
		return load(new FileInputStream(fileROM));
	}

	/**
	 * Carga en memoria el contenido de un flujo de entrada
	 * 
	 * @param rom Fichero ROM
	 * @return Byte Leidos
	 * @throws IOException
	 */
	public int load(InputStream isROM) throws IOException {
		// Stream de entrada
		DataInputStream din = new DataInputStream(isROM);

		// Se lee el flujo de entrada
		int numBytes = din.read(memory);

		logger.debug("{} bytes readed", numBytes);
		return numBytes;
	}

	/**
	 * Carga en memoria un fichero ROM
	 * 
	 * @param fileName Nombre del fichero dentro del directorio de recursos
	 * @return Byte Leidos
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public int load(String fileName) throws IOException {
		logger.info("Loading {}", fileName);

		// Recurso de lectura, si no se encuentra será un null
		URL urlROM = this.getClass().getClassLoader().getResource(fileName);
		if (urlROM == null)
			throw new FileNotFoundException(fileName);

		// Instancia del fichero ROM
		File fileROM;
		try {
			fileROM = new File(urlROM.toURI());
		} catch (URISyntaxException e) {
			IOException eURI = new IOException(e);
			throw eURI;
		}

		return load(fileROM);
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
		// Sólo lectura
		throw new IllegalAddressException("Error writing at 0x" + Hex.addressToString(address) + ". ROM memory: 0x"
				+ Hex.addressToString(getStartAddress()) + " - 0x" + Hex.addressToString(getSize() - 1));
	}
}
