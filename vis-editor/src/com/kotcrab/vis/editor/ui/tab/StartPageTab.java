/*
 * Copyright 2014-2016 See AUTHORS file.
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
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.editor.ProjectIOModule;
import com.kotcrab.vis.editor.module.editor.RecentProjectModule;
import com.kotcrab.vis.editor.module.editor.RecentProjectModule.RecentProjectEntry;
import com.kotcrab.vis.editor.module.editor.StyleProviderModule;
import com.kotcrab.vis.editor.module.editor.VisTwitterReader;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabViewMode;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.LinkLabel.LinkLabelListener;

/**
 * Main tab that is displayed after editor is launched. Provides VisEditor twitter feed and recent projects list.
 * @author Kotcrab
 */
public class StartPageTab extends MainContentTab implements LinkLabelListener {
	private static final String NEW_PROJECT_LINK = "\\*NEW_PROJECT";
	private static final String LOAD_PROJECT_LINK = "\\*LOAD_PROJECT";
	private static final String CLEAR_RECENT_PROJECTS_LIST = "\\*CLEAR_RECENT_PROJECTS_LIST";

	private VisTwitterReader twitterReader;
	private StyleProviderModule styleProvider;
	private RecentProjectModule recentProjectsModule;
	private ProjectIOModule projectIO;

	private Stage stage;

	private VisTable recentProjectListTable;

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
		quickAccessTable.defaults().left();
		quickAccessTable.add("Start creating").row();
		quickAccessTable.add(newProjectLinkLabel).row();
		quickAccessTable.add(loadProjectLinkLabel).row();

		VisTable recentProjectsTable = new VisTable(false);
		recentProjectListTable = new VisTable(false);
		recentProjectsTable.add("Recent projects").row();
		recentProjectsTable.add(recentProjectListTable).growX();

		rebuildRecentProjectList();

		VisTable leftSide = new VisTable(false);
		leftSide.add("Welcome to VisEditor").colspan(2).spaceBottom(8).row();
		leftSide.add(quickAccessTable).spaceRight(36);
		leftSide.add(recentProjectsTable).top();

		VisTable content = new VisTable(false);
		content.add(leftSide).expand();
		content.addSeparator(true);
		content.add(twitterReader.getTable()).fillY().expandY().pad(3).width(400).right();

		return content;
	}

	public void rebuildRecentProjectList () {
		recentProjectListTable.clear();
		recentProjectListTable.defaults().left();

		Array<RecentProjectEntry> recentProjects = recentProjectsModule.getRecentProjects();
		if (recentProjects.size == 0) {
			recentProjectListTable.add(new VisLabel("No recently opened projects", Color.GRAY));
		}

		for (RecentProjectEntry entry : recentProjects) {
			LinkLabel label = new LinkLabel(entry.name, entry.projectPath);
			new Tooltip.Builder(entry.projectPath).target(label).build();
			label.setListener(this);

			VisImageButton removeButton = new VisImageButton(styleProvider.transparentXButton());
			new Tooltip.Builder("Remove from list").target(removeButton).build();
			removeButton.setFocusBorderEnabled(false);
			removeButton.setVisible(false);
			removeButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent changeEvent, Actor actor) {
					recentProjectsModule.remove(entry);
					rebuildRecentProjectList();
				}
			});

			VisTable tableRow = new VisTable(false);
			tableRow.setTouchable(Touchable.enabled);
			tableRow.addListener(new InputListener() { // handle enter/exit events for show remove button
				@Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					removeButton.setVisible(true);
				}

				@Override
				public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
					super.exit(event, x, y, pointer, toActor);
					removeButton.setVisible(false);
				}
			});

			tableRow.add(label).left();
			tableRow.add().growX();
			tableRow.add(removeButton).padLeft(8).right();
			recentProjectListTable.add(tableRow).growX().row();
		}

		if (recentProjects.size > 0) {
			LinkLabel clearListLink = new LinkLabel("Clear List", CLEAR_RECENT_PROJECTS_LIST);
			clearListLink.setListener(this);
			recentProjectListTable.add(clearListLink).padTop(8);
		}
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
			case CLEAR_RECENT_PROJECTS_LIST:
				recentProjectsModule.clear();
				rebuildRecentProjectList();
				break;
			default: // recent project label clicked
				projectIO.loadHandleError(stage, Gdx.files.absolute(url));
				break;
		}
	}
}
