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

package pl.kotcrab.vis.editor.ui;

import pl.kotcrab.vis.editor.module.scene.EditorScene;
import pl.kotcrab.vis.editor.ui.tab.TabAdapater;
import pl.kotcrab.vis.editor.ui.tab.TabViewMode;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.widget.VisLabel;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class SceneTab extends TabAdapater {
	private EditorScene scene;

	private Table content;

	public SceneTab (EditorScene scene) {
		this.scene = scene;

		content = new VisTable(true);

		content.add(new VisLabel("Some scene info:"));
		content.row();
		content.add(new VisLabel("File name: " + scene.getFile().name()));
	}

	@Override
	public String getButtonText () {
		return scene.getFile().name();
	}

	@Override
	public Table getContentTable () {
		return content;
	}

	@Override
	public TabViewMode getViewMode () {
		return TabViewMode.WITH_PROJECT_ASSETS_MANAGER;
	}
}
