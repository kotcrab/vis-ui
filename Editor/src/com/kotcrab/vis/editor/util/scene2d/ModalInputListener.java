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

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.editor.module.editor.InputModule;

/**
 * Used to get input events but with respecting window modality.
 * @author Kotcrab
 */
public abstract class ModalInputListener extends InputListener {
	private InputModule inputModule;

	@Override
	public boolean handle (Event e) {
		if (inputModule == null)
			throw new IllegalStateException("Tried to use ModalInputListener without adding it via InputModule!");

		if (inputModule.isAnyWindowModal())
			return false;
		else
			return super.handle(e);
	}

	/** Called by framework */
	public void setInputModule (InputModule inputModule) {
		this.inputModule = inputModule;
	}
}
