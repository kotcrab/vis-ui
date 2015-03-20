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

import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.ui.tab.ProjectInfoTab;

public class ProjectInfoTabModule extends ProjectModule implements EventListener {
	private TabsModule tabsModule;

	private ProjectInfoTab tab;

	@Override
	public void init () {
		tabsModule = container.get(TabsModule.class);
		tab = new ProjectInfoTab(project);
		tabsModule.addTab(tab);
	}

	@Override
	public void added () {
		App.eventBus.register(this);
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
		tabsModule.removeTab(tab);
	}

	@Override
	public boolean onEvent (Event e) {
		return false;
	}
}
