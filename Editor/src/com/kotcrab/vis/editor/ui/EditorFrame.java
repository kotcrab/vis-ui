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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.util.Log;
import com.kotcrab.vis.editor.util.ThreadUtils;

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

public class EditorFrame extends JFrame {
	private Editor editor;

	public EditorFrame (SplashController splashController) {
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

		LwjglCanvas editorCanvas = new LwjglCanvas(editor, config);
		Canvas canvas = editorCanvas.getCanvas();
		canvas.setSize(1280, 720);

		getContentPane().add(canvas, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(null);
		splashController.shouldClose = true;
	}

	public static void main (String[] args) {
		App.init();

		boolean showSplash = true;

		if (args.length == 1 && args[0].equals("--no-splash")) showSplash = false;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
			e.printStackTrace();
		}

		SplashController splashController = new SplashController();

		if (showSplash) {
			try {
				EventQueue.invokeAndWait(() -> new Splash(splashController).setVisible(true));
			} catch (InterruptedException | InvocationTargetException e) {
				Log.exception(e);
			}
		}

		EventQueue.invokeLater(() -> new EditorFrame(splashController).setVisible(true));
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
		System.exit(-1);
	}

	@Override
	public void dispose () {
		super.dispose();

		//make sure that application will exit eventually
		Thread exitThread = new Thread(() -> {
			ThreadUtils.sleep(1000);
			System.exit(-2);
		}, "Force Exit");

		exitThread.setDaemon(true);
		exitThread.start();

		Gdx.app.exit();
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
