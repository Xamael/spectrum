package org.maox.emulator.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.maox.arq.gui.component.GUIStatusBar;
import org.maox.arq.gui.menu.GUIMenuBar;
import org.maox.arq.gui.menu.GUIMenuItem;
import org.maox.arq.gui.menu.GUIMenuOption;
import org.maox.arq.gui.view.GUIFrame;
import org.maox.arq.gui.view.GUIImagePane;
import org.maox.emulator.Emulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Marco visual del emulador
 * 
 * @author Alex Orgaz
 * 
 */
@SuppressWarnings("serial")
public class AppFrame extends GUIFrame {

	/* Log */
	final private static Logger logger = LoggerFactory.getLogger(AppFrame.class);

	/* Barra de estado */
	private GUIStatusBar statusBar;
	/* Panel contenedor */
	private GUIImagePane imagePanel;
	/* Logica del emulador (Funcionaría como controlador) */
	private Emulator emulator = null;

	/**
	 * Inialización del Marco Visual
	 * 
	 * @param emulator
	 */
	public AppFrame(Emulator emulator) {
		super();
		this.emulator = emulator;
		setTitle("Emulator Z80");
		setBounds(400, 600, 300, 125);
		initLookAndFeel("Nimbus");
		this.addComponentListener(new EventHandler());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Tratamiento de los eventos de menu
		if (e.getSource() instanceof GUIMenuItem) {
			if (e.getActionCommand().equals("Open")) {
				logger.trace("Evento: {}", e.getActionCommand());
			}
		}
	}

	/**
	 * Cierra la aplicación con error de Excepcion
	 * 
	 * @param e Excepcion de cierre
	 */
	@Override
	protected void exit() {
		// Desvincular el marco
		this.dispose();
	}

	/**
	 * Inicialización de la parte gráfica del escritorio
	 */
	@Override
	protected void initComponents() {
		// Estructura Layout
		getContentPane().setLayout(new BorderLayout());

		// Se añade la barra de estatus en la parte de abajo
		statusBar = new GUIStatusBar();
		getContentPane().add(statusBar, BorderLayout.SOUTH);

		// Se crea el panel donde se añadirán las pantallas
		imagePanel = new GUIImagePane();
		getContentPane().add(imagePanel, BorderLayout.CENTER);
		imagePanel.setLayout(null);
		imagePanel.setAutoResize(true);
		imagePanel.setMantainRatio(true);
	}

	@Override
	protected void initMenu() {
		menu = new GUIMenuBar();

		GUIMenuOption menuOption = new GUIMenuOption("File");
		menuOption.setMnemonic(KeyEvent.VK_F);
		menu.add(menuOption);

		GUIMenuItem menuItem = new GUIMenuItem("Open", KeyEvent.VK_O);
		menuItem.setActionCommand("Open");
		menuOption.add(menuItem);
		menu.addItem(menuItem);
	}

	/**
	 * Refresca el contenido de la pantalla
	 * 
	 * @param screen Imagen de pantalla
	 */
	public void refreshScreen(BufferedImage screen) {
		imagePanel.setImage(screen);
		repaint();
	}

	/**
	 * Muestra los FPS en la barra de estado
	 * 
	 * @param fps Frames per second
	 */
	public void updateFPS(int fps) {
		statusBar.setMessage("FPS: " + fps);

	}

	/**
	 * Manejador de eventos de maximizado de ventana
	 * 
	 */
	private class EventHandler extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			if (getState() == JFrame.ICONIFIED) {
				// Minimizar
			} else if (getState() == JFrame.NORMAL) {
				imagePanel.autoResizeImage();
			} else {
				imagePanel.autoResizeImage();
			}
		}
	}
}
