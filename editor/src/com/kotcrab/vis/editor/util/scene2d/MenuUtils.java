/*
 * Copyright 2014-2016 See AUTHORS file.
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

package com.kotcrab.vis.editor.util.scene2d;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.ui.ButtonListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

/**
 * {@link MenuItem}/menus related utils
 * @author Kotcrab
 */
public class MenuUtils {
	public static MenuItem createMenuItem (String text, ButtonListener listener) {
		return createMenuItem(text, (Drawable) null, listener);
	}

	public static MenuItem createMenuItem (String text, PopupMenu subMenu) {
		MenuItem item = new MenuItem(text);
		item.setSubMenu(subMenu);
		return item;
	}

	public static MenuItem createMenuItem (String text, Drawable icon) {
		return createMenuItem(text, icon, null);
	}

	public static MenuItem createMenuItem (String text, Drawable icon, ButtonListener listener) {
		if (listener == null) {
			return new MenuItem(text, icon);
		} else {
			return new MenuItem(text, icon, new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					listener.clicked();
				}
			});
		}
	}

	public static MenuItem createMenuItem (String text, Icons icon, ButtonListener listener) {
		return createMenuItem(text, icon == null ? null : icon.drawable(), listener);
	}
}
