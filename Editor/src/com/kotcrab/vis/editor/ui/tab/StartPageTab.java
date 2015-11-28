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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.editor.ProjectIOModule;
import com.kotcrab.vis.editor.module.editor.RecentProjectModule;
import com.kotcrab.vis.editor.module.editor.RecentProjectModule.RecentProjectEntry;
import com.kotcrab.vis.editor.module.editor.VisTwitterReader;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabViewMode;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.LinkLabel.LinkLabelListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Main tab that is displayed after editor is launched. Provides VisEditor twitter feed and recent projects list.
 * @author Kotcrab
 */
public class StartPageTab extends MainContentTab implements LinkLabelListener {
	private static final String NEW_PROJECT_LINK = "\\*NEW_PROJECT";
	private static final String LOAD_PROJECT_LINK = "\\*LOAD_PROJECT";

	private VisTwitterReader twitterReader;
	private RecentProjectModule recentProjectsModule;
	private ProjectIOModule projectIOModule;

	private Stage stage;

	public StartPageTab (ModuleInjector injector) {
		super(false, false);
		injector.injectModules(this);
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

		VisTable quickAccessTable = new VisTable(false);
		quickAccessTable.add("Start creating").row();
		quickAccessTable.add(newProjectLinkLabel).row();
		quickAccessTable.add(loadProjectLinkLabel).row();

		VisTable recentProjectsTable = new VisTable(false);
		recentProjectsTable.add("Recent projects").row();

		Array<RecentProjectEntry> recentProjects = recentProjectsModule.getRecentProjects();
		if (recentProjects.size == 0)
			recentProjectsTable.add(new VisLabel("No recently opened projects", Color.GRAY));

		for (RecentProjectEntry entry : recentProjects) {
			LinkLabel label = new LinkLabel(entry.name, entry.projectPath);
			label.setListener(this);
			recentProjectsTable.add(label).row();
		}

		VisTable leftSide = new VisTable(false);
		leftSide.add("Welcome to VisEditor").colspan(2).spaceBottom(8).row();
		leftSide.add(quickAccessTable).spaceRight(24);
		leftSide.add(recentProjectsTable).top();

		VisTable content = new VisTable(false);
		content.add(leftSide).expand();
		content.addSeparator(true);
		content.add(twitterReader.getTable()).fillY().expandY().pad(3).width(400).right();

		return content;
	}

	@Override
	public TabViewMode getViewMode () {
		return TabViewMode.TAB_ONLY;
	}

	@Override
	public void clicked (String url) {
		switch (url) {
			case NEW_PROJECT_LINK:
				Editor.instance.newProjectDialog();
				break;
			case LOAD_PROJECT_LINK:
				Editor.instance.loadProjectDialog();
				break;
			default:
				projectIOModule.loadHandleError(stage, Gdx.files.absolute(url));
				break;
		}
	}
}
