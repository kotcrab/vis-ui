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

package com.kotcrab.vis.editor.module;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.util.Log;

public class BaseModuleContainer<T extends BaseModule> {
	protected Array<T> modules = new Array<>();
	private boolean initFinished = false;

	public void add (T module) {
		modules.add(module);
		module.added();
		if (initFinished) module.init();
	}

	public void init () {
		if (initFinished) throw new IllegalStateException("ModuleContainer cannot be initialized twice!");

		long start = System.currentTimeMillis();

		for (int i = 0; i < modules.size; i++)
			modules.get(i).init();

		long end = System.currentTimeMillis();
		long delta = end - start;

		Log.debug(getClass().getSimpleName(), "Init took: " + delta + " ms");

		initFinished = true;
	}

	@SuppressWarnings("unchecked")
	public <C extends BaseModule> C get (Class<C> moduleClass) {
		for (int i = 0; i < modules.size; i++) {
			BaseModule m = modules.get(i);
			if (m.getClass() == moduleClass) return (C) m;
		}

		throw new IllegalStateException("Failed to get module: '" + moduleClass + "' from ModuleContainer, module not found!");
	}

	public Array<T> getModules () {
		return modules;
	}

	public void dispose () {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).dispose();

		modules.clear();
		initFinished = false;
	}

	public int getModuleCounter () {
		return modules.size;
	}

	public void resize () {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).resize();
	}
}
