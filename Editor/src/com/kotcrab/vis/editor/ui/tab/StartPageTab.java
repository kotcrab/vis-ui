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

package com.kotcrab.vis.editor.ui.tab;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabViewMode;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.LinkLabel.LinkLabelListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class StartPageTab extends MainContentTab implements LinkLabelListener {
	private static final String NEW_PROJECT_LINK = "NEW_PROJECT";
	private static final String LOAD_PROJECT_LINK = "LOAD_PROJECT";

	public StartPageTab () {
		super(false, false);
	}

	@Override
	public String getTabTitle () {
		return "Start Page";
	}

	@Override
	public Table getContentTable () {
		LinkLabel newProjectLinkLabel = new LinkLabel("New project", NEW_PROJECT_LINK);
		LinkLabel loadProjectLinkLabel = new LinkLabel("Load project", LOAD_PROJECT_LINK);

		newProjectLinkLabel.setListener(this);
		loadProjectLinkLabel.setListener(this);

		VisTable content = new VisTable(false);
		content.add("Ohayou!").row();
		content.add(new VisLabel("(here will be recent project list etc.)", "small")).spaceBottom(8).row();
		content.add("Start doing something!").row();
		content.add(newProjectLinkLabel).row();
		content.add(loadProjectLinkLabel).row();

		return content;
	}

	@Override
	public TabViewMode getViewMode () {
		return TabViewMode.TAB_ONLY;
	}

	@Override
	public void clicked (String url) {
		if (url.equals(NEW_PROJECT_LINK)) Editor.instance.newProjectDialog();
		if (url.equals(LOAD_PROJECT_LINK)) Editor.instance.loadProjectDialog();
	}
}
