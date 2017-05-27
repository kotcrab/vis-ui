/*
 * Copyright 2014-2017 See AUTHORS file.
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
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.DetailsDialog;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestDialogs extends VisWindow {

	public TestDialogs () {
		super("dialogs");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		VisTextButton showOKMsg = new VisTextButton("show ok popup");
		VisTextButton showErrorMsg = new VisTextButton("show error popup");
		VisTextButton showErrorDetailsMsg = new VisTextButton("show error with details popup");
		VisTextButton showInputDialog = new VisTextButton("show input dialog");
		VisTextButton showInputDialogIntOnly = new VisTextButton("show input dialog (int only)");
		VisTextButton showOptionDialog = new VisTextButton("show option dialog");
		VisTextButton showConfirmDialog = new VisTextButton("show dialog with custom buttons");
		VisTextButton showDetailsDialog = new VisTextButton("show details dialog");
		VisTextButton showDetailsExpandedDialog = new VisTextButton("show expanded details dialog");

		VisTable firstRowTable = new VisTable(true);
		VisTable secondRowTable = new VisTable(true);
		VisTable thirdRowTable = new VisTable(true);
		VisTable fourthRowTable = new VisTable(true);

		firstRowTable.add(showOKMsg);
		firstRowTable.add(showErrorMsg);
		firstRowTable.add(showErrorDetailsMsg);

		secondRowTable.add(showInputDialog);
		secondRowTable.add(showInputDialogIntOnly);

		thirdRowTable.add(showOptionDialog);
		thirdRowTable.add(showConfirmDialog);

		fourthRowTable.add(showDetailsDialog);
		fourthRowTable.add(showDetailsExpandedDialog);

		add(firstRowTable).row();
		add(secondRowTable).row();
		add(thirdRowTable).row();
		add(fourthRowTable).row();
		padBottom(3);

		showOKMsg.addListener(new ChangeListener() {

			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showOKDialog(getStage(), "VisUI demo", "Everything is OK!");
			}
		});

		showErrorMsg.addListener(new ChangeListener() {

			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showErrorDialog(getStage(), "Error occurred while trying to show error popup");
			}
		});

		showErrorDetailsMsg.addListener(new ChangeListener() {

			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showErrorDialog(getStage(), "Error occurred while trying to show error popup", new IllegalStateException(
						"Carrots cannot be casted to Potatoes"));
			}
		});

		showInputDialog.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showInputDialog(getStage(), "enter your name", "name: ", new InputDialogAdapter() {
					@Override
					public void finished (String input) {
						Dialogs.showOKDialog(getStage(), "result", "your name is: " + input);
					}
				});
			}
		});

		showInputDialogIntOnly.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showInputDialog(getStage(), "enter int number", null, Validators.INTEGERS, new InputDialogAdapter() {
					@Override
					public void finished (String input) {
						Dialogs.showOKDialog(getStage(), "result", "you entered: " + input);
					}
				});
			}
		});

		showOptionDialog.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showOptionDialog(getStage(), "option dialog", "do you want to do something?", OptionDialogType.YES_NO_CANCEL, new OptionDialogAdapter() {
					@Override
					public void yes () {
						Dialogs.showOKDialog(getStage(), "result", "pressed: yes");
					}

					@Override
					public void no () {
						Dialogs.showOKDialog(getStage(), "result", "pressed: no");
					}

					@Override
					public void cancel () {
						Dialogs.showOKDialog(getStage(), "result", "pressed: cancel");
					}
				});
			}
		});

		showConfirmDialog.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				final int nothing = 1;
				final int everything = 2;
				final int something = 3;

				//confirmdialog may return result of any type, here we are just using ints
				Dialogs.showConfirmDialog(getStage(), "confirm dialog", "what do you want?",
						new String[]{"nothing", "everything", "something"}, new Integer[]{nothing, everything, something},
						new ConfirmDialogListener<Integer>() {
							@Override
							public void result (Integer result) {
								if (result == nothing)
									Dialogs.showOKDialog(getStage(), "result", "pressed: nothing");
								if (result == everything)
									Dialogs.showOKDialog(getStage(), "result", "pressed: everything");
								if (result == something)
									Dialogs.showOKDialog(getStage(), "result", "pressed: something");

							}
						});
			}
		});

		final String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec pretium, justo sed " +
				"porttitor pretium, justo est porta risus, sed semper elit turpis in leo. Nam aliquam ac augue id congue. " +
				"Maecenas luctus, tortor et suscipit dictum, augue lectus semper erat, a gravida velit libero vel magna. " +
				"Nunc vitae fermentum nisl. Cras magna mauris, porttitor dignissim nunc vitae, viverra congue libero. " +
				"Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Duis et " +
				"suscipit neque, dictum vehicula leo. Sed eu ullamcorper justo, quis sagittis urna. Etiam porttitor ligula " +
				"a eros rutrum fringilla. Quisque quis ligula at neque aliquet malesuada a ac tellus. ";

		showDetailsDialog.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DetailsDialog dialog = Dialogs.showDetailsDialog(getStage(), "this is a short description text", "details dialog test", loremIpsum);
				dialog.setCopyDetailsButtonVisible(false);
				dialog.setWrapDetails(true);
			}
		});

		showDetailsExpandedDialog.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DetailsDialog dialog = Dialogs.showDetailsDialog(getStage(), "this is a short description text", "details dialog test", loremIpsum, true);
				dialog.setCopyDetailsButtonVisible(false);
				dialog.setWrapDetails(true);
			}
		});

		pack();
		setPosition(25, 106);
	}

}
