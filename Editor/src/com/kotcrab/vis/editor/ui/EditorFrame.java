/*
 * Copyright 2014-2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.util.vis.LaunchConfiguration;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Set;

/**
 * VisEditor AWT/Swing based frame that holds {@link LwjglCanvas}. {@link LwjglApplication} is not used directly,
 * because unfortunately we need some features from swing.
 * @author Kotcrab
 */
public class EditorFrame extends JFrame {
	private final LaunchConfiguration launchConfig;
	private Editor editor;
	private LwjglCanvas editorCanvas;

	public EditorFrame (SplashController splashController, LaunchConfiguration launchConfig) {
		this.launchConfig = launchConfig;
		setTitle("VisEditor");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing (WindowEvent e) {
				performEditorExit();
			}
		});

		setIconImage(loadImage("/com/kotcrab/vis/editor/icon.png"));

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.backgroundFPS = 0; //default is 60, when in background it takes a lot of cpu, maybe vsync causes it?

		editor = new Editor(this);

		editorCanvas = new LwjglCanvas(editor, config);
		Canvas canvas = editorCanvas.getCanvas();
		canvas.setSize(1280, 720);

		getContentPane().add(canvas, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(null);
		splashController.shouldClose = true;
	}

	public LaunchConfiguration getLaunchConfig () {
		return launchConfig;
	}

	public static void main (String[] args) {
		App.init();

		LaunchConfiguration launchConfig = new LaunchConfiguration();

		for (String arg : args) {
			if (arg.equals("--no-splash")) {
				launchConfig.showSplash = false;
				continue;
			}

			if (arg.equals("--scale-ui")) {
				launchConfig.scaleUIEnabled = true;
				continue;
			}

			Log.warn("Unrecognized command line argument: " + arg);
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
			Log.exception(e);
		}

		SplashController splashController = new SplashController();

		if (launchConfig.showSplash) {
			try {
				EventQueue.invokeAndWait(() -> new Splash(splashController).setVisible(true));
			} catch (InterruptedException | InvocationTargetException e) {
				Log.exception(e);
			}
		}

		EventQueue.invokeLater(() -> {
			try {
				new EditorFrame(splashController, launchConfig).setVisible(true);
			} catch (Exception e) {
				splashController.fatalExceptionOccurred = true;
				throw e;
			}
		});
	}

	/**
	 * Performs editor exit, if editor is still running, this will cause to display "Do you really want to exit?" dialog in editor window.
	 * If editor LibGDX thread died, for example after uncaught GdxRuntimeException this will simply kill app.
	 */
	private void performEditorExit () {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		for (Thread thread : threadSet) {
			if (thread.getName().contains("LWJGL Timer")) {
				editor.requestExit();
				return;
			}
		}

		Log.fatal("Editor LibGDX thread is not running, performing force exit.");
		Log.dispose();
		System.exit(-4);
	}

	@Override
	public void dispose () {
		super.dispose();
		editorCanvas.stop();
	}

	private static BufferedImage loadImage (String path) {
		try {
			return ImageIO.read(getResource(path));
		} catch (IOException e) {
			Log.exception(e);
		}

		throw new IllegalStateException("Failed to load image: " + path);
	}

	private static URL getResource (String path) {
		return EditorFrame.class.getResource(path);
	}

	private static class SplashController {
		boolean shouldClose = false;
		boolean fatalExceptionOccurred;
	}

	private static class Splash extends JWindow {
		public Splash (SplashController controller) {
			getContentPane().add(new JLabel(new ImageIcon(loadImage("/com/kotcrab/vis/editor/splash.png"))), BorderLayout.CENTER);
			pack();
			setLocationRelativeTo(null);

			new Thread(() -> {
				while (true) {
					if (controller.shouldClose) {
						dispose();
						break;
					}

					if (controller.fatalExceptionOccurred) {
						Log.fatal("Initialization error");
						JOptionPane.showMessageDialog(null, "An error occurred during editor initialization, please check log: " + Log.getLogStoragePath());
						System.exit(-5);
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						Log.exception(e);
					}
				}
			}, "Splash").start();
		}
	}
}
