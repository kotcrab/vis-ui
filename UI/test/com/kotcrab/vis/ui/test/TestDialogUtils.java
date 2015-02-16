/*
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.ui.test;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.InputDialogAdapter;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.util.DialogUtils;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestDialogUtils extends VisWindow {

	public TestDialogUtils () {
		super("dialogutils");

		TableUtils.setSpaceDefaults(this);
		columnDefaults(0).left();

		VisTextButton showOKMsg = new VisTextButton("show ok popup");
		VisTextButton showErrorMsg = new VisTextButton("show error popup");
		VisTextButton showErrorDetailsMsg = new VisTextButton("show error with details popup");
		VisTextButton showInputDialog = new VisTextButton("show input dialog");
		VisTextButton showInputDialogIntOnly = new VisTextButton("show input dialog (int only)");

		VisTable firstRowTable = new VisTable(true);
		VisTable secondRowTable = new VisTable(true);

		firstRowTable.add(showOKMsg);
		firstRowTable.add(showErrorMsg);
		firstRowTable.add(showErrorDetailsMsg).padBottom(1);

		secondRowTable.add(showInputDialog);
		secondRowTable.add(showInputDialogIntOnly);

		add(firstRowTable);
		row();
		add(secondRowTable);

		showOKMsg.addListener(new ChangeListener() {

			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DialogUtils.showOKDialog(getStage(), "VisUI demo", "Everything is OK!");
			}
		});

		showErrorMsg.addListener(new ChangeListener() {

			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DialogUtils.showErrorDialog(getStage(), "Error occurred while trying to show error popup");
			}
		});

		showErrorDetailsMsg.addListener(new ChangeListener() {

			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DialogUtils.showErrorDialog(getStage(), "Error occurred while trying to show error popup", new IllegalStateException(
						"Carrots cannot be casted to Potatoes"));
			}
		});

		showInputDialog.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DialogUtils.showInputDialog(getStage(), "enter your name", "name: ", new InputDialogAdapter() {
					@Override
					public void finished (String input) {
						DialogUtils.showOKDialog(getStage(), "result", "your name is: " + input);
					}
				});
			}
		});

		showInputDialogIntOnly.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DialogUtils.showInputDialog(getStage(), "enter int number", null, Validators.integers, new InputDialogAdapter() {
					@Override
					public void finished (String input) {
						DialogUtils.showOKDialog(getStage(), "result", "you entered: " + input);
					}
				});
			}
		});

		pack();
		setPosition(255, 20);
	}


}
