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

package com.kotcrab.vis.editor.ui.scene.entityproperties.components;

import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.ui.scene.entityproperties.NumberInputField;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.AutoComponentTable;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.runtime.component.Renderable;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

/** @author Kotcrab */
public class RenderableComponentTable extends AutoComponentTable<Renderable> {
	private RenderBatchingSystem batchingSystem;

	private NumberInputField zIndexField;

	public RenderableComponentTable (ModuleInjector sceneMC) {
		super(sceneMC, Renderable.class, false);
	}

	@Override
	protected void init () {
		super.init();
		zIndexField = getUIByFieldId("zIndex", NumberInputField.class);
		zIndexField.addListener(new VisChangeListener((event, actor) -> {
			if (zIndexField.isInputValid()) {
				batchingSystem.markDirty();
			}
		}));
	}
}
