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
		module.setContainter(this);
		modules.add(module);
		module.added();
		if (initFinished) module.init();
	}

	public void init () {
		if (initFinished) throw new IllegalStateException("ModuleContainer cannot be initialized twice!");

		for (int i = 0; i < modules.size; i++)
			modules.get(i).init();

		initFinished = true;
	}

	@SuppressWarnings("unchecked")
	public <T> T get (Class<T> moduleClass) {
		for (int i = 0; i < modules.size; i++) {
			Module m = modules.get(i);
			if (m.getClass() == moduleClass) return (T)m;
		}

		return null;
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
