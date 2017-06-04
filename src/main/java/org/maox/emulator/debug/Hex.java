package org.maox.emulator.debug;

/**
 * Utilidades para el trabajo con datos binarios
 * 
 * @author Alex Orgaz
 * 
 */
public class Hex {

	/* Para la transformación en el dump de memoria */
	final public static char[] hexArrayUpper = "0123456789ABCDEF".toCharArray();
	final public static char[] hexArrayLower = "0123456789abcdef".toCharArray();

	/**
	 * Transforma una dirección de memoria en Hexadecimal 00000000h
	 * 
	 * @param address
	 * @return
	 */
	public static char[] addressToChar(int address) {
		char[] res = new char[4];

		// res[0] = hexArrayLower[address >>> 28];
		// res[1] = hexArrayLower[address >>> 24 & 0x000F];
		// res[2] = hexArrayLower[address >>> 20 & 0x000F];
		// res[3] = hexArrayLower[address >>> 16 & 0x000F];
		res[0] = hexArrayLower[address >>> 12 & 0x000F];
		res[1] = hexArrayLower[address >>> 8 & 0x000F];
		res[2] = hexArrayLower[address >>> 4 & 0x000F];
		res[3] = hexArrayLower[address & 0x000F];

		return res;
	}

	/**
	 * Transforma una dirección de memoria en Hexadecimal
	 * 
	 * @param address
	 * @return
	 */
	public static String addressToString(int address) {
		return new String(addressToChar(address));
	}

	/**
	 * Transforma una cadena de 0 y 1 a un tipo byte
	 * 
	 * @param data Cadena binaria
	 * @return
	 * @deprecated usar: (byte)0bxxxxxxxx
	 */
	@Deprecated
	public static byte binToByte(String data) {
		int parse = Integer.parseInt(data, 2);
		return (byte) parse;
	}

	/**
	 * Tranforma un byte a códgio Hexadecimal (2caracteres) 00h
	 * 
	 * @param data Byte de entrada
	 * @return Byte en Hexadecimal
	 */
	public static char[] byteToChar(byte data) {
		char[] res = new char[2];

		int v = data & 0xFF;
		res[0] = hexArrayUpper[v >>> 4 & 0x0F];
		res[1] = hexArrayUpper[v & 0x0F];

		return res;
	}

	/**
	 * Transforma un byte en un código Hexadecimal (2 caracteres)
	 * 
	 * @param opc
	 * @return
	 */
	public static String byteToHex(byte data) {
		return new String(byteToChar(data));
	}

	/**
	 * Transforma una cadena hexadecimal a un tipo dirección de 16 bits
	 * 
	 * @param data Cadena binaria
	 * @return
	 */
	public static int hexToAddress16(String data) {
		int parse = Integer.parseInt(data, 16);
		return (short) (parse & 0x0000FFFF);
	}

	/**
	 * Transforma una cadena hexadecimal a un tipo byte
	 * 
	 * @param data Cadena binaria
	 * @return
	 */
	public static byte hexToByte(String data) {
		int parse = Integer.parseInt(data, 16);
		return (byte) (parse & 0x000000FF);
	}

	/**
	 * Tranforma un byte a códgio Hexadecimal (2caracteres) 00h
	 * 
	 * @param data Byte de entrada
	 * @return Byte en Hexadecimal
	 */
	public static char[] shortToChar(short data) {
		char[] res = new char[4];

		int v = data & 0xFFFF;
		res[0] = hexArrayUpper[v >> 12 & 0x000F];
		res[1] = hexArrayUpper[v >> 8 & 0x000F];
		res[2] = hexArrayUpper[v >> 4 & 0x000F];
		res[3] = hexArrayUpper[v & 0x00F];

		return res;
	}

	/**
	 * Transforma un byte en un código Hexadecimal (2 caracteres)
	 * 
	 * @param opc
	 * @return
	 */
	public static String shortToHex(short data) {
		return new String(shortToChar(data));
	}
}
