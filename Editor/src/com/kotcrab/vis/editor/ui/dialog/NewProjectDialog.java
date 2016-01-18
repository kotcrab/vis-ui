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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.module.editor.FileChooserModule;
import com.kotcrab.vis.editor.module.editor.ProjectIOModule;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

/**
 * New project dialog, supports multiple projects types
 * @author Kotcrab
 */
public class NewProjectDialog extends VisWindow {
	private static final String LIBGDX = "LibGDX";
	private static final String GENERIC = "Generic";

	private Table containerTable;

	private NewProjectDialogLibGDX libgdxDialog;
	private NewProjectDialogGeneric genericDialog;

	public NewProjectDialog (FileChooserModule fileChooserModule, ProjectIOModule projectIO) {
		super("New Project");

		TableUtils.setSpacingDefaults(this);
		setModal(true);
		addCloseButton();
		closeOnEscape();

		containerTable = new Table();

		VisList<String> projectTypeList = new VisList<>();
		projectTypeList.setItems(LIBGDX, GENERIC);

		libgdxDialog = new NewProjectDialogLibGDX(this, fileChooserModule, projectIO);
		genericDialog = new NewProjectDialogGeneric(this, fileChooserModule, projectIO);

		containerTable.top();
		containerTable.add(libgdxDialog).fillX().expandX();

		VisTable projectTypeTable = new VisTable();
		projectTypeTable.defaults().left();
		projectTypeTable.add(new VisLabel("Project Type")).row();
		projectTypeTable.addSeparator();
		projectTypeTable.add(projectTypeList).expandX().fillX();

		add(projectTypeTable).top().minWidth(150);
		add(new Separator()).fillY().expandY().padTop(2).padBottom(2);
		add(containerTable).expand().fill().minWidth(300);

		projectTypeList.addListener(new VisChangeListener((event, actor) -> {
			if (projectTypeList.getSelected().equals(LIBGDX)) {
				containerTable.clear();
				containerTable.top();
				containerTable.add(libgdxDialog).fillX().expandX();
			}

			if (projectTypeList.getSelected().equals(GENERIC)) {
				containerTable.clear();
				containerTable.top();
				containerTable.add(genericDialog).fillX().expandX();
			}
		}));

		pack();
		centerWindow();
	}
}
