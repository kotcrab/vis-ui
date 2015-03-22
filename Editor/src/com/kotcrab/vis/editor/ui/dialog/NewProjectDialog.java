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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.module.editor.ProjectIOModule;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.util.DialogUtils;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
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

		TableUtils.setSpaceDefaults(this);
		defaults().left();
		add(new VisLabel("Select project type:"));
		row();

		VisTable table = new VisTable(true);
		table. add(genericButton = new VisTextButton("Generic"));
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
