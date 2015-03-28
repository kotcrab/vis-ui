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

package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.dialog.ConfirmDialogListener;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestDialogUtils extends VisWindow {

	public TestDialogUtils () {
		super("dialogutils");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		VisTextButton showOKMsg = new VisTextButton("show ok popup");
		VisTextButton showErrorMsg = new VisTextButton("show error popup");
		VisTextButton showErrorDetailsMsg = new VisTextButton("show error with details popup");
		VisTextButton showInputDialog = new VisTextButton("show input dialog");
		VisTextButton showInputDialogIntOnly = new VisTextButton("show input dialog (int only)");
		VisTextButton showOptionDialog = new VisTextButton("show option dialog");
		VisTextButton showConfirmDialog = new VisTextButton("show dialog with custom buttons");

		VisTable firstRowTable = new VisTable(true);
		VisTable secondRowTable = new VisTable(true);
		VisTable thirdRowTable = new VisTable(true);

		firstRowTable.add(showOKMsg);
		firstRowTable.add(showErrorMsg);
		firstRowTable.add(showErrorDetailsMsg);

		secondRowTable.add(showInputDialog);
		secondRowTable.add(showInputDialogIntOnly);

		thirdRowTable.add(showOptionDialog);
		thirdRowTable.add(showConfirmDialog);

		add(firstRowTable).row();
		add(secondRowTable).row();
		add(thirdRowTable).row();
		padBottom(1);

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
				DialogUtils.showInputDialog(getStage(), "enter int number", null, Validators.INTEGERS, new InputDialogAdapter() {
					@Override
					public void finished (String input) {
						DialogUtils.showOKDialog(getStage(), "result", "you entered: " + input);
					}
				});
			}
		});

		showOptionDialog.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DialogUtils.showOptionDialog(getStage(), "option dialog", "do you want to do something?", OptionDialogType.YES_NO_CANCEL, new OptionDialogAdapter() {
					@Override
					public void yes () {
						DialogUtils.showOKDialog(getStage(), "result", "pressed: yes");
					}

					@Override
					public void no () {
						DialogUtils.showOKDialog(getStage(), "result", "pressed: no");
					}

					@Override
					public void cancel () {
						DialogUtils.showOKDialog(getStage(), "result", "pressed: cancel");
					}
				});
			}
		});

		showInputDialog.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				final int nothing = 1;
				final int everything = 2;
				final int something = 3;

				//confirmdialog may return result of any type, here we are just using ints
				DialogUtils.showConfirmDialog(getStage(), "confirm dialog", "what do you want?",
						new String[]{"nothing", "everything", "something"}, new Integer[]{nothing, everything, something},
						new ConfirmDialogListener<Integer>() {
							@Override
							public void result (Integer result) {
								if(result == nothing) DialogUtils.showOKDialog(getStage(), "result", "pressed: nothing");
								if(result == everything) DialogUtils.showOKDialog(getStage(), "result", "pressed: everything");
								if(result == something) DialogUtils.showOKDialog(getStage(), "result", "pressed: something");

							}
						});
			}
		});

		pack();
		setPosition(255, 20);
	}

}
