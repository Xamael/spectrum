package org.maox.emulator.cpu.z80.asm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.maox.emulator.Emulator;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.core.RAM;
import org.maox.emulator.cpu.z80.Z80;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DECTest {

	final private static Logger logger = LoggerFactory.getLogger(DECTest.class);

	/* CPU para ejecutar las pruebas */
	private static Z80 cpu;
	private static RAM memory;

	@Test
	public void testExecute16Bits() {
		try {
			/* Decremento directo 16 */
			Instruction ins = new DEC_16(Z80.REG_BC);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setB((byte) 0x00);
			cpu.setC((byte) 0xFF);
			ins.execute();
			assertEquals(0x00FE, cpu.getBC());

			ins = new DEC_16(Z80.REG_IX);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setIX(0x0000);
			ins.execute();
			assertEquals(0xFFFF, cpu.getIX());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteAddr() {
		try {
			/* Decremento indirecto */
			Instruction ins = new DEC(Z80.ADDR_HL);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setH((byte) 0x01);
			cpu.setL((byte) 0xAA);
			cpu.write8(cpu.getHL(), (byte) 0xAA);
			ins.execute();
			assertEquals((byte) 0xA9, cpu.read8(cpu.getHL()));
			assertFalse(cpu.getFlag(Z80.FLAG_PV));

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteReg() {
		try {
			/* Decremento directo */
			Instruction ins = new DEC(Z80.REG_B);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setB((byte) 0x00);
			ins.execute();
			assertEquals((byte) 0xFF, cpu.getB());
			assertFalse(cpu.getFlag(Z80.FLAG_PV));

			cpu.setB((byte) 0x69);
			ins.execute();
			assertEquals((byte) 0x68, cpu.getB());
			assertFalse(cpu.getFlag(Z80.FLAG_PV));

			/* Overflow */
			cpu.setB((byte) 0x80);
			ins.execute();
			assertTrue(cpu.getFlag(Z80.FLAG_PV));

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
