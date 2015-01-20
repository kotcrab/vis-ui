/*
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.TabsModule;
import com.kotcrab.vis.editor.module.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.SceneTab;

public class SceneTabsModule extends ProjectModule {
	private TabsModule tabsModule;

	private Array<SceneTab> loadedTabs;

	@Override
	public void init () {
		tabsModule = container.get(TabsModule.class);
		loadedTabs = new Array<>();
	}

	@Override
	public void dispose () {
		for(SceneTab tab : loadedTabs)
			tab.dispose();
	}

	public void open (EditorScene scene) {
		SceneTab oldTab = getTabByScene(scene);

		if (oldTab == null) {
			SceneTab tab = new SceneTab(scene, projectContainer);
			loadedTabs.add(tab);
			tabsModule.addTab(tab);
		} else
			tabsModule.switchTab(oldTab);
	}

	private SceneTab getTabByScene (EditorScene scene) {
		for (SceneTab tab : loadedTabs)
			if (tab.getScene().path.equals(scene.path)) return tab;

		return null;
	}
}
