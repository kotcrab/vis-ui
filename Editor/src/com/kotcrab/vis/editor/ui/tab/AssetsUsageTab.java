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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.kotcrab.vis.editor.module.project.AssetsUsageAnalyzerModule;
import com.kotcrab.vis.editor.module.project.AssetsUsages;
import com.kotcrab.vis.editor.module.project.SceneTabsModule;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.SceneTab;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class AssetsUsageTab extends Tab {
	private AssetsUsageAnalyzerModule usageAnalyzer;
	private final SceneTabsModule sceneTabs;
	private AssetsUsages usages;

	private VisTable mainTable;
	private VisTable scrollPaneTable;
	private VisTree tree;
	private VisTable buttonTable;
	private VisScrollPane scrollPane;

	public AssetsUsageTab (AssetsUsageAnalyzerModule usageAnalyzer, SceneTabsModule sceneTabs, AssetsUsages usages) {
		super(false, true);
		this.usageAnalyzer = usageAnalyzer;
		this.sceneTabs = sceneTabs;
		this.usages = usages;

		createButtonTable();

		mainTable = new VisTable();
		mainTable.setBackground("window-bg");
		mainTable.defaults().left();
		tree = new VisTree();

		scrollPaneTable = new VisTable();

		scrollPane = new VisScrollPane(scrollPaneTable);
		scrollPane.setFadeScrollBars(false);

		mainTable.row();
		mainTable.add(scrollPane).expand().fill().row();
		mainTable.addSeparator();
		mainTable.add(buttonTable).pad(3);

		rebuildUsagesTable();

		tree.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				if (getTapCount() == 2) {
					Node node = tree.getNodeAt(y);
					if (node != null) {
						Actor actor = node.getActor();
						if (actor instanceof UsageLabel) selectEntityInScene((UsageLabel) actor);
					}
				}
			}
		});
	}

	private void selectEntityInScene (UsageLabel label) {
		SceneTab tab = sceneTabs.getTabByScene(label.getScene());
		if (tab == null) {
			sceneTabs.open(label.getScene());
			tab = sceneTabs.getTabByScene(label.getScene());
		}

		sceneTabs.switchTab(tab);

		tab.selectEntity(label.getEntity());
		tab.centerCamera(label.getEntity());
		tab.focusSelf();
	}

	private void rebuildUsagesTable () {
		scrollPaneTable.clear();
		scrollPaneTable.defaults().left().top();
		tree.clearChildren();

		if (usages.limitExceeded)
			scrollPaneTable.add(new VisLabel("More than " + AssetsUsageAnalyzerModule.USAGE_SEARCH_LIMIT + " usages found for " + usages.file.name(), "small"));
		else
			scrollPaneTable.add(new VisLabel("Found " + usages.count + " usages for " + usages.file.name(), "small"));

		scrollPaneTable.row();
		scrollPaneTable.add(tree).expand().fill();

		processUsages();
	}

	private void createButtonTable () {
		VisTextButton deleteButton = new VisTextButton("Delete");
		VisTextButton reanalyzeButton = new VisTextButton("Reanalyze");

		buttonTable = new VisTable(true);
		buttonTable.add(deleteButton);
		buttonTable.add(reanalyzeButton);

		deleteButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DialogUtils.showOptionDialog(event.getStage(), "Delete", "Are you sure?", OptionDialogType.YES_NO, new OptionDialogAdapter() {
					@Override
					public void yes () {
						FileUtils.delete(usages.file);
						removeFromTabPane();
					}
				});
			}
		});

		reanalyzeButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				usages = usageAnalyzer.analyze(usages.file);
				rebuildUsagesTable();
			}
		});
	}

	private void processUsages () {
		for (Entry<EditorScene, Array<EditorObject>> entry : usages.list) {
			Node node = new Node(new VisLabel(entry.key.path, "small"));
			node.setExpanded(true);
			tree.add(node);

			for (EditorObject entity : entry.value)
				node.add(new Node(new UsageLabel(entry.key, entity)));
		}
	}

	@Override
	public String getTabTitle () {
		return "Usages: " + usages.file.nameWithoutExtension();
	}

	@Override
	public Table getContentTable () {
		return mainTable;
	}

	private static class UsageLabel extends VisLabel {
		private final EditorScene scene;
		private final EditorObject entity;

		public UsageLabel (EditorScene scene, EditorObject entity) {
			super(entity.toPrettyString(), "small");
			this.scene = scene;
			this.entity = entity;
		}

		public EditorScene getScene () {
			return scene;
		}

		public EditorObject getEntity () {
			return entity;
		}
	}

}
