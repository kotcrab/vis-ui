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

package com.kotcrab.vis.editor.plugin.api.impl;

import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.Tool;
import com.kotcrab.vis.editor.plugin.api.ToolProvider;

/** @author Kotcrab */
public class ReflectionToolProvider<T extends Tool> implements ToolProvider<T> {
	private Class<T> toolClass;

	public ReflectionToolProvider (Class<T> toolClass) {
		this.toolClass = toolClass;
	}

	@Override
	public T createTool () {
		try {
			return toolClass.getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}
}
