package org.maox.emulator;

import java.awt.EventQueue;
import java.io.IOException;

import org.maox.emulator.core.Computer;
import org.maox.emulator.exceptions.IllegalAddressException;
import org.maox.emulator.exceptions.InstructionException;
import org.maox.emulator.gui.AppFrame;
import org.maox.emulator.virtual.Spectrum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Emulador de 8 Bits
 * 
 * @author Alex Orgaz
 * 
 */
public class Emulator {

	/* Log */
	final private static Logger logger = LoggerFactory.getLogger(Emulator.class);

	private long OPTIMAL_TIME; // Tiempo optimo en ms entre Frames

	/* Marco visual de ejecución */
	private AppFrame app;
	/* Maquina Virtual Activa */
	private Computer computer;
	/* Ciclos entre cada refresco */
	private int refreshCycles;
	private int actualCycles;

	private int fps = 0;
	private long lastFpsTime = 0;
	private long lastLoop = getTimeMilis();

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 * @throws IllegalAddressException
	 * @throws InstructionException
	 */
	public Emulator() throws IOException, InstructionException, IllegalAddressException {
		init();
		/* Inicialización del entorno gráfico */
		initGUI();

		/* Inicialización de la maquina virtual */
		// TODO En un futuro lo haría la pantalla a elegir
		computer = new Spectrum();
		// Se obtien los ciclos entre cada refresco de pantalla
		refreshCycles = computer.getHerz() / computer.getFramesPerSecond();
		OPTIMAL_TIME = 1000 / computer.getFramesPerSecond();
	}

	/**
	 * Vuleca la información de debug del estado actual de la CPU y Memoria
	 */
	private void dump() {
		// TODO Auto-generated method stub
		if (computer != null) {
			logger.debug("\n" + computer.dump());
		} else {
			logger.debug("No dump information.");
		}
	}

	/**
	 * Inicialización básica de las variables del emulador
	 */
	private void init() {
		fps = 0;
		lastLoop = getTimeMilis();
	}

	/**
	 * Inialización del entorno gráfico
	 */
	private void initGUI() {
		final Emulator emu = this;
		/* Inicializa el entorno gráfico */
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				app = new AppFrame(emu);
				app.pack();
				app.setVisible(true);
			}
		});

		// Esperar hasta que se cree el GUI visual
		while (app == null) {
		}
	}

	/**
	 * Dibuja el contenido de pantalla
	 */
	private void repaintScreen() {
		if (actualCycles >= refreshCycles) {
			actualCycles -= refreshCycles;
			// Pintado del Frame
			app.refreshScreen(computer.getFrame());

			int delta = (int) (getTimeMilis() - lastLoop);
			lastLoop = getTimeMilis();
			// Contador de los FPS
			lastFpsTime += delta;
			fps++;

			// Actualizar el FPS si ha transcurrido un segundo
			if (lastFpsTime >= 1000) {
				app.updateFPS(fps);
				lastFpsTime = 0;
				fps = 0;
			}
		}
	}

	/**
	 * Arranca la ejecución limitando la velocidad a la real de la CPU emulada
	 * 
	 * @throws IllegalAddressException
	 * @throws InstructionException
	 */
	public void run() throws InstructionException, IllegalAddressException {
		while (true) {
			actualCycles += computer.execute();
			// Pintado de pantalla (en caso necesario)
			repaintScreen();
			// Se duerme en caso necesario
			sleep();
		}
	}

	/**
	 * Arranca la ejecución sin limitar la velocidad de la CPU
	 * 
	 * @throws IllegalAddressException
	 * @throws InstructionException
	 */
	public void runFast() throws InstructionException, IllegalAddressException {
		while (true) {
			actualCycles += computer.execute();
			// Pintado de pantalla (en caso necesario)
			repaintScreen();
		}
	}

	/**
	 * Arranca la ejecución de una instrucción
	 * 
	 * @throws IllegalAddressException
	 * @throws InstructionException
	 */
	public void runOne() throws InstructionException, IllegalAddressException {
		actualCycles += computer.execute();
		// Pintado de pantalla (en caso necesario)
		repaintScreen();
	}

	/**
	 * Se duerme el tiempo necesario para no ir más rápido de lo configurado
	 */
	private void sleep() {
		// Dormir el Thread para optimizar la CPU
		// Tiempo que se ha tardado en actualizar la logica y renderizar todo
		long timeBetweenUpdate = getTimeMilis() - lastLoop;
		// Thread.yield();
		if (timeBetweenUpdate < OPTIMAL_TIME) {
			try {
				Thread.sleep(OPTIMAL_TIME - timeBetweenUpdate);
			} catch (InterruptedException e) {
			}
			;
		}
	}

	/**
	 * Obtiene el tiempo actual en milisegundos a partir del reloj
	 * de sistema LWJGL de alta resolución
	 * 
	 * @return El tiempo en milisegundos
	 */
	static public long getTimeMilis() {
		return (long) (System.nanoTime() / 1e6);
	}

	/**
	 * Manejador de Excepciones
	 * 
	 * @param e Excepcion a controlar
	 */
	public static void handleException(Exception e) {
		logger.error(e.toString());

		StackTraceElement[] arrayOfStackTraceElement = e.getStackTrace();
		for (StackTraceElement element : arrayOfStackTraceElement) {
			logger.error("\tat {}", element);
		}
	}

	/**
	 * Programa Lanzador del emulador
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Emulator emu = null;
		try {
			emu = new Emulator();
			/* Arranque de la maquina */
			// TODO En un futuro lo haría la pantalla desde botón
			emu.run();
		} catch (Exception e) {
			emu.dump();
			handleException(e);
		}
	}
}
