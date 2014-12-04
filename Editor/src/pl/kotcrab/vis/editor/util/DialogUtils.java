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

package pl.kotcrab.vis.editor.util;

import pl.kotcrab.vis.ui.widget.VisDialog;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class DialogUtils {
	public static void showOKDialog (Stage stage, String title,  String text) {
		VisDialog dialog = new VisDialog(title);
		dialog.text(text);
		dialog.button("OK");
		dialog.pack();
		dialog.centerWindow();
		stage.addActor(dialog.fadeIn());
	}
}
