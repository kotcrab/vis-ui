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

package com.kotcrab.vis.editor.module;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * UI widget for {@link ModuleContainer} that accepts user input such as {@link SceneModuleContainer}
 * @author Kotcrab
 */
public class ContentTable extends VisTable {
	private ModuleContainer container;
	private TableInputListener inputListener;

	public <T extends ModuleContainer & ModuleInput> ContentTable (T container) {
		super(false);
		this.container = container;
		this.inputListener = new TableInputListener(this, container);
		setTouchable(Touchable.enabled);
		addListener(inputListener);
	}

	public void focusContentTable() {
		inputListener.switchFocusToTarget();
	}

	@Override
	protected void sizeChanged () {
		super.sizeChanged();
		container.resize();
	}
}
