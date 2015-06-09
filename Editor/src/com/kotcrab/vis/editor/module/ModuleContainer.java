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

import java.lang.reflect.Field;

public abstract class ModuleContainer<T extends Module> implements ModuleInjector {
	protected Array<T> modules = new Array<>();
	private boolean initFinished = false;

	public void add (T module) {
		modules.add(module);
		module.added();
		if (initFinished) {
			injectModules(module);
			module.init();
			module.postInit();
		}
	}

	public void addAll (Array<T> modules) {
		for (T module : modules)
			add(module);
	}

	public void init () {
		if (initFinished) throw new IllegalStateException("ModuleContainer cannot be initialized twice!");

		long start = System.currentTimeMillis();

		injectAllModules();

		for (int i = 0; i < modules.size; i++) {
			Module m = modules.get(i);
			Log.trace("ModuleContainer", "Init: " + m.getClass().getSimpleName());
			m.init();
		}

		for (int i = 0; i < modules.size; i++) {
			Module m = modules.get(i);
			Log.trace("ModuleContainer", "Post init: " + m.getClass().getSimpleName());
			m.postInit();
		}

		long end = System.currentTimeMillis();
		long delta = end - start;

		Log.debug(getClass().getSimpleName(), "Init took: " + delta + " ms");

		initFinished = true;
	}

	private void injectAllModules () {
		for (T module : modules)
			injectModules(module);
	}

	public void injectModules (Object module) {
		try {
			for (Field field : module.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(InjectModule.class)) {
					field.setAccessible(true);
					field.set(module, findInHierarchy(field.getType().asSubclass(Module.class)));
				}
			}
		} catch (ReflectiveOperationException e) {
			Log.exception(e);
		}
	}

	public <C extends Module> C findInHierarchy (Class<C> moduleClass) {
		return get(moduleClass);
	}

	public <C extends Module> C get (Class<C> moduleClass) {
		C module = getOrNull(moduleClass);
		if (module != null) return module;

		throw new IllegalStateException("Failed to get module: '" + moduleClass + "' from ModuleContainer, module not found!");
	}

	@SuppressWarnings("unchecked")
	protected <C extends Module> C getOrNull (Class<C> moduleClass) {
		for (int i = 0; i < modules.size; i++) {
			Module m = modules.get(i);
			if (m.getClass() == moduleClass) return (C) m;
		}

		return null;
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
