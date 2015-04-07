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

package com.kotcrab.vis.editor.util.gdx;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.ui.ButtonListener;
import com.kotcrab.vis.ui.widget.MenuItem;

public class MenuUtils {
	public static MenuItem createMenuItem (String text, ButtonListener listener) {
		return createMenuItem(text, null, listener);
	}

	public static MenuItem createMenuItem (String text, Icons icon, ButtonListener listener) {
		return new MenuItem(text, icon != null ? Assets.getIcon(icon) : null, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				listener.clicked();
			}
		});
	}

}
