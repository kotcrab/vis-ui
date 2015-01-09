/*******************************************************************************
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
 ******************************************************************************/

package pl.kotcrab.vis.ui.util;

import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.widget.VisDialog;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisScrollPane;
import pl.kotcrab.vis.ui.widget.VisTextButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class DialogUtils {
	public static void showOKDialog (Stage stage, String title, String text) {
		VisDialog dialog = new VisDialog(title);
		dialog.text(text);
		dialog.button("OK");
		dialog.pack();
		dialog.centerWindow();
		stage.addActor(dialog.fadeIn());
	}

	public static void showErrorDialog (Stage stage, String text) {
		showErrorDialog(stage, text, (String)null);
	}

	public static void showErrorDialog (Stage stage, String text, Exception exception) {
		if (exception == null)
			showErrorDialog(stage, text, (String)null);
		else
			showErrorDialog(stage, text, getStackTrace(exception));
	}

	public static void showErrorDialog (Stage stage, String text, String details) {
		ErrorDialog dialog = new ErrorDialog(text, details);
		stage.addActor(dialog.fadeIn());
	}

	private static class ErrorDialog extends VisDialog {
		final int OK = 0;
		final int DETIALS = 1;

		private VisTable detailsTable = new VisTable(true);
		private Cell<?> detailsCell;

		public ErrorDialog (String text, String stackTrace) {
			super("Error");

			text(text);

			if (stackTrace != null) {
				final VisTextButton copyButton = new VisTextButton("Copy");
				final VisLabel errorLabel = new VisLabel(stackTrace);

				copyButton.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {
						Gdx.app.getClipboard().setContents((errorLabel.getText().toString()));
						copyButton.setText("Copied");
					}
				});

				detailsTable.add(new VisLabel("Details:")).left().expand().padTop(6);
				detailsTable.add(copyButton);
				detailsTable.row();

				VisTable errorTable = new VisTable();
				errorTable.add(errorLabel).top().expand().fillX();
				detailsTable.add(createScrollPane(errorTable)).colspan(2).width(600).height(300);

				getContentTable().row();
				detailsCell = getContentTable().add(detailsTable);
				detailsCell.setActor(null);
				button("Details", DETIALS);
			}

			button("OK", OK);
			pack();
			centerWindow();
		}

		@Override
		protected void result (Object object) {
			int result = (Integer)object;

			if (result == DETIALS) {
				detailsCell.setActor(detailsCell.hasActor() ? null : detailsTable);
				pack();
				centerWindow();
				cancel();
			}
		}
	}

	private static VisScrollPane createScrollPane (Actor widget) {
		VisScrollPane scrollPane = new VisScrollPane(widget);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		return scrollPane;
	}

	private static String getStackTrace (Throwable throwable) {
		StringBuilder builder = new StringBuilder();

		for (StackTraceElement element : throwable.getStackTrace()) {
			builder.append(element);
			builder.append("\n");
		}
		
		return builder.toString();
	}
}
