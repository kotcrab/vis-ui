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

package com.kotcrab.vis.editor.module.scene.action;

import com.artemis.Component;
import com.artemis.Entity;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.scene.system.VisComponentManipulator;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableAction;

/** @author Kotcrab */
public class ComponentRemoveAction implements UndoableAction {
	private VisComponentManipulator componentManipulator;
	private final EntityProxy target;
	private final Class<? extends Component> componentClazz;

	private Component component;

	public ComponentRemoveAction (ModuleInjector injector, EntityProxy target, Class<? extends Component> componentClazz) {
		injector.injectModules(this);
		this.target = target;
		this.componentClazz = componentClazz;
	}

	@Override
	public void execute () {
		target.reload();

		Entity entity = target.getEntity();
		component = entity.getComponent(componentClazz);
		componentManipulator.modifyComposition(entity, component, false);
	}

	@Override
	public void undo () {
		target.reload();

		Entity entity = target.getEntity();
		componentManipulator.modifyComposition(entity, component, true);
	}

	@Override
	public String getActionName () {
		return "Remove Component";
	}
}
