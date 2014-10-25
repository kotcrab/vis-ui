/**
 * Copyright 2014 Pawel Pastuszak
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

package pl.kotcrab.vis.editor;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

public class EditorFrame extends JFrame {
	public EditorFrame () {
		setSize(1280, 720);
		setLocation(100, 100);
		setTitle("Vis Editor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		LwjglAWTCanvas editorCanvas = new LwjglAWTCanvas(new Editor());
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
