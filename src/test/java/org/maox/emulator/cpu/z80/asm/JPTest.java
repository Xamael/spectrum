package org.maox.emulator.cpu.z80.asm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.maox.emulator.Emulator;
import org.maox.emulator.core.Instruction;
import org.maox.emulator.core.RAM;
import org.maox.emulator.cpu.z80.Z80;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPTest {

	final private static Logger logger = LoggerFactory.getLogger(JPTest.class);

	/* CPU para ejecutar las pruebas */
	private static Z80 cpu;
	private static RAM memory;

	@Test
	public void testExecuteDesplacement() {
		try {
			/* Salto Incondicional directo */
			Instruction ins = new JP(Z80.DESPLACEMENT);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x0481);
			cpu.write8(0x0481, (byte) 0x03);
			ins.execute();
			assertEquals(0x0485, cpu.getPC());

			cpu.setPC(0x01AB);
			cpu.write8(0x01AB, (byte) 0xFF);
			ins.execute();
			assertEquals(0x01AB, cpu.getPC());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteDirect() {
		try {
			/* Salto Incondicional directo */
			Instruction ins = new JP(Z80.DIRECT_16);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x01AB);
			cpu.write16(0x01AB, 0xABCD);
			ins.execute();
			assertEquals(0xABCD, cpu.getPC());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteDirectCond() {
		try {
			/* Salto Incondicional directo */
			Instruction ins = new JP(Z80.COND_Z, Z80.DIRECT_16);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x01AB);
			cpu.setF((byte) 0x00);
			cpu.write16(0x01AB, 0xABCD);
			ins.execute();
			assertEquals(0x01AD, cpu.getPC());

			/* Salto Incondicional directo */
			ins = new JP(Z80.COND_NZ, Z80.DIRECT_16);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x01AB);
			cpu.setF((byte) 0x00);
			cpu.write16(0x01AB, 0xABCD);
			ins.execute();
			assertEquals(0xABCD, cpu.getPC());

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteReg() {
		try {
			/* Salto Incondicional directo */
			Instruction ins = new JP(Z80.DIRECT_16);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x01AB);
			cpu.setH((byte) 0xAB);
			cpu.setL((byte) 0xCD);
			ins.execute();
			assertEquals(0xABCD, cpu.getPC());

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
