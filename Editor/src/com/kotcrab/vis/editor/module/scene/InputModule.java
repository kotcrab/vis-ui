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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.editor.EditorModule;

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
