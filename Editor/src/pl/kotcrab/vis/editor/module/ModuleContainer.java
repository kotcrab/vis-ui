/**
 * Copyright 2014 Pawel Pastuszak
 * 
 * This file is part of VisEditor.
 * 
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.kotcrab.vis.editor.module;

import com.badlogic.gdx.utils.Array;

public class ModuleContainer {
	private Array<Module> modules = new Array<Module>();
	private boolean initFinished = false;

	public void add (Module module) {
		modules.add(module);
		module.added();
		if (initFinished) module.init();
	}

	public void init () {
		if (initFinished) throw new IllegalStateException("ModuleManger cannot be initialized twice!");

		for (Module m : modules)
			m.init();

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
