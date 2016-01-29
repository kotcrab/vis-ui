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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.editor.QuickAccessModule;
import com.kotcrab.vis.editor.module.project.AssetsAnalyzerModule;
import com.kotcrab.vis.editor.module.project.AssetsUsages;
import com.kotcrab.vis.editor.module.project.SceneTabsModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * Used to display found usages of multiple files before delating them.
 * @author Kotcrab
 */
public class DeleteMultipleFilesTab extends Tab {
	private ModuleInjector injector;
	private final Array<FileItem> items;

	private AssetsAnalyzerModule usageAnalyzer;
	private SceneTabsModule tabsModule;
	private QuickAccessModule quickAccess;

	private VisTable table;
	private VisTable usagesTable;

	public DeleteMultipleFilesTab (ModuleInjector injector, Array<FileItem> items) {
		super(false, true);
		this.injector = injector;
		this.items = items;
		injector.injectModules(this);

		table = new VisTable();
		table.setBackground(VisUI.getSkin().getDrawable("window-bg"));
		table.defaults().left();

		usagesTable = new VisTable(true);
		rebuildUsagesTable();

		VisScrollPane scrollPane = new VisScrollPane(usagesTable);
		scrollPane.setFadeScrollBars(false);

		table.add(scrollPane).expandX().fillX().pad(3).row();
		table.add().expand().fill().row();
		table.addSeparator();
		table.add(createButtonPane()).pad(3);
	}

	private void rebuildUsagesTable () {
		usagesTable.clear();
		usagesTable.left();
		usagesTable.defaults().left();

		for (FileItem item : items) {

			FileHandle file = item.getFile();
			boolean canAnalyze = usageAnalyzer.canAnalyzeUsages(file);

			if (canAnalyze == false) {
				usagesTable.add(new VisLabel("Can't analyze usages for: '" + file.name() + "'"));
				usagesTable.row();
			} else {
				AssetsUsages usages = usageAnalyzer.analyzeUsages(file);
				usagesTable.add(new VisLabel(usages.toPrettyString()));

				if (usages.count() > 0) {
					VisTextButton viewUsages = new VisTextButton("View " + usages.file.name() + " Usages");
					usagesTable.add(viewUsages);

					viewUsages.addListener(new VisChangeListener((event, actor) -> openSpecificUsagesTab(usages)));
				}

				usagesTable.row();
			}
		}
	}

	private void openSpecificUsagesTab (AssetsUsages usages) {
		Tab tab = quickAccess.getAssetsUsagesTabForFile(usages.file);

		if (tab == null) {
			tab = new AssetsUsagesTab(injector, usages, false);
			quickAccess.addTab(tab);
		} else
			quickAccess.switchTab(tab);
	}

	private VisTable createButtonPane () {
		VisTable table = new VisTable(true);

		VisTextButton deleteAll = new VisTextButton("Delete All");
		VisTextButton reanalyze = new VisTextButton("Reanalyze");

		reanalyze.addListener(new VisChangeListener((event, actor) -> rebuildUsagesTable()));

		deleteAll.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showOptionDialog(event.getStage(), "Delete", "Are you sure?", OptionDialogType.YES_NO, new OptionDialogAdapter() {
					@Override
					public void yes () {
						for (FileItem item : items) {
							FileUtils.delete(item.getFile());
							quickAccess.closeAllUsagesTabForFile(item.getFile());
						}

						removeFromTabPane();
					}
				});
			}
		});

		table.add(deleteAll);
		table.add(reanalyze);

		return table;
	}

	@Override
	public String getTabTitle () {
		return "Delete Multiple Files";
	}

	@Override
	public Table getContentTable () {
		return table;
	}
}
