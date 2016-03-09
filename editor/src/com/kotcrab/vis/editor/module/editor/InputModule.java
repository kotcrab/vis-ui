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
import com.kotcrab.vis.editor.util.scene2d.ModalInputListener;
import com.kotcrab.vis.editor.util.scene2d.VisGroup;

/**
 * Allow to add ModalInputListener that will send events from editor.
 * If some other modal window will be added to stage, input listener won't receive inputs.
 * Useful for implementing keyboard shortcuts like Ctrl+Z etc.
 * @author Kotcrab
 * @see GlobalInputModule
 */
public class InputModule extends EditorModule {
	private Stage stage;
	private VisGroup stageRoot;

	public InputModule () {
	}

	@Override
	public void init () {
		stageRoot = (VisGroup) stage.getRoot();
	}

	public void addListener (ModalInputListener listener) {
		listener.setInputModule(this);
		stage.addListener(listener);
	}

	public boolean removeListener (ModalInputListener listener) {
		listener.setInputModule(null);
		return stage.removeListener(listener);
	}

	public boolean isAnyWindowModal () {
		return stageRoot.isAnyWindowModal();
	}
}
