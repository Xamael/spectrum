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

public class DJNZTest {

	final private static Logger logger = LoggerFactory.getLogger(DJNZTest.class);

	/* CPU para ejecutar las pruebas */
	private static Z80 cpu;
	private static RAM memory;

	@Test
	public void test() {
		try {
			Instruction ins = new DJNZ(Z80.DESPLACEMENT);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setPC(0x0481);
			cpu.write8(0x0481, (byte) 0x03);
			cpu.setB((byte) 0x00);
			ins.execute();
			assertEquals(0x0482, cpu.getPC());

			cpu.setPC(0x0481);
			cpu.write8(0x0481, (byte) 0x03);
			cpu.setB((byte) 0x01);
			ins.execute();
			assertEquals(0x0485, cpu.getPC());

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
