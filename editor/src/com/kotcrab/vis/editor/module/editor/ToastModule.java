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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.toast.Toast;

/**
 * @author Kotcrab
 * @see ToastManager
 */
public class ToastModule extends EditorModule {
	private Stage stage;

	private ToastManager manager;

	@Override
	public void init () {
		manager = new ToastManager(stage);
	}

	@Override
	public void resize () {
		manager.resize();
	}

	public void show (String text) {
		manager.show(text);
	}

	public void show (VisTable table, int timeSec) {
		manager.show(table, timeSec);
	}

	public void show (Toast toast) {
		manager.show(toast);
	}

	public void show (String text, int timeSec) {
		manager.show(text, timeSec);
	}

	public void show (Toast toast, int timeSec) {
		manager.show(toast, timeSec);
	}

	public boolean remove (Toast toast) {
		return manager.remove(toast);
	}

	public void show (VisTable table) {
		manager.show(table);
	}
}


