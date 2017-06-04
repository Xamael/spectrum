package org.maox.emulator.cpu.z80;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.maox.emulator.Emulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Z80Test {

	final private static Logger logger = LoggerFactory.getLogger(Z80Test.class);

	/* CPU para pruebas */
	private static Z80 cpu;

	final byte testData[] = { (byte) 0x00, (byte) 0x01, (byte) 0x7F, (byte) 0x80, (byte) 0x81, (byte) 0xFF };

	@Test
	public void testAddWithCarry() {
		logger.trace("ADD with carry");

		cpu.setF((byte) 0x00);

		try {
			for (byte c = 0; c <= 1; c++) {
				for (byte a : testData) {
					for (byte b : testData) {
						cpu.setA(a);
						cpu.addWithCarry(b, c);

						logger.trace("FS {} FZ {} FC {} FO {} :\t{} + {} + {} = {}", cpu.getFlag(Z80.FLAG_S) ? 1 : 0,
								cpu.getFlag(Z80.FLAG_Z) ? 1 : 0, cpu.getFlag(Z80.FLAG_C) ? 1 : 0,
								cpu.getFlag(Z80.FLAG_PV) ? 1 : 0, a, b, c, cpu.getA());

						assertEquals((a + b + c) & 0x0FF, cpu.getA() & 0x0FF);
					}
				}
			}
		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testExchange() {
		try {

			logger.trace("Exchange Registers");
			cpu.setA((byte) 0xAB);
			cpu.setF((byte) 0x8B);
			cpu.exchangeAF();
			cpu.setA((byte) 0x45);
			cpu.setF((byte) 0x45);
			cpu.exchangeAF();
			assertTrue(cpu.getA() == (byte) 0xAB);
			assertTrue(cpu.getF() == (byte) 0x8B);
			cpu.exchangeAF();
			assertTrue(cpu.getA() == (byte) 0x45);
			assertTrue(cpu.getF() == (byte) 0x45);
			logger.trace("Exchange OK");

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testFlags() {
		try {
			/* Comprobación de FLAGS */
			cpu.setF((byte) 0x00);

			logger.trace("Checking Flags");
			assertFalse(cpu.getFlag(Z80.FLAG_S));
			assertFalse(cpu.getFlag(Z80.FLAG_Z));
			assertFalse(cpu.isFlagZero());
			assertFalse(cpu.getFlag(Z80.FLAG_5));
			assertFalse(cpu.getFlag(Z80.FLAG_H));
			assertFalse(cpu.getFlag(Z80.FLAG_3));
			assertFalse(cpu.getFlag(Z80.FLAG_PV));
			assertFalse(cpu.getFlag(Z80.FLAG_N));
			assertFalse(cpu.getFlag(Z80.FLAG_C));
			assertEquals(cpu.getCarry(), 0);
			assertFalse(cpu.isFlagCarry());

			cpu.setFlag(Z80.FLAG_S, true);
			assertTrue(cpu.getFlag(Z80.FLAG_S));
			assertFalse(cpu.getFlag(Z80.FLAG_Z));
			assertFalse(cpu.getFlag(Z80.FLAG_5));
			assertFalse(cpu.getFlag(Z80.FLAG_H));
			assertFalse(cpu.getFlag(Z80.FLAG_3));
			assertFalse(cpu.getFlag(Z80.FLAG_PV));
			assertFalse(cpu.getFlag(Z80.FLAG_N));
			assertFalse(cpu.getFlag(Z80.FLAG_C));
			assertEquals(cpu.getCarry(), 0);
			assertFalse(cpu.isFlagCarry());

			cpu.setFlag(Z80.FLAG_Z, true);
			cpu.setFlag(Z80.FLAG_H, true);
			cpu.setFlag(Z80.FLAG_C, true);
			assertTrue(cpu.getFlag(Z80.FLAG_S));
			assertTrue(cpu.getFlag(Z80.FLAG_Z));
			assertTrue(cpu.isFlagZero());
			assertFalse(cpu.getFlag(Z80.FLAG_5));
			assertTrue(cpu.getFlag(Z80.FLAG_H));
			assertFalse(cpu.getFlag(Z80.FLAG_3));
			assertFalse(cpu.getFlag(Z80.FLAG_PV));
			assertFalse(cpu.getFlag(Z80.FLAG_N));
			assertTrue(cpu.getFlag(Z80.FLAG_C));
			assertEquals(cpu.getCarry(), 1);
			assertTrue(cpu.isFlagCarry());

			cpu.setFlag(Z80.FLAG_S, false);
			cpu.setFlag(Z80.FLAG_Z, true);
			cpu.setFlag(Z80.FLAG_H, false);
			cpu.setFlag(Z80.FLAG_C, true);
			assertFalse(cpu.getFlag(Z80.FLAG_S));
			assertTrue(cpu.getFlag(Z80.FLAG_Z));
			assertTrue(cpu.isFlagZero());
			assertFalse(cpu.getFlag(Z80.FLAG_5));
			assertFalse(cpu.getFlag(Z80.FLAG_H));
			assertFalse(cpu.getFlag(Z80.FLAG_3));
			assertFalse(cpu.getFlag(Z80.FLAG_PV));
			assertFalse(cpu.getFlag(Z80.FLAG_N));
			assertTrue(cpu.getFlag(Z80.FLAG_C));
			assertEquals(cpu.getCarry(), 1);
			assertTrue(cpu.isFlagCarry());

			logger.trace("Flags OK");

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testFlagsOperations() {
		try {
			logger.trace("Setting Flags");
			cpu.setA((byte) 0xFF);
			cpu.setFlagSigned(cpu.getA());
			assertTrue(cpu.getFlag(Z80.FLAG_S));
			assertTrue(cpu.isFlagNegative());
			cpu.setFlagZero(cpu.getA());
			assertFalse(cpu.getFlag(Z80.FLAG_Z));
			assertFalse(cpu.isFlagZero());
			cpu.setFlagParity(cpu.getA());
			assertTrue(cpu.getFlag(Z80.FLAG_PV));

			cpu.setA((byte) 0x00);
			cpu.setFlagSigned(cpu.getA());
			assertFalse(cpu.getFlag(Z80.FLAG_S));
			assertTrue(!cpu.isFlagNegative());
			cpu.setFlagZero(cpu.getA());
			assertTrue(cpu.getFlag(Z80.FLAG_Z));
			assertTrue(cpu.isFlagZero());
			cpu.setFlagParity(cpu.getA());
			assertTrue(cpu.getFlag(Z80.FLAG_PV));

			cpu.setA((byte) 0x7F);
			cpu.setFlagSigned(cpu.getA());
			assertFalse(cpu.getFlag(Z80.FLAG_S));
			assertTrue(cpu.isFlagPositive());
			cpu.setFlagZero(cpu.getA());
			assertFalse(cpu.getFlag(Z80.FLAG_Z));
			assertFalse(cpu.isFlagZero());
			cpu.setFlagParity(cpu.getA());
			assertFalse(cpu.getFlag(Z80.FLAG_PV));

			logger.trace("Signed OK");
			logger.trace("Zero OK");
			logger.trace("Parity OK");

		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testSubWithCarry() {
		logger.trace("SUB with carry");

		cpu.setF((byte) 0x00);

		try {
			for (byte c = 0; c <= 1; c++) {
				for (byte a : testData) {
					for (byte b : testData) {
						cpu.setA(a);
						// a - b - c = a + ~b + 1 - c = a + ~b + !c
						cpu.subWithCarry(b, c);

						logger.trace("FS {} FZ {} FC {} FO {} :\t{} - {} - {} = {}", cpu.getFlag(Z80.FLAG_S) ? 1 : 0,
								cpu.getFlag(Z80.FLAG_Z) ? 1 : 0, cpu.getFlag(Z80.FLAG_C) ? 1 : 0,
								cpu.getFlag(Z80.FLAG_PV) ? 1 : 0, a, b, c, cpu.getA());

						assertEquals((a - b - c) & 0x0FF, cpu.getA() & 0x0FF);
					}
				}
			}
		} catch (Exception e) {
			Emulator.handleException(e);
			fail(e.getMessage());
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/* Se crea una CPU Z80A */
		cpu = new Z80();
	}
}
