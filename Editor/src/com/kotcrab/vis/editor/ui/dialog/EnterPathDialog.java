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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.editor.ui.WindowResultListener;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.editor.util.gdx.VisChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.form.FormInputValidator;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

/** TODO: Temp dialog until we have some nice way to allow user select target directory from fixed parent. */
public class EnterPathDialog extends VisWindow {

	private final WindowResultListener<EnterPathDialogResult> listener;

	public EnterPathDialog (FileHandle absoluteParent, String parent, String parentRelativePath, WindowResultListener<EnterPathDialogResult> listener) {
		super("Enter new path");
		this.listener = listener;

		if (parent.endsWith("/") == false) parent += "/";

		setModal(true);
		addCloseButton();
		closeOnEscape();
		centerWindow();
		TableUtils.setSpacingDefaults(this);

		VisLabel errorLabel = new VisLabel(" ");
		errorLabel.setColor(Color.RED);
		VisValidableTextField fieldPath = new VisValidableTextField(parentRelativePath);

		VisTextButton refactorButton = new VisTextButton("Refactor");

		FormValidator validator = new FormValidator(refactorButton, errorLabel);
		validator.notEmpty(fieldPath, "Path is empty");
		validator.fileNotExists(fieldPath, absoluteParent, "This file already exist");
		validator.custom(fieldPath, new FormInputValidator("The path is unchanged") {
			@Override
			protected boolean validate (String input) {
				return input.equals(parentRelativePath) == false;
			}
		});

		add(TableBuilder.build(new VisLabel(parent), fieldPath)).colspan(2).row();
		add(errorLabel).expand().fill();
		add(refactorButton);

		final String finalParent = parent;
		refactorButton.addListener(new VisChangeListener((event, actor) -> {
			listener.finished(new EnterPathDialogResult(finalParent + fieldPath.getText()));
			fadeOut();
		}));

		pack();
	}

	@Override
	protected void close () {
		super.close();
		listener.canceled();
	}

	public static class EnterPathDialogResult {
		public String relativePath;

		public EnterPathDialogResult (String relativePath) {
			this.relativePath = relativePath;
		}
	}
}
