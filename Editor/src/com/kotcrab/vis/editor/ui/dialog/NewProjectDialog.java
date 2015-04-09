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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.module.editor.ProjectIOModule;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class NewProjectDialog extends VisWindow {
	private ProjectIOModule projectIO;

	private VisTextButton libGDXButton;
	private VisTextButton genericButton;
	private VisTextButton cancelButton;

	private NewProjectDialogLibGDX libGDXDialog;

	public NewProjectDialog (ProjectIOModule projectIO) {
		super("New Project");
		setModal(true);

		this.projectIO = projectIO;

		addCloseButton();
		closeOnEscape();

		TableUtils.setSpacingDefaults(this);
		defaults().left();
		add(new VisLabel("Select project type:"));
		row();

		VisTable table = new VisTable(true);
		table.add(genericButton = new VisTextButton("Generic"));
		table.add(libGDXButton = new VisTextButton("LibGDX"));
		add(table);
		row();

		add(cancelButton = new VisTextButton("Cancel")).right();

		libGDXDialog = new NewProjectDialogLibGDX(projectIO);

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fadeOut();
			}
		});

		genericButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DialogUtils.showOKDialog(getStage(), "Message", "Not implemented yet");
			}
		});

		libGDXButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().addActor(libGDXDialog.fadeIn());
				fadeOut();
			}
		});

		pack();
		centerWindow();
	}

}
