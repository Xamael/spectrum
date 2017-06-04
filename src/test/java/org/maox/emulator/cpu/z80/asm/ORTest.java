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

public class ORTest {

	final private static Logger logger = LoggerFactory.getLogger(ORTest.class);

	/* CPU para ejecutar las pruebas */
	private static Z80 cpu;
	private static RAM memory;

	@Test
	public void testExecuteDirect() {

		try {
			/* Valor directo */
			Instruction ins = new OR(Z80.DIRECT_8);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setA((byte) 0b11100111);
			int address = 0x0FFF;
			cpu.setPC(address);
			cpu.write8(address, (byte) 0b11001011);
			ins.execute();
			logger.trace("A <- A | n: {}h", Hex.byteToHex(cpu.getA()));

			assertEquals(cpu.getA(), (byte) 0b11101111);

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteHL() {

		try {

			/* Prueba registro HL (Direccionamiento memoria) */
			Instruction ins = new OR(Z80.ADDR_HL);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setA((byte) 0b10101010);
			logger.trace("Register A: {}h", Hex.byteToHex(cpu.getA()));
			cpu.setH((byte) 0x03);
			cpu.setL((byte) 0xFF);
			logger.trace("Register HL: {}h", Hex.addressToString(cpu.getHL()));
			cpu.write8(cpu.getHL(), (byte) 0b11000000);
			logger.trace("(HL): {}h", Hex.byteToHex(cpu.read8(cpu.getHL())));
			ins.execute();
			logger.trace("A <- A | (HL): {}h", Hex.byteToHex(cpu.getA()));

			assertEquals(cpu.getA(), (byte) 0b11101010);

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteIX_d() {

		try {

			/* Prueba registro IX + d (Direccionamiento memoria con desplazamiento) */
			Instruction ins = new OR(Z80.IX_D);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setA((byte) 0b10101010);
			int address = 0x00FF;
			cpu.setPC(address);
			cpu.setIX((short) 0x0AAF);
			cpu.write8(address, (byte) 0x01); /* Desplazamiento Positivo (una unidad) */
			cpu.write8(0x0AB0, (byte) 0b11001100); /* Destino IX+d */
			ins.execute();
			logger.trace("A <- A | (IX + d): {}h", Hex.byteToHex(cpu.getA()));

			assertEquals(cpu.getA(), (byte) 0b11101110);

			/* Prueba registro IY + d (Direccionamiento memoria con desplazamiento) */
			ins = new OR(Z80.IY_D);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setA((byte) 0b10111011);
			address = 0x0CFF;
			cpu.setPC(address);
			cpu.setIY((short) 0x0AAF);
			cpu.write8(address, (byte) 0xFF); /* Desplazamiento Negativo (una unidad) */
			cpu.write8(0x0AAE, (byte) 0b11001101); /* Destino IY+d */
			ins.execute();
			logger.trace("A <- A | (IY + d): {}h", Hex.byteToHex(cpu.getA()));

			assertEquals(cpu.getA(), (byte) 0b11111111);

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteReg() {

		try {
			/* Prueba registro B */
			Instruction ins = new OR(Z80.REG_B);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setA((byte) 0b10101010);
			logger.trace("Register A: {}h", Hex.byteToHex(cpu.getA()));
			cpu.setB((byte) 0b01010101);
			logger.trace("Register B: {}h", Hex.byteToHex(cpu.getB()));
			ins.execute();
			logger.trace("A <- A | B: {}h", Hex.byteToHex(cpu.getA()));

			assertEquals(cpu.getA(), (byte) 0b11111111);

			/* Prueba registro E */
			ins = new OR(Z80.REG_E);
			ins.setCPU(cpu);
			logger.trace(ins.getAssembly());
			cpu.setA((byte) 0b11100111);
			logger.trace("Register A: {}h", Hex.byteToHex(cpu.getA()));
			cpu.setE((byte) 0b11000011);
			logger.trace("Register E: {}h", Hex.byteToHex(cpu.getE()));
			ins.execute();
			logger.trace("A <- A | E: {}h", Hex.byteToHex(cpu.getA()));

			assertEquals(cpu.getA(), (byte) 0b11100111);

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
