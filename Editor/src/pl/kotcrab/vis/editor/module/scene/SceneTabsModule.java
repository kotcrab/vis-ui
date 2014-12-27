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

package pl.kotcrab.vis.editor.module.scene;

import java.util.ArrayList;

import pl.kotcrab.vis.editor.module.TabsModule;
import pl.kotcrab.vis.editor.module.project.ProjectModule;

public class SceneTabsModule extends ProjectModule {
	private TabsModule tabsModule;
	
	private ArrayList<SceneTab> loadedTabs;

	@Override
	public void init () {
		tabsModule = containter.get(TabsModule.class);
		loadedTabs = new ArrayList<SceneTab>();
	}
	
	public void open (EditorScene scene) {
		SceneTab tab = new SceneTab(scene);
		loadedTabs.add(tab);
		tabsModule.addTab(tab);
	}
}
