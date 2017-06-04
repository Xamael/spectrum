package org.maox.emulator.cpu.z80.asm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.maox.emulator.Emulator;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.core.RAM;
import org.maox.emulator.cpu.z80.Z80;
import org.maox.emulator.debug.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDTest {

	final private static Logger logger = LoggerFactory.getLogger(LDTest.class);

	/* CPU para ejecutar las pruebas */
	private static Z80 cpu;
	private static RAM memory;

	@Test
	public void testExecuteBC() {

		try {
			/* Prueba registro BC (Direccionamiento memoria) */
			Instruction ins = new LD(Z80.REG_A, Z80.ADDR_BC);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setB((byte) 0x0A);
			cpu.setC((byte) 0xC5);
			logger.trace("Register BC: {}h", Hex.addressToString(cpu.getBC()));
			cpu.write8(cpu.getBC(), (byte) 0xBC);
			logger.trace("(BC): {}h", Hex.byteToHex(cpu.read8(cpu.getBC())));
			ins.execute();

			assertEquals((byte) 0xBC, cpu.getA());

			/* Inversa */
			ins = new LD(Z80.ADDR_BC, Z80.REG_A);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setB((byte) 0x05);
			cpu.setC((byte) 0xC9);
			cpu.setA((byte) 0xBC);
			ins.execute();
			logger.trace("(BC): {}h", Hex.byteToHex(cpu.read8(cpu.getBC())));

			assertEquals((byte) 0xBC, cpu.read8(cpu.getBC()));

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteDE() {

		try {
			/* Prueba registro DE (Direccionamiento memoria) */
			Instruction ins = new LD(Z80.REG_A, Z80.ADDR_DE);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setD((byte) 0x03);
			cpu.setE((byte) 0x09);
			logger.trace("Register DE: {}h", Hex.addressToString(cpu.getDE()));
			cpu.write8(cpu.getDE(), (byte) 0xDE);
			logger.trace("(DE): {}h", Hex.byteToHex(cpu.read8(cpu.getDE())));
			ins.execute();

			assertEquals((byte) 0xDE, cpu.getA());

			/* Inversa */
			ins = new LD(Z80.ADDR_DE, Z80.REG_A);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setD((byte) 0x06);
			cpu.setE((byte) 0x0E);
			cpu.setA((byte) 0xA3);
			logger.trace("(DE): {}h", Hex.byteToHex(cpu.read8(cpu.getDE())));
			ins.execute();

			assertEquals((byte) 0xA3, cpu.read8(cpu.getDE()));

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteDirect() {

		try {
			/* Prueba registro carga directa */
			Instruction ins = new LD(Z80.REG_A, Z80.DIRECT_8);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x0010);
			cpu.write8(0x0010, (byte) 0xFE);
			ins.execute();

			assertEquals((byte) 0xFE, cpu.getA());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteDirect16() {

		try {
			/* Prueba registro carga directa 16 bits */
			Instruction ins = new LD_16(Z80.REG_BC, Z80.DIRECT_16);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x0010);
			cpu.write8(0x0010, (byte) 0xFE);
			cpu.write8(0x0011, (byte) 0x0A);
			ins.execute();

			assertEquals(0x0AFE, cpu.getBC());

			/* Prueba registro carga directa 16 bits Puntero de Pila */
			ins = new LD_16(Z80.REG_SP, Z80.DIRECT_16);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x0020);
			cpu.write8(0x0020, (byte) 0xED);
			cpu.write8(0x0021, (byte) 0x1C);
			ins.execute();

			assertEquals(0x1CED, cpu.getSP());

			/* Prueba registro carga directa 16 bits Registro Especial */
			ins = new LD_16(Z80.REG_IX, Z80.DIRECT_16);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x0030);
			cpu.write8(0x0030, (byte) 0xAB);
			cpu.write8(0x0031, (byte) 0xF0);
			ins.execute();

			assertEquals(0xF0AB, cpu.getIX());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteHL() {

		try {
			/* Prueba registro HL (Direccionamiento memoria) */
			Instruction ins = new LD(Z80.REG_B, Z80.ADDR_HL);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setH((byte) 0x00);
			cpu.setL((byte) 0x0D);
			logger.trace("Register HL: {}h", Hex.addressToString(cpu.getHL()));
			cpu.write8(cpu.getHL(), (byte) 0x67);
			logger.trace("(HL): {}h", Hex.byteToHex(cpu.read8(cpu.getHL())));
			ins.execute();

			assertEquals((byte) 0x67, cpu.getB());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteIX_d() {

		try {
			/* Prueba registro IX + d (Direccionamiento memoria con desplazamiento) */
			Instruction ins = new LD(Z80.REG_B, Z80.IX_D);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			int address = 0x00FF;
			cpu.setPC(address);
			cpu.setIX((short) 0x0AAF);
			cpu.write8(address, (byte) 0x10); /* Desplazamiento Positivo (10 unidades) */
			cpu.write8(0x0ABF, (byte) 0xBB); /* Destino IX+d */
			ins.execute();

			assertEquals((byte) 0xBB, cpu.getB());

			/* Prueba registro IY + d (Direccionamiento memoria con desplazamiento) */
			ins = new LD(Z80.REG_B, Z80.IY_D);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			address = 0x0CFF;
			cpu.setPC(address);
			cpu.setIY((short) 0x0AAF);
			cpu.write8(address, (byte) 0xFD); /* Desplazamiento Negativo (3 unidades) */
			cpu.write8(0x0AAC, (byte) 0XB0); /* Destino IY+d */
			ins.execute();

			assertEquals((byte) 0XB0, cpu.getB());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteMem16FromReg() {
		try {
			/* Prueba registro BC */
			Instruction ins = new LD_16(Z80.ADDR_NN_16, Z80.REG_BC);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x1100);
			cpu.write8(0x1100, (byte) 0xFF);
			cpu.write8(0x1101, (byte) 0x00);
			cpu.setB((byte) 0xDB);
			cpu.setC((byte) 0xB7);
			ins.execute();
			assertEquals(0xDBB7, cpu.read16(0x00FF));

			/* Prueba registro HL */
			ins = new LD_16(Z80.ADDR_NN_16, Z80.REG_HL);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x1110);
			cpu.write8(0x1110, (byte) 0x4B);
			cpu.write8(0x1111, (byte) 0x0A);
			cpu.setH((byte) 0x9F);
			cpu.setL((byte) 0xFB);
			ins.execute();
			assertEquals(0x9FFB, cpu.read16(0x0A4B));

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteMem8FromDirect() {
		try {
			/* Prueba registro B */
			Instruction ins = new LD(Z80.ADDR_HL, Z80.DIRECT_8);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setH((byte) 0x11);
			cpu.setL((byte) 0x4B);
			cpu.setPC(0x13);
			cpu.write8(0x13, (byte) 0xEE);
			ins.execute();
			assertEquals((byte) 0xEE, cpu.read8(cpu.getHL()));

			/* Prueba registro IX + d (Direccionamiento memoria con desplazamiento) */
			ins = new LD(Z80.IX_D, Z80.DIRECT_8);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			int address = 0x00FF;
			cpu.setPC(address);
			cpu.setIX((short) 0x0AAF);
			cpu.write8(address, (byte) 0x10); /* Desplazamiento Positivo (10 unidades) */
			cpu.write8(address + 1, (byte) 0xAF); /* Dato */
			ins.execute();

			assertEquals((byte) 0xAF, cpu.read8(0x0ABF)); /* Destino IX+d */

			/* Prueba registro IY + d (Direccionamiento memoria con desplazamiento) */
			ins = new LD(Z80.IX_D, Z80.DIRECT_8);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			address = 0x0CFF;
			cpu.setPC(address);
			cpu.setIY((short) 0x0AAF);
			cpu.write8(address, (byte) 0xFD); /* Desplazamiento Negativo (3 unidades) */
			cpu.write8(address + 1, (byte) 0xBF); /* Dato */
			ins.execute();

			assertEquals((byte) 0xBF, cpu.read8(0x0AAC)); /* Destino IY+d */

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteMem8FromReg() {
		try {
			/* Prueba registro B */
			Instruction ins = new LD(Z80.ADDR_HL, Z80.REG_B);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setH((byte) 0x11);
			cpu.setL((byte) 0x4B);
			cpu.setB((byte) 0xB7);
			logger.trace("Register B: {}h", Hex.byteToHex(cpu.getB()));
			ins.execute();
			assertEquals((byte) 0xB7, cpu.read8(cpu.getHL()));

			/* Prueba registro E */
			ins = new LD(Z80.ADDR_HL, Z80.REG_E);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setH((byte) 0x1F);
			cpu.setL((byte) 0xCC);
			cpu.setE((byte) 0x9F);
			logger.trace("Register E: {}h", Hex.byteToHex(cpu.getE()));
			ins.execute();
			assertEquals((byte) 0x9F, cpu.read8(cpu.getHL()));

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteMem8Offset() {

		try {
			/* Prueba registro IX + d (Direccionamiento memoria con desplazamiento) */
			Instruction ins = new LD(Z80.IX_D, Z80.REG_A);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			int address = 0x00FF;
			cpu.setPC(address);
			cpu.setIX((short) 0x0AAF);
			cpu.write8(address, (byte) 0x10); /* Desplazamiento Positivo (10 unidades) */
			cpu.setA((byte) 0xAA);
			ins.execute();

			assertEquals((byte) 0xAA, cpu.read8(0x0ABF)); /* Destino IX+d */

			/* Prueba registro IY + d (Direccionamiento memoria con desplazamiento) */
			ins = new LD(Z80.IX_D, Z80.REG_H);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			address = 0x0CFF;
			cpu.setPC(address);
			cpu.setIY((short) 0x0AAF);
			cpu.write8(address, (byte) 0xFD); /* Desplazamiento Negativo (3 unidades) */
			cpu.setH((byte) 0XB0);
			ins.execute();

			assertEquals((byte) 0XB0, cpu.read8(0x0AAC)); /* Destino IY+d */

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteNN() {

		try {
			/* Prueba registro carga con direccionamiento directo 16 bits */
			Instruction ins = new LD(Z80.REG_A, Z80.ADDR_NN);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x0010);
			cpu.write8(0x0010, (byte) 0xFE); /* Parte baja */
			cpu.write8(0x0011, (byte) 0x0A); /* Parte alta */
			cpu.write8(0x0AFE, (byte) 0x88);
			ins.execute();

			assertEquals((byte) 0x88, cpu.getA());

			/* Inversa */
			ins = new LD(Z80.ADDR_NN, Z80.REG_A);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x0010);
			cpu.write8(0x0010, (byte) 0xDD); /* Parte baja */
			cpu.write8(0x0011, (byte) 0x07); /* Parte alta */
			cpu.setA((byte) 0x99);
			ins.execute();

			assertEquals((byte) 0x99, cpu.read8(0x07DD));

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteNN_16() {

		try {
			/* Prueba registro carga con direccionamiento directo 16 bits */
			Instruction ins = new LD_16(Z80.REG_HL, Z80.ADDR_NN_16);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x0010);
			cpu.write8(0x0010, (byte) 0xFE); /* Parte baja */
			cpu.write8(0x0011, (byte) 0x0A); /* Parte alta */
			cpu.write8(0x0AFE, (byte) 0x88); /* nn */
			cpu.write8(0x0AFF, (byte) 0x99); /* nn+1 */
			ins.execute();

			assertEquals(0x9988, cpu.getHL());

			/* Inversa */
			ins = new LD_16(Z80.REG_DE, Z80.ADDR_NN_16);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x0010);
			cpu.write8(0x0010, (byte) 0xDD); /* Parte baja */
			cpu.write8(0x0011, (byte) 0x07); /* Parte alta */
			cpu.write8(0x07DD, (byte) 0x78); /* nn */
			cpu.write8(0x07DE, (byte) 0x9A); /* nn+1 */
			ins.execute();

			assertEquals(0x9A78, cpu.getDE());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteReg() {
		try {
			/* Prueba registro B */
			Instruction ins = new LD(Z80.REG_B, Z80.REG_A);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setA((byte) 0xD3);
			cpu.setB((byte) 0x00);
			logger.trace("Register A: {}h", Hex.byteToHex(cpu.getA()));
			ins.execute();
			assertEquals((byte) 0xD3, cpu.getB());

			ins = new LD(Z80.REG_B, Z80.REG_E);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setE((byte) 0x9F);
			logger.trace("Register E: {}h", Hex.byteToHex(cpu.getE()));
			ins.execute();
			assertEquals((byte) 0x9F, cpu.getB());

			/* Registro Especial */
			ins = new LD(Z80.REG_A, Z80.REG_I);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setI((byte) 0x77);
			logger.trace("Register I: {}h", Hex.byteToHex(cpu.getI()));
			ins.execute();
			assertEquals((byte) 0x77, cpu.getA());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteSP() {
		try {
			/* Prueba registro HL */
			Instruction ins = new LD_16(Z80.REG_SP, Z80.REG_HL);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setH((byte) 0xD3);
			cpu.setL((byte) 0x00);
			ins.execute();
			assertEquals(cpu.getSP(), cpu.getHL());

			ins = new LD_16(Z80.REG_SP, Z80.REG_IX);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setIX(0x9FE9);
			ins.execute();
			assertEquals(cpu.getSP(), cpu.getIX());

			/* Registro Especial */
			ins = new LD_16(Z80.REG_SP, Z80.REG_IY);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setIY(0x7766);
			ins.execute();
			assertEquals(cpu.getSP(), cpu.getIY());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/* Se crea una CPU Z80A */
		cpu = new Z80();
		/* Se crea un modulo de memoria de 8K */
		memory = new RAM(8192);
		/* Se le asigna la memoria a la CPU */
		cpu.setDataBus(memory);
	}
}
