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
