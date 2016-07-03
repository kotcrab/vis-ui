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
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.scene.system.VisComponentManipulator;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.runtime.util.ImmutableArray;

import java.lang.reflect.Constructor;

/** @author Kotcrab */
public class ComponentAddAction implements UndoableAction {
	private VisComponentManipulator componentManipulator;

	private Array<EntityProxy> proxies;

	private Array<Component> componentInstances;

	public ComponentAddAction (ModuleInjector injector, ImmutableArray<EntityProxy> proxies, Class<? extends Component> componentClass) throws ReflectiveOperationException {
		injector.injectModules(this);
		this.proxies = new Array<>();
		this.componentInstances = new Array<>();

		Constructor<? extends Component> compConstructor = componentClass.getDeclaredConstructor();
		compConstructor.setAccessible(true);

		for (EntityProxy proxy : proxies) {
			if (proxy.hasComponent(componentClass)) continue;

			this.proxies.add(proxy);
			this.componentInstances.add(compConstructor.newInstance());
		}
	}

	@Override
	public void execute () {
		modifyComponents(true);
	}

	@Override
	public void undo () {
		modifyComponents(false);
	}

	private void modifyComponents (boolean add) {
		for (int i = 0; i < proxies.size; i++) {
			EntityProxy proxy = proxies.get(i);
			Component component = componentInstances.get(i);

			proxy.reload();

			componentManipulator.modifyComposition(proxy.getEntity(), component, add);
		}
	}

	@Override
	public String getActionName () {
		return "Add Component";
	}
}
