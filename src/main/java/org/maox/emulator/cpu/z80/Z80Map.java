package org.maox.emulator.cpu.z80;

import org.maox.emulator.core.Instruction;
import org.maox.emulator.core.InstructionMap;
import org.maox.emulator.cpu.z80.asm.ADC;
import org.maox.emulator.cpu.z80.asm.ADC_16;
import org.maox.emulator.cpu.z80.asm.ADD;
import org.maox.emulator.cpu.z80.asm.ADD_16;
import org.maox.emulator.cpu.z80.asm.AND;
import org.maox.emulator.cpu.z80.asm.CP;
import org.maox.emulator.cpu.z80.asm.DEC;
import org.maox.emulator.cpu.z80.asm.DEC_16;
import org.maox.emulator.cpu.z80.asm.DI;
import org.maox.emulator.cpu.z80.asm.DJNZ;
import org.maox.emulator.cpu.z80.asm.EI;
import org.maox.emulator.cpu.z80.asm.EX;
import org.maox.emulator.cpu.z80.asm.EXX;
import org.maox.emulator.cpu.z80.asm.IM;
import org.maox.emulator.cpu.z80.asm.INC;
import org.maox.emulator.cpu.z80.asm.INC_16;
import org.maox.emulator.cpu.z80.asm.JP;
import org.maox.emulator.cpu.z80.asm.LD;
import org.maox.emulator.cpu.z80.asm.LDD;
import org.maox.emulator.cpu.z80.asm.LDDR;
import org.maox.emulator.cpu.z80.asm.LDI;
import org.maox.emulator.cpu.z80.asm.LDIR;
import org.maox.emulator.cpu.z80.asm.LD_16;
import org.maox.emulator.cpu.z80.asm.NOP;
import org.maox.emulator.cpu.z80.asm.OR;
import org.maox.emulator.cpu.z80.asm.OUT;
import org.maox.emulator.cpu.z80.asm.SBC;
import org.maox.emulator.cpu.z80.asm.SBC_16;
import org.maox.emulator.cpu.z80.asm.SET;
import org.maox.emulator.cpu.z80.asm.SUB;
import org.maox.emulator.cpu.z80.asm.XOR;
import org.maox.emulator.debug.Hex;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.exceptions.UnknowInstructionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapeo de Instrucciones del Z80
 * 
 * @author Alex Orgaz
 * 
 */
public class Z80Map extends InstructionMap {

	final private static Logger logger = LoggerFactory.getLogger(Z80Map.class);

	/* CPU asociada */
	private Z80 cpu;

	/* Tabla de códigos */
	protected Instruction[] tableCodes = new Instruction[Z80.MAP_SIZE];
	protected Instruction[] tableCodesCB = new Instruction[Z80.MAP_SIZE];
	protected Instruction[] tableCodesDD = new Instruction[Z80.MAP_SIZE];
	protected Instruction[] tableCodesED = new Instruction[Z80.MAP_SIZE];
	protected Instruction[] tableCodesFD = new Instruction[Z80.MAP_SIZE];

	/* Para las instrucciones con 2 prefijos SET y BIT --> DD CB y FD CB */
	protected Instruction[] tableCodesDDCB = new Instruction[Z80.MAP_SIZE];
	protected Instruction[] tableCodesFDCB = new Instruction[Z80.MAP_SIZE];

	/**
	 * Constructor del mapeo
	 * 
	 * @throws UnknowInstructionException
	 */
	public Z80Map(Z80 cpu) throws UnknowInstructionException {
		super();
		this.cpu = cpu;
		init();
		// logger.trace("\n" + toString());
	}

	@Override
	public Instruction getInstruction(byte opcode) throws InstructionException, IllegalAddressException {

		/* Instrucción a devolver */
		Instruction inst = null;

		/* Transformación opCode a sin signo */
		int opc = opcode & 0x000000FF;

		/* Algunas instrucciones son multibytes */
		/* Van precedidas por los siguientes bytes: CB, DD, ED, FD */
		if (opcode == (byte) 0xCB) {
			cpu.loadIR();
			inst = tableCodesCB[cpu.getIR() & 0x000000FF];
		} else if (opcode == (byte) 0xDD || opcode == (byte) 0xFD) {
			/* En estos casos hay que comprobar si es con 2 prefijos */
			/* Si el siguiente opcode es CB se ha se consultar la tabla con 2 prefijos */
			cpu.loadIR();

			if (cpu.getIR() == (byte) 0xCB) {
				/* El código de instrucción no está en la siguiente instrucción sino 2 más adelante */
				cpu.loadIR(); /* Aquí esta el desplazamiento (se irá a por el en la instrucción a mano) */
				cpu.loadIR(); /* Código de Instrucción */

				if (opcode == (byte) 0xDD) {
					inst = tableCodesDDCB[cpu.getIR() & 0x000000FF];
				} else if (opcode == (byte) 0xFD) {
					inst = tableCodesFDCB[cpu.getIR() & 0x000000FF];
				}

			} else if (opcode == (byte) 0xDD) {
				inst = tableCodesDD[cpu.getIR() & 0x000000FF];
			} else if (opcode == (byte) 0xFD) {
				inst = tableCodesFD[cpu.getIR() & 0x000000FF];
			}

		} else if (opcode == (byte) 0xED) {
			cpu.loadIR();
			inst = tableCodesED[cpu.getIR() & 0x000000FF];
		} else {
			inst = tableCodes[opc];
		}

		if (inst == null)
			throw new UnknowInstructionException("opCode: " + Hex.byteToHex(opcode));

		return inst;
	}

	@Override
	public Instruction getInstructionComplete(int address) throws InstructionException, IllegalAddressException {
		/* Instrucción a devolver */
		Instruction inst = null;

		/* Transformación opCode a sin signo */
		byte opcode = cpu.read8(address);

		/* Algunas instrucciones son multibytes */
		/* Van precedidas por los siguientes bytes: CB, DD, ED, FD */
		byte nextOpCode1 = (byte) 0x00;
		byte nextOpCode2 = (byte) 0x00;
		byte nextOpCode3 = (byte) 0x00;

		if (opcode == (byte) 0xCB) {
			nextOpCode1 = (byte) (cpu.read8(cpu.getPC() + 1) & 0x000000FF);
			inst = tableCodesCB[nextOpCode1 & 0x000000FF];
		} else if (opcode == (byte) 0xDD || opcode == (byte) 0xFD) {
			/* En estos casos hay que comprobar si es con 2 prefijos */
			/* Si el siguiente opcode es CB se ha se consultar la tabla con 2 prefijos */
			nextOpCode1 = (byte) (cpu.read8(cpu.getPC() + 1) & 0x000000FF);

			if (nextOpCode1 == (byte) 0xCB) {
				/* El código de instrucción no está en la siguiente instrucción sino 2 más adelante */
				nextOpCode2 = (byte) (cpu.read8(cpu.getPC() + 2) & 0x000000FF);
				nextOpCode3 = (byte) (cpu.read8(cpu.getPC() + 3) & 0x000000FF);

				if (opcode == (byte) 0xDD) {
					inst = tableCodesDDCB[nextOpCode3 & 0x000000FF];
				} else if (opcode == (byte) 0xFD) {
					inst = tableCodesFDCB[nextOpCode3 & 0x000000FF];
				}

			} else if (opcode == (byte) 0xDD) {
				inst = tableCodesDD[nextOpCode1 & 0x000000FF];
			} else if (opcode == (byte) 0xFD) {
				inst = tableCodesFD[nextOpCode1 & 0x000000FF];
			}

		} else if (opcode == (byte) 0xED) {
			nextOpCode1 = (byte) (cpu.read8(cpu.getPC() + 1) & 0x000000FF);
			inst = tableCodesED[nextOpCode1 & 0x000000FF];
		} else {
			inst = tableCodes[opcode & 0x000000FF];
		}

		if (inst == null) {
			if (nextOpCode3 != (byte) 0x00)
				throw new UnknowInstructionException("opCode: " + Hex.byteToHex(opcode) + " "
						+ Hex.byteToHex(nextOpCode1) + " " + Hex.byteToHex(nextOpCode2) + " "
						+ Hex.byteToHex(nextOpCode3));
			else if (opcode == (byte) 0xCB || opcode == (byte) 0xDD || opcode == (byte) 0xED || opcode == (byte) 0xFD)
				throw new UnknowInstructionException("opCode: " + Hex.byteToHex(opcode) + " "
						+ Hex.byteToHex(nextOpCode1));
			else
				throw new UnknowInstructionException("opCode: " + Hex.byteToHex(opcode));
		}

		/* Se recuperan los parametros de la instrucción en caso necesario */
		inst.completeValues(address);

		return inst;
	}

	/**
	 * Inicialización del mapa de instrucciones
	 * 
	 * @throws UnknowInstructionException
	 */
	private void init() throws UnknowInstructionException {

		// Instrucción Nula
		tableCodes[0x00] = new NOP();

		// CPU Control
		tableCodes[0xF3] = new DI();
		tableCodes[0xFB] = new EI();

		tableCodesED[0x46] = new IM(Z80.INTERRUPT_MODE_0);
		tableCodesED[0x56] = new IM(Z80.INTERRUPT_MODE_1);
		tableCodesED[0x5E] = new IM(Z80.INTERRUPT_MODE_2);

		// Intercambio de Registros
		tableCodes[0xD9] = new EXX();
		tableCodes[0xEB] = new EX(Z80.REG_DE, Z80.REG_HL);
		tableCodes[0x08] = new EX(Z80.REG_AF, Z80.REG_AF_ALT);
		tableCodes[0xE3] = new EX(Z80.ADDR_SP, Z80.REG_HL);
		tableCodesDD[0xE3] = new EX(Z80.ADDR_SP, Z80.REG_IX);
		tableCodesFD[0xE3] = new EX(Z80.ADDR_SP, Z80.REG_IY);

		// Op. Carga Load 8 bits Básicas
		tableCodes[0x78] = new LD(Z80.REG_A, Z80.REG_B);
		tableCodes[0x79] = new LD(Z80.REG_A, Z80.REG_C);
		tableCodes[0x7A] = new LD(Z80.REG_A, Z80.REG_D);
		tableCodes[0x7B] = new LD(Z80.REG_A, Z80.REG_E);
		tableCodes[0x7C] = new LD(Z80.REG_A, Z80.REG_H);
		tableCodes[0x7D] = new LD(Z80.REG_A, Z80.REG_L);
		tableCodes[0x7E] = new LD(Z80.REG_A, Z80.ADDR_HL);
		tableCodes[0x7F] = new LD(Z80.REG_A, Z80.REG_A);
		tableCodes[0x3E] = new LD(Z80.REG_A, Z80.DIRECT_8);
		tableCodesDD[0x7E] = new LD(Z80.REG_A, Z80.IX_D);
		tableCodesFD[0x7E] = new LD(Z80.REG_A, Z80.IY_D);
		tableCodes[0x0A] = new LD(Z80.REG_A, Z80.ADDR_BC);
		tableCodes[0x1A] = new LD(Z80.REG_A, Z80.ADDR_DE);
		tableCodes[0x3A] = new LD(Z80.REG_A, Z80.ADDR_NN);

		tableCodes[0x40] = new LD(Z80.REG_B, Z80.REG_B);
		tableCodes[0x41] = new LD(Z80.REG_B, Z80.REG_C);
		tableCodes[0x42] = new LD(Z80.REG_B, Z80.REG_D);
		tableCodes[0x43] = new LD(Z80.REG_B, Z80.REG_E);
		tableCodes[0x44] = new LD(Z80.REG_B, Z80.REG_H);
		tableCodes[0x45] = new LD(Z80.REG_B, Z80.REG_L);
		tableCodes[0x46] = new LD(Z80.REG_B, Z80.ADDR_HL);
		tableCodes[0x47] = new LD(Z80.REG_B, Z80.REG_A);
		tableCodes[0x06] = new LD(Z80.REG_B, Z80.DIRECT_8);
		tableCodesDD[0x46] = new LD(Z80.REG_B, Z80.IX_D);
		tableCodesFD[0x46] = new LD(Z80.REG_B, Z80.IY_D);

		tableCodes[0x48] = new LD(Z80.REG_C, Z80.REG_B);
		tableCodes[0x49] = new LD(Z80.REG_C, Z80.REG_C);
		tableCodes[0x4A] = new LD(Z80.REG_C, Z80.REG_D);
		tableCodes[0x4B] = new LD(Z80.REG_C, Z80.REG_E);
		tableCodes[0x4C] = new LD(Z80.REG_C, Z80.REG_H);
		tableCodes[0x4D] = new LD(Z80.REG_C, Z80.REG_L);
		tableCodes[0x4E] = new LD(Z80.REG_C, Z80.ADDR_HL);
		tableCodes[0x4F] = new LD(Z80.REG_C, Z80.REG_A);
		tableCodes[0x0E] = new LD(Z80.REG_C, Z80.DIRECT_8);
		tableCodesDD[0x4E] = new LD(Z80.REG_C, Z80.IX_D);
		tableCodesFD[0x4E] = new LD(Z80.REG_C, Z80.IY_D);

		tableCodes[0x50] = new LD(Z80.REG_D, Z80.REG_B);
		tableCodes[0x51] = new LD(Z80.REG_D, Z80.REG_C);
		tableCodes[0x52] = new LD(Z80.REG_D, Z80.REG_D);
		tableCodes[0x53] = new LD(Z80.REG_D, Z80.REG_E);
		tableCodes[0x54] = new LD(Z80.REG_D, Z80.REG_H);
		tableCodes[0x55] = new LD(Z80.REG_D, Z80.REG_L);
		tableCodes[0x56] = new LD(Z80.REG_D, Z80.ADDR_HL);
		tableCodes[0x57] = new LD(Z80.REG_D, Z80.REG_A);
		tableCodes[0x16] = new LD(Z80.REG_D, Z80.DIRECT_8);
		tableCodesDD[0x56] = new LD(Z80.REG_D, Z80.IX_D);
		tableCodesFD[0x56] = new LD(Z80.REG_D, Z80.IY_D);

		tableCodes[0x58] = new LD(Z80.REG_E, Z80.REG_B);
		tableCodes[0x59] = new LD(Z80.REG_E, Z80.REG_C);
		tableCodes[0x5A] = new LD(Z80.REG_E, Z80.REG_D);
		tableCodes[0x5B] = new LD(Z80.REG_E, Z80.REG_E);
		tableCodes[0x5C] = new LD(Z80.REG_E, Z80.REG_H);
		tableCodes[0x5D] = new LD(Z80.REG_E, Z80.REG_L);
		tableCodes[0x5E] = new LD(Z80.REG_E, Z80.ADDR_HL);
		tableCodes[0x5F] = new LD(Z80.REG_E, Z80.REG_A);
		tableCodes[0x1E] = new LD(Z80.REG_E, Z80.DIRECT_8);
		tableCodesDD[0x5E] = new LD(Z80.REG_E, Z80.IX_D);
		tableCodesFD[0x5E] = new LD(Z80.REG_E, Z80.IY_D);

		tableCodes[0x60] = new LD(Z80.REG_H, Z80.REG_B);
		tableCodes[0x61] = new LD(Z80.REG_H, Z80.REG_C);
		tableCodes[0x62] = new LD(Z80.REG_H, Z80.REG_D);
		tableCodes[0x63] = new LD(Z80.REG_H, Z80.REG_E);
		tableCodes[0x64] = new LD(Z80.REG_H, Z80.REG_H);
		tableCodes[0x65] = new LD(Z80.REG_H, Z80.REG_L);
		tableCodes[0x66] = new LD(Z80.REG_H, Z80.ADDR_HL);
		tableCodes[0x67] = new LD(Z80.REG_H, Z80.REG_A);
		tableCodes[0x26] = new LD(Z80.REG_H, Z80.DIRECT_8);
		tableCodesDD[0x66] = new LD(Z80.REG_H, Z80.IX_D);
		tableCodesFD[0x66] = new LD(Z80.REG_H, Z80.IY_D);

		tableCodes[0x68] = new LD(Z80.REG_L, Z80.REG_B);
		tableCodes[0x69] = new LD(Z80.REG_L, Z80.REG_C);
		tableCodes[0x6A] = new LD(Z80.REG_L, Z80.REG_D);
		tableCodes[0x6B] = new LD(Z80.REG_L, Z80.REG_E);
		tableCodes[0x6C] = new LD(Z80.REG_L, Z80.REG_H);
		tableCodes[0x6D] = new LD(Z80.REG_L, Z80.REG_L);
		tableCodes[0x6E] = new LD(Z80.REG_L, Z80.ADDR_HL);
		tableCodes[0x6F] = new LD(Z80.REG_L, Z80.REG_A);
		tableCodes[0x2E] = new LD(Z80.REG_L, Z80.DIRECT_8);
		tableCodesDD[0x6E] = new LD(Z80.REG_L, Z80.IX_D);
		tableCodesFD[0x6E] = new LD(Z80.REG_L, Z80.IY_D);

		// Registros Especiales
		tableCodesED[0x57] = new LD(Z80.REG_A, Z80.REG_I);
		tableCodesED[0x5F] = new LD(Z80.REG_A, Z80.REG_R);
		tableCodesED[0x47] = new LD(Z80.REG_I, Z80.REG_A);
		tableCodesED[0x4F] = new LD(Z80.REG_R, Z80.REG_A);

		// Op. Carga Load 8 bits con direccionamiento
		tableCodes[0x70] = new LD(Z80.ADDR_HL, Z80.REG_B);
		tableCodes[0x71] = new LD(Z80.ADDR_HL, Z80.REG_C);
		tableCodes[0x72] = new LD(Z80.ADDR_HL, Z80.REG_D);
		tableCodes[0x73] = new LD(Z80.ADDR_HL, Z80.REG_E);
		tableCodes[0x74] = new LD(Z80.ADDR_HL, Z80.REG_H);
		tableCodes[0x75] = new LD(Z80.ADDR_HL, Z80.REG_L);
		tableCodes[0x77] = new LD(Z80.ADDR_HL, Z80.REG_A);
		tableCodes[0x36] = new LD(Z80.ADDR_HL, Z80.DIRECT_8);

		tableCodesDD[0x70] = new LD(Z80.IX_D, Z80.REG_B);
		tableCodesDD[0x71] = new LD(Z80.IX_D, Z80.REG_C);
		tableCodesDD[0x72] = new LD(Z80.IX_D, Z80.REG_D);
		tableCodesDD[0x73] = new LD(Z80.IX_D, Z80.REG_E);
		tableCodesDD[0x74] = new LD(Z80.IX_D, Z80.REG_H);
		tableCodesDD[0x75] = new LD(Z80.IX_D, Z80.REG_L);
		tableCodesDD[0x77] = new LD(Z80.IX_D, Z80.REG_A);
		tableCodesDD[0x36] = new LD(Z80.IX_D, Z80.DIRECT_8);

		tableCodesFD[0x70] = new LD(Z80.IY_D, Z80.REG_B);
		tableCodesFD[0x71] = new LD(Z80.IY_D, Z80.REG_C);
		tableCodesFD[0x72] = new LD(Z80.IY_D, Z80.REG_D);
		tableCodesFD[0x73] = new LD(Z80.IY_D, Z80.REG_E);
		tableCodesFD[0x74] = new LD(Z80.IY_D, Z80.REG_H);
		tableCodesFD[0x75] = new LD(Z80.IY_D, Z80.REG_L);
		tableCodesFD[0x77] = new LD(Z80.IY_D, Z80.REG_A);
		tableCodesFD[0x36] = new LD(Z80.IY_D, Z80.DIRECT_8);

		tableCodes[0x02] = new LD(Z80.ADDR_BC, Z80.REG_A);
		tableCodes[0x12] = new LD(Z80.ADDR_DE, Z80.REG_A);
		tableCodes[0x32] = new LD(Z80.ADDR_NN, Z80.REG_A);

		// Carga 16 Bits
		tableCodes[0x01] = new LD_16(Z80.REG_BC, Z80.DIRECT_16);
		tableCodes[0x11] = new LD_16(Z80.REG_DE, Z80.DIRECT_16);
		tableCodes[0x21] = new LD_16(Z80.REG_HL, Z80.DIRECT_16);
		tableCodes[0x31] = new LD_16(Z80.REG_SP, Z80.DIRECT_16);
		tableCodesDD[0x21] = new LD_16(Z80.REG_IX, Z80.DIRECT_16);
		tableCodesFD[0x21] = new LD_16(Z80.REG_IY, Z80.DIRECT_16);

		// Carga 16 Bits Direccionamiento Memoria
		tableCodes[0x2A] = new LD_16(Z80.REG_HL, Z80.ADDR_NN_16);
		tableCodes[0x2A].setCycles((byte) 16);
		tableCodesED[0x4B] = new LD_16(Z80.REG_BC, Z80.ADDR_NN_16);
		tableCodesED[0x5B] = new LD_16(Z80.REG_DE, Z80.ADDR_NN_16);
		tableCodesED[0x6B] = new LD_16(Z80.REG_HL, Z80.ADDR_NN_16);
		tableCodesED[0x7B] = new LD_16(Z80.REG_SP, Z80.ADDR_NN_16);
		tableCodesDD[0x2A] = new LD_16(Z80.REG_IX, Z80.ADDR_NN_16);
		tableCodesFD[0x2A] = new LD_16(Z80.REG_IY, Z80.ADDR_NN_16);

		tableCodes[0x22] = new LD_16(Z80.ADDR_NN_16, Z80.REG_HL);
		tableCodes[0x22].setCycles((byte) 16);
		tableCodesED[0x43] = new LD_16(Z80.ADDR_NN_16, Z80.REG_BC);
		tableCodesED[0x53] = new LD_16(Z80.ADDR_NN_16, Z80.REG_DE);
		tableCodesED[0x63] = new LD_16(Z80.ADDR_NN_16, Z80.REG_HL);
		tableCodesED[0x73] = new LD_16(Z80.ADDR_NN_16, Z80.REG_SP);
		tableCodesDD[0x22] = new LD_16(Z80.ADDR_NN_16, Z80.REG_IX);
		tableCodesFD[0x22] = new LD_16(Z80.ADDR_NN_16, Z80.REG_IY);

		tableCodes[0xF9] = new LD_16(Z80.REG_SP, Z80.REG_HL);
		tableCodesDD[0xF9] = new LD_16(Z80.REG_SP, Z80.REG_IX);
		tableCodesFD[0xF9] = new LD_16(Z80.REG_SP, Z80.REG_IY);

		// Carga en bloque
		tableCodesED[0xA0] = new LDI();
		tableCodesED[0xB0] = new LDIR();
		tableCodesED[0xA8] = new LDD();
		tableCodesED[0xB8] = new LDDR();

		// Op. Control
		tableCodes[0xC3] = new JP(Z80.DIRECT_16);
		tableCodes[0xC2] = new JP(Z80.COND_NZ, Z80.DIRECT_16);
		tableCodes[0xCA] = new JP(Z80.COND_Z, Z80.DIRECT_16);
		tableCodes[0xD2] = new JP(Z80.COND_NC, Z80.DIRECT_16);
		tableCodes[0xDA] = new JP(Z80.COND_C, Z80.DIRECT_16);
		tableCodes[0xE2] = new JP(Z80.COND_PO, Z80.DIRECT_16);
		tableCodes[0xEA] = new JP(Z80.COND_PE, Z80.DIRECT_16);
		tableCodes[0xF2] = new JP(Z80.COND_P, Z80.DIRECT_16);
		tableCodes[0xFA] = new JP(Z80.COND_M, Z80.DIRECT_16);
		tableCodes[0x18] = new JP(Z80.DESPLACEMENT);
		tableCodes[0x20] = new JP(Z80.COND_NZ, Z80.DESPLACEMENT);
		tableCodes[0x28] = new JP(Z80.COND_Z, Z80.DESPLACEMENT);
		tableCodes[0x30] = new JP(Z80.COND_NC, Z80.DESPLACEMENT);
		tableCodes[0x38] = new JP(Z80.COND_C, Z80.DESPLACEMENT);
		tableCodes[0xE9] = new JP(Z80.REG_HL);
		tableCodesDD[0xE9] = new JP(Z80.REG_IX);
		tableCodesFD[0xE9] = new JP(Z80.REG_IY);

		tableCodes[0x10] = new DJNZ(Z80.DESPLACEMENT);

		// I/O
		tableCodes[0xD3] = new OUT();
		tableCodesED[0x41] = new OUT(Z80.REG_B);
		tableCodesED[0x49] = new OUT(Z80.REG_C);
		tableCodesED[0x51] = new OUT(Z80.REG_D);
		tableCodesED[0x59] = new OUT(Z80.REG_E);
		tableCodesED[0x61] = new OUT(Z80.REG_H);
		tableCodesED[0x69] = new OUT(Z80.REG_L);
		tableCodesED[0x71] = new OUT(Z80.REG_F);
		tableCodesED[0x79] = new OUT(Z80.REG_A);

		// Op. Lógicas AND
		tableCodes[0xA0] = new AND(Z80.REG_B);
		tableCodes[0xA1] = new AND(Z80.REG_C);
		tableCodes[0xA2] = new AND(Z80.REG_D);
		tableCodes[0xA3] = new AND(Z80.REG_E);
		tableCodes[0xA4] = new AND(Z80.REG_H);
		tableCodes[0xA5] = new AND(Z80.REG_L);
		tableCodes[0xA6] = new AND(Z80.ADDR_HL);
		tableCodes[0xA7] = new AND(Z80.REG_A);
		tableCodes[0xE6] = new AND(Z80.DIRECT_8);
		tableCodesDD[0xA6] = new AND(Z80.IX_D);
		tableCodesFD[0xA6] = new AND(Z80.IY_D);

		// Op. Lógicas OR
		tableCodes[0xB0] = new OR(Z80.REG_B);
		tableCodes[0xB1] = new OR(Z80.REG_C);
		tableCodes[0xB2] = new OR(Z80.REG_D);
		tableCodes[0xB3] = new OR(Z80.REG_E);
		tableCodes[0xB4] = new OR(Z80.REG_H);
		tableCodes[0xB5] = new OR(Z80.REG_L);
		tableCodes[0xB6] = new OR(Z80.ADDR_HL);
		tableCodes[0xB7] = new OR(Z80.REG_A);
		tableCodes[0xF6] = new OR(Z80.DIRECT_8);
		tableCodesDD[0xB6] = new OR(Z80.IX_D);
		tableCodesFD[0xB6] = new OR(Z80.IY_D);

		// Op. Lógicas XOR
		tableCodes[0xA8] = new XOR(Z80.REG_B);
		tableCodes[0xA9] = new XOR(Z80.REG_C);
		tableCodes[0xAA] = new XOR(Z80.REG_D);
		tableCodes[0xAB] = new XOR(Z80.REG_E);
		tableCodes[0xAC] = new XOR(Z80.REG_H);
		tableCodes[0xAD] = new XOR(Z80.REG_L);
		tableCodes[0xAE] = new XOR(Z80.ADDR_HL);
		tableCodes[0xAF] = new XOR(Z80.REG_A);
		tableCodes[0xEE] = new XOR(Z80.DIRECT_8);
		tableCodesDD[0xAE] = new XOR(Z80.IX_D);
		tableCodesFD[0xAE] = new XOR(Z80.IY_D);

		// Op. Comparacion CP
		tableCodes[0xB8] = new CP(Z80.REG_B);
		tableCodes[0xB9] = new CP(Z80.REG_C);
		tableCodes[0xBA] = new CP(Z80.REG_D);
		tableCodes[0xBB] = new CP(Z80.REG_E);
		tableCodes[0xBC] = new CP(Z80.REG_H);
		tableCodes[0xBD] = new CP(Z80.REG_L);
		tableCodes[0xBE] = new CP(Z80.ADDR_HL);
		tableCodes[0xBF] = new CP(Z80.REG_A);
		tableCodes[0xFE] = new CP(Z80.DIRECT_8);
		tableCodesDD[0xBE] = new CP(Z80.IX_D);
		tableCodesFD[0xBE] = new CP(Z80.IY_D);

		// Op. Artiméticas 8 Bits
		tableCodes[0x80] = new ADD(Z80.REG_B);
		tableCodes[0x81] = new ADD(Z80.REG_C);
		tableCodes[0x82] = new ADD(Z80.REG_D);
		tableCodes[0x83] = new ADD(Z80.REG_E);
		tableCodes[0x84] = new ADD(Z80.REG_H);
		tableCodes[0x85] = new ADD(Z80.REG_L);
		tableCodes[0x86] = new ADD(Z80.ADDR_HL);
		tableCodes[0x87] = new ADD(Z80.REG_A);
		tableCodes[0xC6] = new ADD(Z80.DIRECT_8);
		tableCodesDD[0x86] = new ADD(Z80.IX_D);
		tableCodesFD[0x86] = new ADD(Z80.IY_D);

		tableCodes[0x88] = new ADC(Z80.REG_B);
		tableCodes[0x89] = new ADC(Z80.REG_C);
		tableCodes[0x8A] = new ADC(Z80.REG_D);
		tableCodes[0x8B] = new ADC(Z80.REG_E);
		tableCodes[0x8C] = new ADC(Z80.REG_H);
		tableCodes[0x8D] = new ADC(Z80.REG_L);
		tableCodes[0x8E] = new ADC(Z80.ADDR_HL);
		tableCodes[0x8F] = new ADC(Z80.REG_A);
		tableCodes[0xCE] = new ADC(Z80.DIRECT_8);
		tableCodesDD[0x8E] = new ADC(Z80.IX_D);
		tableCodesFD[0x8E] = new ADC(Z80.IY_D);

		tableCodes[0x90] = new SUB(Z80.REG_B);
		tableCodes[0x91] = new SUB(Z80.REG_C);
		tableCodes[0x92] = new SUB(Z80.REG_D);
		tableCodes[0x93] = new SUB(Z80.REG_E);
		tableCodes[0x94] = new SUB(Z80.REG_H);
		tableCodes[0x95] = new SUB(Z80.REG_L);
		tableCodes[0x96] = new SUB(Z80.ADDR_HL);
		tableCodes[0x97] = new SUB(Z80.REG_A);
		tableCodes[0xD6] = new SUB(Z80.DIRECT_8);
		tableCodesDD[0x96] = new SUB(Z80.IX_D);
		tableCodesFD[0x96] = new SUB(Z80.IY_D);

		tableCodes[0x98] = new SBC(Z80.REG_B);
		tableCodes[0x99] = new SBC(Z80.REG_C);
		tableCodes[0x9A] = new SBC(Z80.REG_D);
		tableCodes[0x9B] = new SBC(Z80.REG_E);
		tableCodes[0x9C] = new SBC(Z80.REG_H);
		tableCodes[0x9D] = new SBC(Z80.REG_L);
		tableCodes[0x9E] = new SBC(Z80.ADDR_HL);
		tableCodes[0x9F] = new SBC(Z80.REG_A);
		tableCodes[0xDE] = new SBC(Z80.DIRECT_8);
		tableCodesDD[0x9E] = new SBC(Z80.IX_D);
		tableCodesFD[0x9E] = new SBC(Z80.IY_D);

		tableCodes[0x04] = new INC(Z80.REG_B);
		tableCodes[0x0C] = new INC(Z80.REG_C);
		tableCodes[0x14] = new INC(Z80.REG_D);
		tableCodes[0x1C] = new INC(Z80.REG_E);
		tableCodes[0x24] = new INC(Z80.REG_H);
		tableCodes[0x2C] = new INC(Z80.REG_L);
		tableCodes[0x34] = new INC(Z80.ADDR_HL);
		tableCodes[0x3C] = new INC(Z80.REG_A);
		tableCodesDD[0x34] = new INC(Z80.IX_D);
		tableCodesFD[0x34] = new INC(Z80.IY_D);

		tableCodes[0x05] = new DEC(Z80.REG_B);
		tableCodes[0x0D] = new DEC(Z80.REG_C);
		tableCodes[0x15] = new DEC(Z80.REG_D);
		tableCodes[0x1D] = new DEC(Z80.REG_E);
		tableCodes[0x25] = new DEC(Z80.REG_H);
		tableCodes[0x2D] = new DEC(Z80.REG_L);
		tableCodes[0x35] = new DEC(Z80.ADDR_HL);
		tableCodes[0x3D] = new DEC(Z80.REG_A);
		tableCodesDD[0x35] = new DEC(Z80.IX_D);
		tableCodesFD[0x35] = new DEC(Z80.IY_D);

		// Op. Artiméticas 16 Bits
		tableCodes[0x09] = new ADD_16(Z80.REG_HL, Z80.REG_BC);
		tableCodes[0x19] = new ADD_16(Z80.REG_HL, Z80.REG_DE);
		tableCodes[0x29] = new ADD_16(Z80.REG_HL, Z80.REG_HL);
		tableCodes[0x39] = new ADD_16(Z80.REG_HL, Z80.REG_SP);
		tableCodesDD[0x09] = new ADD_16(Z80.REG_IX, Z80.REG_BC);
		tableCodesDD[0x19] = new ADD_16(Z80.REG_IX, Z80.REG_DE);
		tableCodesDD[0x29] = new ADD_16(Z80.REG_IX, Z80.REG_IX);
		tableCodesDD[0x39] = new ADD_16(Z80.REG_IX, Z80.REG_SP);
		tableCodesFD[0x09] = new ADD_16(Z80.REG_IY, Z80.REG_BC);
		tableCodesFD[0x19] = new ADD_16(Z80.REG_IY, Z80.REG_DE);
		tableCodesFD[0x29] = new ADD_16(Z80.REG_IY, Z80.REG_IY);
		tableCodesFD[0x39] = new ADD_16(Z80.REG_IY, Z80.REG_SP);

		tableCodesED[0x4A] = new ADC_16(Z80.REG_HL, Z80.REG_BC);
		tableCodesED[0x5A] = new ADC_16(Z80.REG_HL, Z80.REG_DE);
		tableCodesED[0x6A] = new ADC_16(Z80.REG_HL, Z80.REG_HL);
		tableCodesED[0x7A] = new ADC_16(Z80.REG_HL, Z80.REG_SP);

		tableCodesED[0x42] = new SBC_16(Z80.REG_HL, Z80.REG_BC);
		tableCodesED[0x52] = new SBC_16(Z80.REG_HL, Z80.REG_DE);
		tableCodesED[0x62] = new SBC_16(Z80.REG_HL, Z80.REG_HL);
		tableCodesED[0x72] = new SBC_16(Z80.REG_HL, Z80.REG_SP);

		tableCodes[0x03] = new INC_16(Z80.REG_BC);
		tableCodes[0x13] = new INC_16(Z80.REG_DE);
		tableCodes[0x23] = new INC_16(Z80.REG_HL);
		tableCodes[0x33] = new INC_16(Z80.REG_SP);
		tableCodesDD[0x23] = new INC_16(Z80.REG_IX);
		tableCodesFD[0x23] = new INC_16(Z80.REG_IY);

		tableCodes[0x0B] = new DEC_16(Z80.REG_BC);
		tableCodes[0x1B] = new DEC_16(Z80.REG_DE);
		tableCodes[0x2B] = new DEC_16(Z80.REG_HL);
		tableCodes[0x3B] = new DEC_16(Z80.REG_SP);
		tableCodesDD[0x2B] = new DEC_16(Z80.REG_IX);
		tableCodesFD[0x2B] = new DEC_16(Z80.REG_IY);

		// Instrucciones de Bit
		tableCodesCB[0xC0] = new SET(0, Z80.REG_B);
		tableCodesCB[0xC1] = new SET(0, Z80.REG_C);
		tableCodesCB[0xC2] = new SET(0, Z80.REG_D);
		tableCodesCB[0xC3] = new SET(0, Z80.REG_E);
		tableCodesCB[0xC4] = new SET(0, Z80.REG_H);
		tableCodesCB[0xC5] = new SET(0, Z80.REG_L);
		tableCodesCB[0xC6] = new SET(0, Z80.ADDR_HL);
		tableCodesCB[0xC7] = new SET(0, Z80.REG_A);

		tableCodesCB[0xC8] = new SET(1, Z80.REG_B);
		tableCodesCB[0xC9] = new SET(1, Z80.REG_C);
		tableCodesCB[0xCA] = new SET(1, Z80.REG_D);
		tableCodesCB[0xCB] = new SET(1, Z80.REG_E);
		tableCodesCB[0xCC] = new SET(1, Z80.REG_H);
		tableCodesCB[0xCD] = new SET(1, Z80.REG_L);
		tableCodesCB[0xCE] = new SET(1, Z80.ADDR_HL);
		tableCodesCB[0xCF] = new SET(1, Z80.REG_A);

		tableCodesCB[0xD0] = new SET(2, Z80.REG_B);
		tableCodesCB[0xD1] = new SET(2, Z80.REG_C);
		tableCodesCB[0xD2] = new SET(2, Z80.REG_D);
		tableCodesCB[0xD3] = new SET(2, Z80.REG_E);
		tableCodesCB[0xD4] = new SET(2, Z80.REG_H);
		tableCodesCB[0xD5] = new SET(2, Z80.REG_L);
		tableCodesCB[0xD6] = new SET(2, Z80.ADDR_HL);
		tableCodesCB[0xD7] = new SET(2, Z80.REG_A);

		tableCodesCB[0xD8] = new SET(3, Z80.REG_B);
		tableCodesCB[0xD9] = new SET(3, Z80.REG_C);
		tableCodesCB[0xDA] = new SET(3, Z80.REG_D);
		tableCodesCB[0xDB] = new SET(3, Z80.REG_E);
		tableCodesCB[0xDC] = new SET(3, Z80.REG_H);
		tableCodesCB[0xDD] = new SET(3, Z80.REG_L);
		tableCodesCB[0xDE] = new SET(3, Z80.ADDR_HL);
		tableCodesCB[0xDF] = new SET(3, Z80.REG_A);

		tableCodesCB[0xE0] = new SET(4, Z80.REG_B);
		tableCodesCB[0xE1] = new SET(4, Z80.REG_C);
		tableCodesCB[0xE2] = new SET(4, Z80.REG_D);
		tableCodesCB[0xE3] = new SET(4, Z80.REG_E);
		tableCodesCB[0xE4] = new SET(4, Z80.REG_H);
		tableCodesCB[0xE5] = new SET(4, Z80.REG_L);
		tableCodesCB[0xE6] = new SET(4, Z80.ADDR_HL);
		tableCodesCB[0xE7] = new SET(4, Z80.REG_A);

		tableCodesCB[0xE8] = new SET(5, Z80.REG_B);
		tableCodesCB[0xE9] = new SET(5, Z80.REG_C);
		tableCodesCB[0xEA] = new SET(5, Z80.REG_D);
		tableCodesCB[0xEB] = new SET(5, Z80.REG_E);
		tableCodesCB[0xEC] = new SET(5, Z80.REG_H);
		tableCodesCB[0xED] = new SET(5, Z80.REG_L);
		tableCodesCB[0xEE] = new SET(5, Z80.ADDR_HL);
		tableCodesCB[0xEF] = new SET(5, Z80.REG_A);

		tableCodesCB[0xF0] = new SET(6, Z80.REG_B);
		tableCodesCB[0xF1] = new SET(6, Z80.REG_C);
		tableCodesCB[0xF2] = new SET(6, Z80.REG_D);
		tableCodesCB[0xF3] = new SET(6, Z80.REG_E);
		tableCodesCB[0xF4] = new SET(6, Z80.REG_H);
		tableCodesCB[0xF5] = new SET(6, Z80.REG_L);
		tableCodesCB[0xF6] = new SET(6, Z80.ADDR_HL);
		tableCodesCB[0xF7] = new SET(6, Z80.REG_A);

		tableCodesCB[0xF8] = new SET(7, Z80.REG_B);
		tableCodesCB[0xF9] = new SET(7, Z80.REG_C);
		tableCodesCB[0xFA] = new SET(7, Z80.REG_D);
		tableCodesCB[0xFB] = new SET(7, Z80.REG_E);
		tableCodesCB[0xFC] = new SET(7, Z80.REG_H);
		tableCodesCB[0xFD] = new SET(7, Z80.REG_L);
		tableCodesCB[0xFE] = new SET(7, Z80.ADDR_HL);
		tableCodesCB[0xFF] = new SET(7, Z80.REG_A);

		tableCodesDDCB[0xC6] = new SET(0, Z80.IX_D);
		tableCodesDDCB[0xCE] = new SET(1, Z80.IX_D);
		tableCodesDDCB[0xD6] = new SET(2, Z80.IX_D);
		tableCodesDDCB[0xDE] = new SET(3, Z80.IX_D);
		tableCodesDDCB[0xE6] = new SET(4, Z80.IX_D);
		tableCodesDDCB[0xEE] = new SET(5, Z80.IX_D);
		tableCodesDDCB[0xF6] = new SET(6, Z80.IX_D);
		tableCodesDDCB[0xFE] = new SET(7, Z80.IX_D);

		tableCodesFDCB[0xC6] = new SET(0, Z80.IY_D);
		tableCodesFDCB[0xCE] = new SET(1, Z80.IY_D);
		tableCodesFDCB[0xD6] = new SET(2, Z80.IY_D);
		tableCodesFDCB[0xDE] = new SET(3, Z80.IY_D);
		tableCodesFDCB[0xE6] = new SET(4, Z80.IY_D);
		tableCodesFDCB[0xEE] = new SET(5, Z80.IY_D);
		tableCodesFDCB[0xF6] = new SET(6, Z80.IY_D);
		tableCodesFDCB[0xFE] = new SET(7, Z80.IY_D);

		logger.trace("Created Map Tables");

		/* A cada instrucción se le asignan sus opcodes y prefijos en caso necesario */
		for (int idx = 0; idx < Z80.MAP_SIZE; idx++) {
			if (tableCodes[idx] != null) {
				tableCodes[idx].setCPU(cpu);
				tableCodes[idx].setOpCode((byte) idx);
			}

			if (tableCodesCB[idx] != null) {
				tableCodesCB[idx].setCPU(cpu);
				tableCodesCB[idx].setOpCode((byte) idx);
				tableCodesCB[idx].setPrefix((byte) 0xCB);
			}

			if (tableCodesDD[idx] != null) {
				tableCodesDD[idx].setCPU(cpu);
				tableCodesDD[idx].setOpCode((byte) idx);
				tableCodesDD[idx].setPrefix((byte) 0xDD);
			}

			if (tableCodesED[idx] != null) {
				tableCodesED[idx].setCPU(cpu);
				tableCodesED[idx].setOpCode((byte) idx);
				tableCodesED[idx].setPrefix((byte) 0xED);
			}

			if (tableCodesFD[idx] != null) {
				tableCodesFD[idx].setCPU(cpu);
				tableCodesFD[idx].setOpCode((byte) idx);
				tableCodesFD[idx].setPrefix((byte) 0xFD);
			}

			if (tableCodesDDCB[idx] != null) {
				tableCodesDDCB[idx].setCPU(cpu);
				tableCodesDDCB[idx].setOpCode((byte) idx);
				tableCodesDDCB[idx].setPrefix((byte) 0xDD);
				tableCodesDDCB[idx].setPrefix2((byte) 0xCB);
			}

			if (tableCodesFDCB[idx] != null) {
				tableCodesFDCB[idx].setCPU(cpu);
				tableCodesFDCB[idx].setOpCode((byte) idx);
				tableCodesFDCB[idx].setPrefix((byte) 0xFD);
				tableCodesFDCB[idx].setPrefix2((byte) 0xCB);
			}
		}

		logger.trace("Asigned OpCodes");
	}

	@Override
	public String toString() {
		StringBuilder tab = new StringBuilder();
		for (int idx = 0; idx < Z80.MAP_SIZE; idx++) {
			Instruction ins = tableCodes[idx];
			if (ins != null) {
				tab.append("\t" + Hex.byteToHex((byte) idx) + "\t" + ins.getAssembly() + "\n");
			}
			ins = tableCodesCB[idx];
			if (ins != null) {
				tab.append("CB\t" + Hex.byteToHex((byte) idx) + "\t" + ins.getAssembly() + "\n");
			}
			ins = tableCodesDD[idx];
			if (ins != null) {
				tab.append("DD\t" + Hex.byteToHex((byte) idx) + "\t" + ins.getAssembly() + "\n");
			}
			ins = tableCodesED[idx];
			if (ins != null) {
				tab.append("ED\t" + Hex.byteToHex((byte) idx) + "\t" + ins.getAssembly() + "\n");
			}
			ins = tableCodesFD[idx];
			if (ins != null) {
				tab.append("FD\t" + Hex.byteToHex((byte) idx) + "\t" + ins.getAssembly() + "\n");
			}
			ins = tableCodesDDCB[idx];
			if (ins != null) {
				tab.append("DD CB\t" + Hex.byteToHex((byte) idx) + "\t" + ins.getAssembly() + "\n");
			}
			ins = tableCodesFDCB[idx];
			if (ins != null) {
				tab.append("FD CB\t" + Hex.byteToHex((byte) idx) + "\t" + ins.getAssembly() + "\n");
			}
		}

		return tab.toString();
	}
}
