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

package com.kotcrab.vis.editor.module.scene.action;

import com.artemis.Component;
import com.kotcrab.vis.editor.module.scene.VisComponentManipulator;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.proxy.GroupEntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableAction;

/** @author Kotcrab */
public class ComponentAddAction implements UndoableAction {
	private VisComponentManipulator componentManipulator;
	private EntityProxy target;
	private Component component;

	public ComponentAddAction (VisComponentManipulator componentManipulator, EntityProxy target, Component component) {
		this.componentManipulator = componentManipulator;
		this.target = target;
		this.component = component;

		if (target instanceof GroupEntityProxy)
			throw new IllegalStateException("ComponentAddAcion does not support GroupEntityProxy as target");
	}

	@Override
	public void execute () {
		target.reload();
		componentManipulator.addJob(target.getEntities().get(0), component, true);
	}

	@Override
	public void undo () {
		target.reload();
		componentManipulator.addJob(target.getEntities().get(0), component, false);
	}
}
