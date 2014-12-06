/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.editor.module;

import com.badlogic.gdx.utils.Array;

public class ModuleContainer {
	private Array<Module> modules = new Array<Module>();
	private boolean initFinished = false;

	public void add (Module module) {
		modules.add(module);
		module.added();
		if (initFinished) module.afterInit();
	}

	public void init () {
		if (initFinished) throw new IllegalStateException("ModuleManger cannot be initialized twice!");

		for (Module m : modules)
			m.afterInit();

		initFinished = true;
	}

	public Module get (Class<? extends Module> moduleClazz) {
		for (Module m : modules)
			if (m.getClass() == moduleClazz) return m;

		return null;
	}

	public void dispose () {
		for (Module m : modules)
			m.dispose();

		modules.clear();
	}

}
