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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.EditorModule;

/**
 * Allow to add InputListener that will send events from editor.
 * If some Window added to stage has focus then events won't be send
 */
public class InputModule extends EditorModule {
	private Array<InputListener> listeners = new Array<>();

	private Table table;

	public InputModule (Table table) {
		this.table = table;
	}

	public void reattachListeners () {
		for (InputListener listener : listeners) {
			table.addListener(listener);
		}
	}

	public void addListener (InputListener listener) {
		listeners.add(listener);
		table.addListener(listener);
	}

	public boolean removeListener (InputListener listener) {
		listeners.removeValue(listener, true);
		return table.removeListener(listener);
	}
}
