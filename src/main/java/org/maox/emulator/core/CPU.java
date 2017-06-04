package org.maox.emulator.core;

import org.maox.emulator.debug.DebugCPU;
import org.maox.emulator.debug.Hex;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulación de una CPU 8 bits, deberá proporcionar acceso a su estructura interna y al estado interno de los
 * registros
 * 
 * @author Alex Orgaz
 * 
 */
public abstract class CPU implements DebugCPU {

	final private static Logger logger = LoggerFactory.getLogger(CPU.class);

	/* Memoria RAM asociada a la CPU para realizar las operaciones */
	private BusComponent dataBus;
	/* Ciclos por segundo de velocidad del procesador */
	private int herz;
	/* Mapeo de códigos de instrucción OPC a sus clases correcpondientes */
	private InstructionMap mapOPC;

	/*
	 * Registros Especiales
	 * Se utiliza un int (32 bits) para el direccionamiento de memoria aunque sólo se necesiten
	 * 16 bits ya que en Java no hay unsigned
	 */
	private int SP; /* Puntero de Pila */
	private int PC; /* Contador de programa */
	private byte IR; /* Registro de Instrucción */

	@Override
	public byte debugInstruction(int address) throws InstructionException, IllegalAddressException {
		Instruction inst = getInstructionMap().getInstructionComplete(address);
		String spaces = "                  ".substring(inst.getAssemblyDecoded().length());

		logger.debug("{} : {}{} {}{}{}", Hex.addressToString(address), inst.getStringOpCodes(),
				"              ".substring(0, (4 - inst.getNumOpcodes()) * 3), inst.getAssemblyDecoded(), spaces,
				dump());

		// Parada manual
		if (address == 0xFFFF) {
			@SuppressWarnings("unused")
			int stop = 0;
		}

		return inst.getCycles();
	}

	/**
	 * Vuelca el contenido de los registro de la CPU para el DEBUG
	 * 
	 * @return
	 */
	abstract public String dump();

	/**
	 * Se realiza un fech con ejecucións de la instrucción que este en la memoria almacenada en el registro PC
	 * 
	 * @return número de ciclos consumidos en la ejecución
	 * @throws UnknowInstructionException
	 */
	public byte fetch() throws InstructionException, IllegalAddressException {
		/* Sólo para debug, realiza una precarga de la instrucción para ver todos sus parametros */
		debugInstruction(getPC());
		/* Se lee la posición de memoria del contador de programa y se almacena en el IR */
		loadIR();
		/* Se decodifica y ejecuta la instrucción */
		return getInstructionMap().getInstruction(IR).execute();
	}

	/**
	 * Obtiene la memoria asociada al procesador
	 * 
	 * @return
	 */
	public BusComponent getDataBus() {
		return dataBus;
	}

	/**
	 * Obtiene los Hz del procesador
	 * 
	 * @return número de Hz
	 */
	public int getHerz() {
		return herz;
	}

	/**
	 * Devuelve el mapa de instrucciones del procesador
	 * 
	 * @return the mapOPC
	 */
	public InstructionMap getInstructionMap() {
		return mapOPC;
	}

	/**
	 * @return Registro IR, Registro de Instrucción
	 */
	public byte getIR() {
		return IR;
	}

	/**
	 * @return Registro PC, ProgramCounter
	 */
	public int getPC() {
		return PC;
	}

	/**
	 * @return Registro SP, StackPointer
	 */
	public int getSP() {
		return SP;
	}

	/**
	 * Se carga la instrucción del (PC) en el IR y se avanza el PC
	 * 
	 * @throws UnknowInstructionException
	 */
	public void loadIR() throws IllegalAddressException {
		/* Se lee la posición de memoria del contador de programa y se almacena en el IR */
		IR = read8(getPC());
		// logger.trace("Fetch opCode: {}", Hex.byteToHex(IR));
		/* Se incrementa el PC */
		PC++;
	}

	/**
	 * Lee de la memoria 2 bytes desde una posición determinada
	 * 
	 * @param address
	 * @return bytes leídos
	 * @throws IllegalAddressException
	 */
	public int read16(int address) throws IllegalAddressException {
		int low = dataBus.read(address) & 0x000000FF;
		int high = dataBus.read(address + 1) << 8 & 0x0000FF00;

		return high | low;
	}

	/**
	 * Lee de la memoria un byte de una posición determinada
	 * 
	 * @param address
	 * @return byte leído
	 * @throws IllegalAddressException
	 */
	public byte read8(int address) throws IllegalAddressException {
		return dataBus.read(address);
	}

	/**
	 * Establece la memoria RAM que se usará
	 * 
	 * @param bus
	 */
	public void setDataBus(BusComponent bus) {
		this.dataBus = bus;
	}

	/**
	 * Establece los Hz del procesador
	 * 
	 * @param herz
	 */
	public void setHerz(int herz) {
		logger.info("CPU speed {} MHz", (float) herz / 1000);
		this.herz = herz;
	}

	/**
	 * Establece el mapa de instrucciones del procesador
	 * 
	 * @param mapOPC the mapOPC to set
	 */
	public void setInstructionMap(InstructionMap mapOPC) {
		this.mapOPC = mapOPC;
	}

	/**
	 * @param programCounter the PC to set
	 */
	public void setPC(int programCounter) {
		PC = programCounter;
	}

	/**
	 * @param stackPointer the SP to set
	 */
	public void setSP(int stackPointer) {
		SP = stackPointer;
	}

	/**
	 * Escribe un dato 16 bits en la posición de memoria apuntada por las 2 posiciones de memoria consecutivas
	 * (nn + 1) ← High, (nn) ← Low
	 * 
	 * @param address Dirección de Memoria
	 * @param data
	 * @throws IllegalAddressException
	 */
	public void write16(int address, int data) throws IllegalAddressException {
		dataBus.write(address, (byte) (data & 0x000000FF));
		dataBus.write(address + 1, (byte) (data >> 8 & 0x000000FF));
	}

	/**
	 * Escribe en memoria un byte en una posición determinada
	 * 
	 * @param address
	 * @param data
	 * @throws IllegalAddressException
	 */
	public void write8(int address, byte data) throws IllegalAddressException {
		dataBus.write(address, data);
	}

}
