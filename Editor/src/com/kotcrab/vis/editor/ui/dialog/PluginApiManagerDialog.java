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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.editor.BuildToolsCheckerModule;
import com.kotcrab.vis.editor.module.editor.EditorModuleContainer;
import com.kotcrab.vis.editor.module.editor.PluginApiManagerModule;
import com.kotcrab.vis.editor.ui.ProgressImage;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.editor.util.scene2d.VisActions;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/** @author Kotcrab */
public class PluginApiManagerDialog extends BaseDialog {
	private BuildToolsCheckerModule buildToolsChecker;
	private PluginApiManagerModule apiInstaller;

	public PluginApiManagerDialog (EditorModuleContainer injector) {
		super("Plugin API Installer");
		injector.injectModules(this);
		init();
	}

	@Override
	protected void createUI () {
		defaults().left();

		VisTable mainTable = new VisTable(true);
		mainTable.add(".");

		VisTable checkingBuildToolsTable = TableBuilder.build(
				new ProgressImage(),
				new VisLabel("Checking build tools..."));

		add(mainTable).width(350).height(150).colspan(2).row();
		add(checkingBuildToolsTable);
		add(new VisTextButton("Close", new VisChangeListener((event, actor) -> fadeOut()))).right();

		buildToolsChecker.isToolsInstalled(missingTools -> {
			if (missingTools.isEmpty()) {
				checkingBuildToolsTable.clearChildren();
				TableBuilder.build(checkingBuildToolsTable,
						new Image(Icons.CHECK.drawable()),
						new VisLabel("Build tools are available"));

				checkingBuildToolsTable.addAction(Actions.sequence(Actions.delay(2), VisActions.fadeOutAndRemove()));
				return;
			}

			LinkLabel detailsLabel = new LinkLabel("Details");
			detailsLabel.setListener(url -> getStage().addActor(new MissingBuildToolsDialog(missingTools).fadeIn()));

			checkingBuildToolsTable.clearChildren();
			TableBuilder.build(checkingBuildToolsTable,
					new Image(Icons.WARNING.drawable()),
					new VisLabel("Some build tools are missing!"),
					detailsLabel);
		});
	}
}
