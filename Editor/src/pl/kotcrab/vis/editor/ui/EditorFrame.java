/**
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

package pl.kotcrab.vis.editor.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import pl.kotcrab.vis.editor.App;
import pl.kotcrab.vis.editor.Editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;

public class EditorFrame extends JFrame {
	public static void main (String[] args) {
		App.init();

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run () {
				new EditorFrame().setVisible(true);
			}
		});
	}

	public EditorFrame () {
		setSize(1280, 720);
		setLocationRelativeTo(null);
		setTitle("Vis Editor");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.backgroundFPS = -1;

		Editor editor = new Editor(this);
		LwjglCanvas editorCanvas = new LwjglCanvas(editor, config);
		editorCanvas.getCanvas().setSize(1280, 720);

		getContentPane().add(editorCanvas.getCanvas(), BorderLayout.CENTER);

		pack();
		setVisible(true);
	}

	@Override
	public void dispose () {
		super.dispose();
		Gdx.app.exit();
	}
}
