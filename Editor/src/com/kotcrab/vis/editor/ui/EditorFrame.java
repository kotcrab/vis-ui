/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.util.Log;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class EditorFrame extends JFrame {
	private Editor editor;

	public EditorFrame () {
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
	}

	public static void main (String[] args) {
		App.init();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(() -> new EditorFrame().setVisible(true));
	}

	/**
	 * Performs editor exit, if editor is still running, this will cause to display "Do you really want to exit?" dialog in editor window.
	 * If editor LibGDX thread died, for example. after uncaught GdxRuntimeException this will simply kill app.
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
		Gdx.app.exit();
	}

	private BufferedImage loadImage (String path) {
		try {
			return ImageIO.read(getResource(path));
		} catch (IOException e) {
			Log.exception(e);
		}

		return null;
	}

	public URL getResource (String path) {
		return EditorFrame.class.getResource(path);
	}
}
