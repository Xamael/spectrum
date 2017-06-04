package org.maox.emulator.cpu.z80;

import org.maox.emulator.core.BusComponent;
import org.maox.emulator.core.CPU;
import org.maox.emulator.debug.Hex;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;
import org.maox.emulator.peripheral.z80.ULA;

/**
 * Procesador Zilog Z80
 * 
 * @author Alex Orgaz
 * 
 */
public class Z80 extends CPU {

	/* Tamaño de las tablas de mapeo de instrucciones 2^8 */
	final public static int MAP_SIZE = 256;

	/* Nomemclatura de Flags del registro F */
	final public static int FLAG_S = 7;
	final public static int FLAG_Z = 6;
	final public static int FLAG_5 = 5;
	final public static int FLAG_H = 4;
	final public static int FLAG_3 = 3;
	final public static int FLAG_PV = 2;
	final public static int FLAG_N = 1;
	final public static int FLAG_C = 0;

	/* Modos de direccionamiento de las instrucciones */
	final public static int REG_A = 0;
	final public static int REG_B = 1;
	final public static int REG_C = 2;
	final public static int REG_D = 3;
	final public static int REG_E = 4;
	final public static int REG_F = 5;
	final public static int REG_H = 6;
	final public static int REG_L = 7;
	final public static int REG_I = 8;
	final public static int REG_R = 9;
	final public static int REG_AF = 10;
	final public static int REG_BC = 11;
	final public static int REG_DE = 12;
	final public static int REG_HL = 13;
	final public static int REG_SP = 14;
	final public static int REG_IX = 15;
	final public static int REG_IY = 16;
	final public static int DIRECT_8 = 17;
	final public static int DIRECT_16 = 18;
	final public static int ADDR_BC = 19;
	final public static int ADDR_DE = 20;
	final public static int ADDR_HL = 21;
	final public static int ADDR_SP = 22;
	final public static int ADDR_NN = 23;
	final public static int ADDR_NN_16 = 24;
	final public static int IX_D = 25;
	final public static int IY_D = 26;
	final public static int DESPLACEMENT = 27;
	final public static int REG_AF_ALT = 28;

	/* Nombres de los registros */
	final public static String DIR_NAMES[] = { "A", "B", "C", "D", "E", "F", "H", "L", "I", "R", "AF", "BC", "DE",
			"HL", "SP", "IX", "IY", "n", "nn", "(BC)", "(DE)", "(HL)", "(SP)", "(nn)", "(nn)", "(IX + d)", "(IY + d)",
			"+e", "AF'" };

	/* Condiciones */
	final public static int COND_NZ = 0;
	final public static int COND_Z = 1;
	final public static int COND_NC = 2;
	final public static int COND_C = 3;
	final public static int COND_PO = 4;
	final public static int COND_PE = 5;
	final public static int COND_P = 6;
	final public static int COND_M = 7;

	/* Nombres de las condiciones */
	final public static String COND_NAMES[] = { "NZ", "Z", "NC", "C", "PO", "PE", "P", "M" };

	/* Modos de Interrupción */
	final public static int INTERRUPT_MODE_0 = 0;
	final public static int INTERRUPT_MODE_1 = 1;
	final public static int INTERRUPT_MODE_2 = 2;

	/* Registros Principales */
	private byte A; /* Acumulador */
	private byte F; /* Flags */
	private byte B; /* Par BC auxiliar */
	private byte C;
	private byte D; /* Par DE auxiliar */
	private byte E;
	private byte H; /* Par HL dirección memoria */
	private byte L;

	/* Registros Especiales */
	private byte I; /* Interrupt Page Address */
	private byte R; /* RAM Refresh */

	/* Registros Alternativos */
	private byte A_alt; /* Acumulador */
	private byte F_alt; /* Flags */
	private byte B_alt; /* Par BC auxiliar */
	private byte C_alt;
	private byte D_alt; /* Par DE auxiliar */
	private byte E_alt;
	private byte H_alt; /* Par HL dirección memoria */
	private byte L_alt;

	/* Registros Indices */
	private int IX;
	private int IY;

	/* Flipflop de control de interrupciones */
	private boolean IFF1 = false;
	private boolean IFF2 = false;

	/* Uncommitted Logic Array (ULA) encargada del I/O */
	private ULA ULA;

	/**
	 * Constructor base
	 * 
	 * @throws UnknowInstructionException
	 * 
	 */
	public Z80() throws UnknowInstructionException {
		super();
		init();
	}

	/**
	 * Suma un byte al acumulador teniendo en cuenta el flag de acarreo
	 * 
	 * @param b Byte a sumar al registro A
	 * @param c Byte de acarreo
	 * @return resultado (registro A)
	 */
	public byte addWithCarry(byte b, byte c) {
		// Se suma el byte b al acumulador y se almacena en un short para saber después
		// si ha habido acarreo o overflow
		byte res = (byte) (A + b + c);

		// HalfCarry
		boolean bHalfCarry = (((A & 0x0F) + (b & 0x0F) + (c & 0x0F)) & 0x10) == 0x10;
		// Overflow (Si son signos iguales)
		boolean bOverFlow = false;
		if ((((A ^ b) ^ 0x80) & 0x80) == 0x80) {
			// Overflow si el signo del resultado es distinto de los operandos
			bOverFlow = ((res ^ A) & 0x80) != 0;
		}
		// Carry
		byte carry = 0;

		if (c != 0) {
			carry = (byte) ((A & 0xFF) >= (0xFF - b & 0xFF) ? 1 : 0);
		} else {
			carry = (byte) ((A & 0xFF) > (0xFF - b & 0xFF) ? 1 : 0);
		}

		A = res;

		// Se establecen los flag
		setFlagSigned(res);
		setFlagZero(res);
		setFlag(Z80.FLAG_H, bHalfCarry);
		setFlag(Z80.FLAG_PV, bOverFlow);
		setFlag(Z80.FLAG_N, false);

		F &= ~(1 << FLAG_C); // Set a bit to 0
		F |= (carry << FLAG_C); // Set a bit to carry

		// Flags sin documentar
		setFlag35(res);

		return A;
	}

	/**
	 * Suma un byte teniendo en cuenta el flag de acarreo
	 * 
	 * @param a registro a sumar (16 bits)
	 * @param b registro a sumar (16 bits)
	 * @param c Byte de acarreo
	 * @return resultado 16 bits
	 */
	public int addWithCarry16(int a, int b, byte c) {
		// Se suma el resgitro b y se almacena para saber después si ha habido acarreo o overflow
		int res = (a + b + c);

		// HalfCarry
		boolean bHalfCarry = (((a & 0x0F00) + (b & 0x0F00) + (c & 0x0F00)) & 0x1000) == 0x1000;

		// Overflow (Si son signos iguales)
		boolean bOverFlow = false;
		if ((((a ^ b) ^ 0x8000) & 0x8000) == 0x8000) {
			// Overflow si el signo del resultado es distinto de los operandos
			bOverFlow = ((res ^ a) & 0x8000) != 0;
		}
		// Carry
		byte carry = 0;

		if (c != 0) {
			carry = (byte) ((a & 0xFFFF) >= (0xFFFF - b & 0xFFFF) ? 1 : 0);
		} else {
			carry = (byte) ((a & 0xFFFF) > (0xFFFF - b & 0xFFFF) ? 1 : 0);
		}

		// Se establecen los flag
		/* Si es negativo se establece S */
		setFlag(Z80.FLAG_S, (res >> 15 & 0x01) == 0x01);
		/* Si es cero se establece Z */
		setFlag(Z80.FLAG_Z, res == 0x00);

		setFlag(Z80.FLAG_H, bHalfCarry);
		setFlag(Z80.FLAG_PV, bOverFlow);
		setFlag(Z80.FLAG_N, false);
		// setFlag(Z80.FLAG_C, bCarry);
		F &= ~(1 << FLAG_C); // Set a bit to 0
		F |= (carry << FLAG_C); // Set a bit to carry

		// Flags sin documentar
		setFlag35((byte) (res >> 8 & 0x00FF));

		return res;
	}

	@Override
	public String dump() {
		StringBuilder registers = new StringBuilder();
		// registers.append(" PC   SP     AF    BC   DE      HL   IX   IY    SZ5H3PNC\n");
		registers.append(Hex.shortToHex((short) getPC()));
		registers.append(" ");
		registers.append(Hex.shortToHex((short) getSP()));
		registers.append("   ");
		registers.append(Hex.byteToHex(getA()));
		registers.append(Hex.byteToHex(getF()));
		registers.append("  ");
		registers.append(Hex.byteToHex(getB()));
		registers.append(Hex.byteToHex(getC()));
		registers.append(" ");
		registers.append(Hex.byteToHex(getD()));
		registers.append(Hex.byteToHex(getE()));
		registers.append(" ");
		registers.append(Hex.byteToHex(getH()));
		registers.append(Hex.byteToHex(getL()));
		registers.append("    ");
		registers.append(Hex.shortToHex((short) getIX()));
		registers.append(" ");
		registers.append(Hex.shortToHex((short) getIY()));
		registers.append("   ");
		registers.append(getFlag(FLAG_S) ? "S" : " ");
		registers.append(getFlag(FLAG_Z) ? "Z" : " ");
		registers.append(getFlag(FLAG_5) ? "5" : " ");
		registers.append(getFlag(FLAG_H) ? "H" : " ");
		registers.append(getFlag(FLAG_3) ? "3" : " ");
		registers.append(getFlag(FLAG_PV) ? "P" : " ");
		registers.append(getFlag(FLAG_N) ? "N" : " ");
		registers.append(getFlag(FLAG_C) ? "C" : " ");

		return registers.toString();
	}

	/**
	 * Intercambia el contenido de los registro A y F con sus alternativos
	 */
	public void exchangeAF() {
		byte temp = A;
		A = A_alt;
		A_alt = temp;

		temp = F;
		F = F_alt;
		F_alt = temp;
	}

	/**
	 * Intercambia el contenido de los registro DE con HL
	 */
	public void exchangeDEHL() {
		byte temp = D;
		D = H;
		H = temp;

		temp = E;
		E = L;
		L = temp;
	}

	/**
	 * Intercambia el contenido del puntero de pila con algún registro
	 */
	public void exchangeSP(int mode) {
		// TODO
		int stop = 0;
	}

	/**
	 * Intercambia el contenido de todos los registros, menos el A y F, con sus alternativos
	 */
	public void exchangeX() {
		byte temp = B;
		B = B_alt;
		B_alt = temp;

		temp = C;
		C = C_alt;
		C_alt = temp;

		temp = D;
		D = D_alt;
		D_alt = temp;

		temp = E;
		E = E_alt;
		E_alt = temp;

		temp = H;
		H = H_alt;
		H_alt = temp;

		temp = L;
		L = L_alt;
		L_alt = temp;
	}

	/**
	 * @return Registro A
	 */
	public byte getA() {
		return A;
	}

	/**
	 * @return Registro B
	 */
	public byte getB() {
		return B;
	}

	/**
	 * @return Devuelve el contenido de los registros B y C en 16 bits (Registro BC)
	 */
	public int getBC() {
		int B = getB() << 8 & 0x0000FF00;
		int C = getC() & 0x000000FF;
		int BC = B | C;

		return BC;// & 0x0000FFFF;
	}

	/**
	 * @return Registro C
	 */
	public byte getC() {
		return C;
	}

	/**
	 * Obtiene el flag de acarreo
	 * 
	 * @return Acarreo 1 o 0
	 */
	public byte getCarry() {
		return (byte) ((F & 1 << FLAG_C) >> FLAG_C);
	}

	/**
	 * @return Registro D
	 */
	public byte getD() {
		return D;
	}

	/**
	 * Obtiene un dato de 16 bits direccionado por un modo concreto
	 * 
	 * @param mode Modo de acceso a la información de lectura
	 * @return
	 * @throws IllegalAddressException
	 * @throws InstructionException
	 */
	public int getData16(int mode) throws IllegalAddressException, InstructionException {

		int data = 0x00;
		switch (mode) {
		case REG_BC:
			data = getBC();
			break;
		case REG_DE:
			data = getDE();
			break;
		case REG_HL:
			data = getHL();
			break;
		case REG_IX:
			data = getIX();
			break;
		case REG_IY:
			data = getIY();
			break;
		case REG_SP:
			data = getSP();
			break;
		case DIRECT_16:
			data = getNN();
			break;
		case ADDR_NN_16: /* (nn) */
			data = read16(getNN());
			break;
		case DESPLACEMENT:
			loadIR();
			data = getPC() + getIR();
			break;
		default:
			throw new InstructionException("Read Mode not valid");
		}
		return data;
	}

	/**
	 * Obtiene el byte direccionado por un modo concreto
	 * 
	 * @param mode Modo de acceso a la información de lectura
	 * @return
	 * @throws IllegalAddressException
	 * @throws InstructionException
	 */
	public byte getData8(int mode) throws IllegalAddressException, InstructionException {
		byte data = (byte) 0x00;
		switch (mode) {
		case REG_A:
			data = getA();
			break;
		case REG_B:
			data = getB();
			break;
		case REG_C:
			data = getC();
			break;
		case REG_D:
			data = getD();
			break;
		case REG_E:
			data = getE();
			break;
		case REG_H:
			data = getH();
			break;
		case REG_L:
			data = getL();
			break;
		case REG_I:
			data = getI();
			break;
		case REG_R:
			data = getR();
			break;
		case ADDR_BC: /* (BC) */
			data = read8(getBC());
			break;
		case ADDR_DE: /* (DE) */
			data = read8(getDE());
			break;
		case ADDR_HL: /* (HL) */
			data = read8(getHL());
			break;
		case ADDR_NN: /* (nn) */
			data = read8(getNN());
			break;
		case DIRECT_8: /* n */
			loadIR();
			data = getIR();
			break;
		case IX_D: /* IX + d */
			loadIR();
			data = readIX(getIR());
			break;
		case IY_D: /* IY + d */
			loadIR();
			data = readIY(getIR());
			break;
		default:
			throw new InstructionException("Read Mode not valid");
		}

		return data;
	}

	/**
	 * @return Devuelve el contenido de los registros D y E en 16 bits (Registro DE)
	 */
	public int getDE() {
		int D = getD() << 8 & 0x0000FF00;
		int E = getE() & 0x000000FF;
		int DE = D | E;

		return DE;// & 0x0000FFFF;
	}

	/**
	 * @return Registro E
	 */
	public byte getE() {
		return E;
	}

	/**
	 * @return Registro F
	 */
	public byte getF() {
		return F;
	}

	/**
	 * Obtiene un Flag del registro F
	 * 
	 * @param flag (S, Z, H, P/V, N, C)
	 * @return
	 */
	public boolean getFlag(int bitFlag) {
		return ((F & 1 << bitFlag) >> bitFlag) != 0;
	}

	/**
	 * @return Registro H
	 */
	public byte getH() {
		return H;
	}

	/**
	 * @return Devuelve el contenido de los registros H y L en 16 bits (Registro HL)
	 */
	public int getHL() {
		int H = getH() << 8 & 0x0000FF00;
		int L = getL() & 0x000000FF;
		int HL = H | L;

		return HL;// & 0x0000FFFF;
	}

	/**
	 * @return the Interrupt Page Address Register
	 */
	public byte getI() {
		return I;
	}

	/**
	 * @return Registro IX
	 */
	public int getIX() {
		return IX;
	}

	/**
	 * @return Registro IY
	 */
	public int getIY() {
		return IY;
	}

	/**
	 * @return Registro L
	 */
	public byte getL() {
		return L;
	}

	/**
	 * Lee las sigueintes 2 posiciones de memoria y construye un dato de 16 bits con ellas
	 * 
	 * @return
	 * @throws IllegalAddressException
	 */
	public int getNN() throws IllegalAddressException {
		loadIR();
		byte low = getIR();
		loadIR();
		byte high = getIR();
		int H = high << 8 & 0x0000FF00;
		int L = low & 0x000000FF;
		return (H | L);
	}

	/**
	 * @return RAM Refresh Register
	 */
	public byte getR() {
		return R;
	}

	/**
	 * @return the ULA
	 */
	public BusComponent getULA() {
		return ULA;
	}

	/**
	 * Inicialización de los parametros básicos del Z80
	 * 
	 * @throws UnknowInstructionException
	 */
	private void init() throws UnknowInstructionException {
		/* Frecuencia de trabajo 3.58 MHz */
		setHerz(3580000);
		/* Se carga la tabla de instrucciones */
		setInstructionMap(new Z80Map(this));
	}

	/**
	 * Determina si el Flag C es cero o no
	 * 
	 * @return
	 */
	public boolean isFlagCarry() {
		if ((F & 0b00000001) == 0)
			return false;
		else
			return true;
	}

	/**
	 * Determina si el Flag Paridad es Impar (1)
	 * 
	 * @return
	 */
	public boolean isFlagEven() {
		if ((F & 0b00000100) == 0)
			return false;
		else
			return true;
	}

	/**
	 * Determina si el Flag S es negativo
	 * 
	 * @return
	 */
	public boolean isFlagNegative() {
		if ((F & 0b10000000) == 0)
			return false;
		else
			return true;
	}

	/**
	 * Determina si el Flag Paridad es par (0)
	 * 
	 * @return
	 */
	public boolean isFlagOdd() {
		if ((F & 0b00000100) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Determina si el Flag S es positivo
	 * 
	 * @return
	 */
	public boolean isFlagPositive() {
		if ((F & 0b10000000) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Determina si el Flag Z es cero o no
	 * 
	 * @return
	 */
	public boolean isFlagZero() {
		if ((F & 0b01000000) == 0)
			return false;
		else
			return true;
	}

	/**
	 * @return Flipflop IFF1
	 */
	public boolean isIFF1() {
		return IFF1;
	}

	/**
	 * @return Flipflop IFF2
	 */
	public boolean isIFF2() {
		return IFF2;
	}

	/**
	 * Lee el contenido de la memoria apuntado en el registro IX con un desplazamiento d
	 * 
	 * @param offset Desplazamiento a la dirección (signed byte)
	 * @return byte leído
	 * @throws IllegalAddressException
	 */
	public byte readIX(byte offset) throws IllegalAddressException {
		return read8(getIX() + offset);
	}

	/**
	 * Lee el contenido de la memoria apuntado en el registro IY con un desplazamiento d
	 * 
	 * @param offset Desplazamiento a la dirección (signed byte)
	 * @return byte leído
	 * @throws IllegalAddressException
	 */
	public byte readIY(byte offset) throws IllegalAddressException {
		return read8(getIY() + offset);
	}

	/**
	 * @param Registro A
	 */
	public void setA(byte a) {
		A = a;
	}

	/**
	 * @param Registro B
	 */
	public void setB(byte b) {
		B = b;
	}

	/**
	 * @param Registro C
	 */
	public void setC(byte c) {
		C = c;
	}

	/**
	 * @param Registro D
	 */
	public void setD(byte d) {
		D = d;
	}

	/**
	 * Escribe 2 bytes en un destino establecido por un modo en concreto
	 * 
	 * @param mode Modo de escritura de la información
	 * @param data Información a escribir
	 * @throws IllegalAddressException
	 * @throws InstructionException
	 */
	public void setData16(int mode, int data) throws IllegalAddressException, InstructionException {
		/* Dependiendo del destino */
		switch (mode) {
		case REG_BC:
			setB((byte) (data >> 8 & 0x000000FF));
			setC((byte) (data & 0x000000FF));
			break;
		case REG_DE:
			setD((byte) (data >> 8 & 0x000000FF));
			setE((byte) (data & 0x000000FF));
			break;
		case REG_HL:
			setH((byte) (data >> 8 & 0x000000FF));
			setL((byte) (data & 0x000000FF));
			break;
		case REG_SP:
			setSP(data);
			break;
		case REG_IX:
			setIX(data);
			break;
		case REG_IY:
			setIY(data);
			break;
		case Z80.ADDR_NN_16:
			write16(getNN(), data);
			break;
		default:
			throw new InstructionException("Write Mode not valid");
		}
	}

	/**
	 * Escribe un byte en un destino establecido por un modo en concreto
	 * 
	 * @param mode Modo de escritura de la información
	 * @param data Información a escribir
	 * @throws IllegalAddressException
	 * @throws InstructionException
	 */
	public void setData8(int mode, byte data) throws IllegalAddressException, InstructionException {
		/* Dependiendo del destino */
		switch (mode) {
		case Z80.REG_A:
			setA(data);
			break;
		case Z80.REG_B:
			setB(data);
			break;
		case Z80.REG_C:
			setC(data);
			break;
		case Z80.REG_D:
			setD(data);
			break;
		case Z80.REG_E:
			setE(data);
			break;
		case Z80.REG_H:
			setH(data);
			break;
		case Z80.REG_L:
			setL(data);
			break;
		case Z80.REG_I:
			setI(data);
			break;
		case Z80.REG_R:
			setR(data);
			break;
		case Z80.ADDR_BC:
			write8(getBC(), data);
			break;
		case Z80.ADDR_DE:
			write8(getDE(), data);
			break;
		case Z80.ADDR_HL:
			write8(getHL(), data);
			break;
		case Z80.ADDR_NN:
			write8(getNN(), data);
			break;
		case Z80.IX_D:
			writeIX_d(data);
			break;
		case Z80.IY_D:
			writeIY_d(data);
			break;
		default:
			throw new InstructionException("Write Mode not valid");
		}
	}

	/**
	 * @param Registro E
	 */
	public void setE(byte e) {
		E = e;
	}

	/**
	 * @param Registro F
	 */
	public void setF(byte f) {
		F = f;
	}

	/**
	 * Establece un flag en el registro F
	 * 
	 * @param flag (S, Z, H, P/V, N, C)
	 * @param value valor a asignar
	 */
	public void setFlag(int bitFlag, boolean value) {
		F &= ~(1 << bitFlag); // Set bit a 0
		F |= ((value ? 1 : 0) << bitFlag); // Set bit a 1 si es necesario
	}

	/**
	 * Establece los flags sin documentar 3 y 5 con el contenido de los bit 3 y 5 del byte pasado
	 * 
	 * @param reg
	 */
	public void setFlag35(byte reg) {
		// Flags sin documentar
		setFlag(Z80.FLAG_3, (reg >> 3 & 0x01) == 0x01);
		setFlag(Z80.FLAG_5, (reg >> 5 & 0x01) == 0x01);
	}

	/**
	 * Se establece el flag de paridad, contanto en número de 1 (binarios) del Acumulador
	 * El resultado será 1 si ese conteo es par, y 0 si es impar
	 * 
	 * @param reg
	 */
	public void setFlagParity(byte reg) {

		boolean res = true;

		if ((reg & 0b10000000) != 0) {
			res = res ^ true;
		}
		if ((reg & 0b01000000) != 0) {
			res = res ^ true;
		}
		if ((reg & 0b00100000) != 0) {
			res = res ^ true;
		}
		if ((reg & 0b00010000) != 0) {
			res = res ^ true;
		}
		if ((reg & 0b00001000) != 0) {
			res = res ^ true;
		}
		if ((reg & 0b00000100) != 0) {
			res = res ^ true;
		}
		if ((reg & 0b00000010) != 0) {
			res = res ^ true;
		}
		if ((reg & 0b10000001) != 0) {
			res = res ^ true;
		}

		setFlag(Z80.FLAG_PV, res);
	}

	/**
	 * Marca el Flag S con el signo del registro Acumulador A, siendo 0 positivo y 1 negativo
	 * 
	 * @param reg
	 */
	public void setFlagSigned(byte reg) {
		byte sig = (byte) (reg & 0b10000000);

		/* Si es negativo se establece S */
		setFlag(Z80.FLAG_S, sig != 0);
	}

	/**
	 * Marca el Flag Z dependiendo de si el Acumulador A es cero o no
	 * 
	 * @param reg
	 */
	public void setFlagZero(byte reg) {
		setFlag(Z80.FLAG_Z, reg == 0);
	}

	/**
	 * @param Registro H
	 */
	public void setH(byte h) {
		H = h;
	}

	/**
	 * @param i the Interrupt Page Address Register to set
	 */
	public void setI(byte i) {
		I = i;
	}

	/**
	 * @param Flipflop IFF1
	 */
	public void setIFF1(boolean iFF1) {
		IFF1 = iFF1;
	}

	/**
	 * @param Flipflop IFF2
	 */
	public void setIFF2(boolean iFF2) {
		IFF2 = iFF2;
	}

	// Establece el modo de interrupción
	public void setInterruptMode(int mode) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param Registro IX
	 */
	public void setIX(int iX) {
		IX = iX & 0x0000FFFF;
	}

	/**
	 * @param Registro IY
	 */
	public void setIY(int iY) {
		IY = iY & 0x0000FFFF;
	}

	/**
	 * @param Registro L
	 */
	public void setL(byte l) {
		L = l;
	}

	/**
	 * @param r the Memory Refresh Register to set
	 */
	public void setR(byte r) {
		R = r;
	}

	/**
	 * @param Establece la ULA asociada al procesador
	 */
	public void setULA(ULA ula) {
		this.ULA = ula;
	}

	/**
	 * Resta un byte al acumulador teniendo en cuenta el flag de acarreo
	 * Realmente hace una operaciones addWithCarry y cambia el falg de acarreo al final
	 * a - b - c = a + ~b + 1 - c = a + ~b + !c
	 * 
	 * @param b Byte a restar al registro A
	 * @param c Byte de acarreo
	 * @return resultado (registro A)
	 */
	public byte subWithCarry(byte b, byte c) {
		// Flag de Half Borrow
		boolean bHalf = (A & 0x0F) < (b & 0x0F + c & 0x0F);

		addWithCarry((byte) ~b, (byte) (1 - c));
		F ^= (1 << FLAG_C); // Flip del flag de acarreo

		setFlag(Z80.FLAG_H, bHalf);

		// Flag de sustracción
		setFlag(Z80.FLAG_N, true);

		return A;
	}

	/**
	 * Resta un registro a otro teniendo en cuenta el flag de acarreo
	 * Realmente hace una operaciones addWithCarry y cambia el falg de acarreo al final
	 * a - b - c = a + ~b + 1 - c = a + ~b + !c
	 * 
	 * @param a Byte a restar 16 bits
	 * @param b Byte a restar 16 bits
	 * @param c Byte de acarreo
	 * @return resultado 16 bits
	 */
	public int subWithCarry16(int a, int b, byte c) {
		int res = addWithCarry16(a, ~b & 0xFFFF, (byte) (1 - c));

		F ^= (1 << FLAG_C); // Flip del flag de acarreo
		F ^= (1 << FLAG_H); // Flip del flag de half carry

		// Flag de sustracción
		setFlag(Z80.FLAG_N, true);
		// Flag de Half Borrow
		boolean bHalf = (a & 0x0FFF) < (b & 0x0FFF + c & 0x0FFF);
		setFlag(Z80.FLAG_H, bHalf);

		return res;
	}

	/**
	 * Escribe en el Bus de IO que recibirá la ULA
	 * 
	 * @param address
	 * @param data
	 * @throws IllegalAddressException
	 */
	public void writeIO(int address, byte data) throws IllegalAddressException {
		ULA.writeIO(address, data);
	}

	/**
	 * Escribe un dato en la memoria apuntado en el registro XY con un desplazamiento d
	 * 
	 * @param byte escrito
	 * @throws IllegalAddressException
	 */
	public void writeIX_d(byte data) throws IllegalAddressException {
		loadIR();
		write8(getIX() + getIR(), data);
	}

	/**
	 * Escribe un dato en la memoria apuntado en el registro IY con un desplazamiento d
	 * 
	 * @param byte escrito
	 * @throws IllegalAddressException
	 */
	public void writeIY_d(byte data) throws IllegalAddressException {
		loadIR();
		write8(getIY() + getIR(), data);
	}

}
