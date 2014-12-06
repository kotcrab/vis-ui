/**
 * Copyright 2014 Pawel Pastuszak
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

package pl.kotcrab.vis.editor.ui;

import pl.kotcrab.vis.editor.AsyncTask;
import pl.kotcrab.vis.editor.AsyncTaskListener;
import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.editor.util.DialogUtils;
import pl.kotcrab.vis.ui.TableUtils;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisProgressBar;
import pl.kotcrab.vis.ui.widget.VisTextButton;
import pl.kotcrab.vis.ui.widget.VisWindow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class AsyncTaskProgressDialog extends VisWindow {
	public AsyncTaskProgressDialog (String title, AsyncTask task) {
		super(title);
		setTitleAlignment(Align.center);
		setModal(true);

		TableUtils.setSpaceDefaults(this);

		final VisLabel statusLabel = new VisLabel("Please wait...");
		final VisProgressBar progressBar = new VisProgressBar(0, 100, 1, false);

		defaults().padLeft(6).padRight(6);

		add(statusLabel).padTop(6).left().row();
		add(progressBar).width(300).padTop(6).padBottom(6);

		task.setListener(new AsyncTaskListener() {
			@Override
			public void progressChanged (int newProgressPercent) {
				progressBar.setValue(newProgressPercent);
			}

			@Override
			public void messageChanged (String newMsg) {
				statusLabel.setText(newMsg);
			}

			@Override
			public void finished () {
				fadeOut();
			}

			@Override
			public void failed (String reason) {
				failed(reason, null);
			}

			@Override
			public void failed (String reason, Exception ex) {
				DialogUtils.showErrorDialog(Editor.instance.getStage(), reason, ex);
			}
		});

		task.start();

		pack();
		centerWindow();
	}

}
