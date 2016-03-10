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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.editor.QuickAccessModule;
import com.kotcrab.vis.editor.module.project.AssetsAnalyzerModule;
import com.kotcrab.vis.editor.module.project.AssetsUsages;
import com.kotcrab.vis.editor.module.project.AssetsUsages.SceneUsages;
import com.kotcrab.vis.editor.module.project.SceneTabsModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.SceneTab;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * Displayed in a list assets usages after analyzing by {@link AssetsAnalyzerModule}
 * @author Kotcrab
 */
public class AssetsUsagesTab extends Tab {
	private AssetsAnalyzerModule usageAnalyzer;
	private SceneTabsModule sceneTabs;
	private QuickAccessModule quickAccess;

	private AssetsUsages usages;

	private VisTable mainTable;
	private VisTable usagesTable;
	private VisTree tree;
	private VisTable buttonTable;

	public AssetsUsagesTab (ModuleInjector injector, AssetsUsages usages, boolean showDeleteButton) {
		super(false, true);
		injector.injectModules(this);
		this.usages = usages;

		createButtonTable(showDeleteButton);

		mainTable = new VisTable();
		mainTable.setBackground("window-bg");
		mainTable.defaults().left();
		tree = new VisTree();

		usagesTable = new VisTable();

		VisScrollPane scrollPane = new VisScrollPane(usagesTable);
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

		tab.centerAround(label.getEntityId());
		tab.focusSelf();
	}

	private void rebuildUsagesTable () {
		usagesTable.clear();
		usagesTable.defaults().left().top();
		tree.clearChildren();

		usagesTable.add(new VisLabel(usages.toPrettyString(), "small"));

		usagesTable.row();
		usagesTable.add(tree).expand().fill();

		processUsages();
	}

	private void createButtonTable (boolean showDeleteButton) {
		VisTextButton deleteButton = new VisTextButton("Delete");
		VisTextButton reanalyzeButton = new VisTextButton("Reanalyze");

		buttonTable = new VisTable(true);
		if (showDeleteButton) buttonTable.add(deleteButton);
		buttonTable.add(reanalyzeButton);

		deleteButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showOptionDialog(event.getStage(), "Delete", "Are you sure?", OptionDialogType.YES_NO, new OptionDialogAdapter() {
					@Override
					public void yes () {
						FileUtils.delete(usages.file);
						quickAccess.closeAllUsagesTabForFile(usages.file);
					}
				});
			}
		});

		reanalyzeButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				usages = usageAnalyzer.analyzeUsages(usages.file);
				rebuildUsagesTable();
			}
		});
	}

	private void processUsages () {
		for (SceneUsages sceneUsages : usages.list) {
			Node node = new Node(new VisLabel(sceneUsages.scene.path, "small"));
			node.setExpanded(true);
			tree.add(node);

			for (int i = 0; i < sceneUsages.ids.size; i++) {
				int id = sceneUsages.ids.get(i);
				node.add(new Node(new UsageLabel(sceneUsages.scene, id)));
			}

		}
	}

	public FileHandle getUsageFile () {
		return usages.file;
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
		private final int id;

		public UsageLabel (EditorScene scene, int id) {
			super("Entity[" + id + "]", "small");
			this.scene = scene;
			this.id = id;
		}

		public EditorScene getScene () {
			return scene;
		}

		public int getEntityId () {
			return id;
		}
	}

}
