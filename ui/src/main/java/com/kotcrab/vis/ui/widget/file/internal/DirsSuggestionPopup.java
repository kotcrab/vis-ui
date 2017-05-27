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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** @author Kotcrab */
public class DirsSuggestionPopup extends AbstractSuggestionPopup {
	private final VisTextField pathField;

	private ExecutorService listDirExecutor = Executors.newSingleThreadExecutor(new ServiceThreadFactory("FileChooserListDirThread"));
	private Future<?> listDirFuture;

	public DirsSuggestionPopup (FileChooser chooser, VisTextField pathField) {
		super(chooser);
		this.pathField = pathField;
	}

	public void pathFieldKeyTyped (Stage stage, float width) {
		if (pathField.getText().length() == 0) {
			remove();
			return;
		}
		createDirSuggestions(stage, width);
	}

	private void createDirSuggestions (final Stage stage, final float width) {
		final String pathFieldText = pathField.getText();
		//quiet period before listing files takes too long and popup will be removed
		addAction(Actions.sequence(Actions.delay(0.2f, Actions.removeActor())));

		if (listDirFuture != null) listDirFuture.cancel(true);
		listDirFuture = listDirExecutor.submit(new Runnable() {
			@Override
			public void run () {
				FileHandle enteredDir = Gdx.files.absolute(pathFieldText);
				final FileHandle listDir;
				final String partialPath;
				if (enteredDir.exists()) {
					listDir = enteredDir;
					partialPath = "";
				} else {
					listDir = enteredDir.parent();
					partialPath = enteredDir.name();
				}

				final FileHandle[] files = listDir.list(chooser.getFileFilter());
				if (Thread.currentThread().isInterrupted()) return;
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run () {
						clearChildren();
						clearActions();
						int suggestions = 0;

						for (final FileHandle file : files) {
							if (file.exists() == false || file.isDirectory() == false) continue;
							if (file.name().startsWith(partialPath) == false || file.name().equals(partialPath))
								continue;

							MenuItem item = createMenuItem(file.path());
							item.getLabel().setEllipsis(true);
							item.getLabelCell().width(width - 20);
							addItem(item);

							item.addListener(new ChangeListener() {
								@Override
								public void changed (ChangeEvent event, Actor actor) {
									chooser.setDirectory(file, FileChooser.HistoryPolicy.ADD);
								}
							});

							suggestions++;
							if (suggestions == MAX_SUGGESTIONS) {
								break;
							}
						}

						if (suggestions == 0) {
							remove();
							return;
						}

						showMenu(stage, pathField);
						setWidth(width);
						layout();
					}
				});
			}
		});
	}

	public void showRecentDirectories (Stage stage, Array<FileHandle> recentDirectories, float width) {
		int suggestions = createRecentDirSuggestions(recentDirectories, width);
		if (suggestions == 0) {
			remove();
			return;
		}
		showMenu(stage, pathField);
		setWidth(width);
		layout();
	}

	private int createRecentDirSuggestions (Array<FileHandle> files, float width) {
		clearChildren();
		int suggestions = 0;
		for (final FileHandle file : files) {
			if (file.exists() == false) continue;

			MenuItem item = createMenuItem(file.path());
			item.getLabel().setEllipsis(true);
			item.getLabelCell().width(width - 20);
			addItem(item);

			item.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					chooser.setDirectory(file, FileChooser.HistoryPolicy.ADD);
				}
			});

			suggestions++;
			if (suggestions == MAX_SUGGESTIONS) {
				break;
			}
		}

		return suggestions;
	}
}

