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

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.editor.EditorModuleContainer;
import com.kotcrab.vis.editor.util.vis.EditorRuntimeException;

import java.lang.reflect.Field;

/**
 * Base container class. Module container holds all loaded {@link Module}s and allows other modules to access them.
 * @param <T> type of module that this container will be used for
 * @author Kotcrab
 * @see EditorModuleContainer
 */
public abstract class ModuleContainer<T extends Module> implements ModuleInjector {
	protected Array<T> modules = new Array<>();
	private boolean initFinished = false;
	private static final String TAG = "ModuleContainer";

	private boolean logTrace = (Log.getLogLevel() == Log.TRACE);

	public void add (T module) {
		modules.add(module);
		module.added();
		if (initFinished) {
			injectModules(module);
			module.init();
			module.postInit();
		}
	}

	public <C extends Module> boolean remove (Class<C> moduleClass) {
		if (initFinished) throw new IllegalStateException("Modules cannot be removed after initialization!");
		C module = get(moduleClass);
		boolean result = modules.removeValue((T) module, true);
		if (result) module.removed();
		return result;
	}

	public void addAll (Array<T> modules) {
		for (T module : modules)
			add(module);
	}

	public void init () {
		if (initFinished) throw new IllegalStateException("ModuleContainer cannot be initialized twice!");

		long start = System.currentTimeMillis();
		long moduleInit = 0;

		injectAllModules();

		for (int i = 0; i < modules.size; i++) {
			Module module = modules.get(i);
			if (logTrace) {
				Log.trace(TAG, "Init: " + module.getClass().getSimpleName());
				moduleInit = System.currentTimeMillis();
			}

			module.init();

			if (logTrace) {
				Log.trace(TAG, "Module init took: " + (System.currentTimeMillis() - moduleInit) + " ms");
			}

			if (module.getClass().isAnnotationPresent(EventBusSubscriber.class)) {
				App.eventBus.register(module);
				Log.trace(TAG, "Registered " + module.getClass().getSimpleName() + " to EventBus");
			}
		}

		for (int i = 0; i < modules.size; i++) {
			Module module = modules.get(i);

			if (logTrace) {
				Log.trace(TAG, "Post init: " + module.getClass().getSimpleName());
				moduleInit = System.currentTimeMillis();
			}

			module.postInit();

			if (logTrace) {
				Log.trace(TAG, "Module post init took: " + (System.currentTimeMillis() - moduleInit) + " ms");
			}
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

	@Override
	public void injectModules (Object target) {
		try {
			for (Field field : getAllFields(target.getClass())) {
				if (field.isAnnotationPresent(SkipInject.class)) continue;

				injectField(target, field, field.getType());
			}
		} catch (EditorRuntimeException e) {
			throw new IllegalStateException("ModuleInjector failed for target: " + target.getClass() + ". See nested exception for error details.", e);
		} catch (ReflectiveOperationException e) {
			Log.exception(e);
		}
	}

	protected boolean injectField (Object target, Field field, Class<?> type) throws ReflectiveOperationException {
		if (Module.class.isAssignableFrom(type)) {
			field.setAccessible(true);
			field.set(target, findInHierarchy(type.asSubclass(Module.class)));
			return true;
		}

		if (Stage.class.isAssignableFrom(type)) {
			field.setAccessible(true);
			field.set(target, Editor.instance.getStage());
			return true;
		}

		return false;
	}

	public static Array<Field> getAllFields (Class<?> type) {
		Array<Field> fields = new Array<>();
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			fields.addAll(c.getDeclaredFields());
		}

		return fields;
	}

	public <C extends Module> C findInHierarchy (Class<C> moduleClass) {
		return get(moduleClass);
	}

	public <C extends Module> C get (Class<C> moduleClass) {
		C module = getOrNull(moduleClass);
		if (module != null) return module;

		throw new EditorRuntimeException("Failed to get module: '" + moduleClass + "' from ModuleContainer, module not found!");
	}

	@SuppressWarnings("unchecked")
	protected <C extends Module> C getOrNull (Class<C> moduleClass) {
		for (int i = 0; i < modules.size; i++) {
			Module m = modules.get(i);
			if (moduleClass.isAssignableFrom(m.getClass())) return (C) m;
		}

		return null;
	}

	public Array<T> getModules () {
		return modules;
	}

	public void dispose () {
		if (initFinished == false) {
			Log.warn("Attempt to dispose uninitialized module container " + getClass().getSimpleName());
			return;
		}
		for (int i = 0; i < modules.size; i++) {
			Module module = modules.get(i);
			module.dispose();

			if (module.getClass().isAnnotationPresent(EventBusSubscriber.class)) {
				try {
					App.eventBus.unregister(module);
				} catch (IllegalArgumentException e) {
					Log.error("Failed to unregister module " + module.getClass());
					Log.exception(e);
				}
			}
		}

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
