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

public class ADDTest {

	final private static Logger logger = LoggerFactory.getLogger(ADDTest.class);

	/* CPU para ejecutar las pruebas */
	private static Z80 cpu;
	private static RAM memory;

	@Test
	public void testExecuteReg() {

		try {
			/* Prueba registro B */
			Instruction ins = new ADD(Z80.REG_B);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setA((byte) 0x01);
			logger.trace("Register A: {}h", Hex.byteToHex(cpu.getA()));
			cpu.setB((byte) 0x7E);
			logger.trace("Register B: {}h", Hex.byteToHex(cpu.getB()));
			ins.execute();
			logger.trace("A <- A + B: {}h", Hex.byteToHex(cpu.getA()));

			assertEquals(cpu.getA(), (byte) 0x7F);

			/* Prueba registro C */
			ins = new ADD(Z80.REG_C);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setA((byte) 0xFF);
			logger.trace("Register A: {}h", Hex.byteToHex(cpu.getA()));
			cpu.setC((byte) 0x01);
			logger.trace("Register C: {}h", Hex.byteToHex(cpu.getC()));
			ins.execute();
			logger.trace("A <- A + C: {}h", Hex.byteToHex(cpu.getA()));

			assertEquals(cpu.getA(), (byte) 0x00);

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
