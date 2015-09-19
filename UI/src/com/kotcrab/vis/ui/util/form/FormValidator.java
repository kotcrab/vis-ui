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

package com.kotcrab.vis.ui.util.form;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import java.io.File;

/**
 * Makes validating forms easier <br>
 * FromValidator is not gwt compatible, if you need that see {@link SimpleFormValidator}
 * @author Kotcrab
 */
public class FormValidator extends SimpleFormValidator {
	public FormValidator (Button buttonToDisable, Label errorMsgLabel) {
		super(buttonToDisable, errorMsgLabel);
	}

	public FormInputValidator fileExists (VisValidatableTextField field, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	public FormInputValidator fileExists (VisValidatableTextField field, VisTextField relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo, errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	/**
	 * @param errorIfRelativeEmpty if true field input will be valid if 'relativeTo' field is empty, usually used with notEmpty validator on 'relativeTo' to
	 * avoid form errors. Settings this to true improves UX, error are not displayed until user types something in 'relativeTo'
	 * field
	 */
	public FormInputValidator fileExists (VisValidatableTextField field, VisTextField relativeTo, String errorMsg, boolean errorIfRelativeEmpty) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo, errorMsg, false, errorIfRelativeEmpty);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	public FormInputValidator fileExists (VisValidatableTextField field, File relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo, errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	public FormInputValidator fileExists (VisValidatableTextField field, FileHandle relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo.file(), errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;

	}

	public FormInputValidator fileNotExists (VisValidatableTextField field, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(errorMsg, true);
		field.addValidator(validator);
		add(field);
		return validator;

	}

	public FormInputValidator fileNotExists (VisValidatableTextField field, VisTextField relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo, errorMsg, true);
		field.addValidator(validator);
		add(field);
		return validator;

	}

	public FormInputValidator fileNotExists (VisValidatableTextField field, File relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo, errorMsg, true);
		field.addValidator(validator);
		add(field);
		return validator;

	}

	public FormInputValidator fileNotExists (VisValidatableTextField field, FileHandle relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo.file(), errorMsg, true);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	public FormInputValidator directory (VisValidatableTextField field, String errorMsg) {
		DirectoryValidator validator = new DirectoryValidator(errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	public FormInputValidator directoryEmpty (VisValidatableTextField field, String errorMsg) {
		DirectoryContentValidator validator = new DirectoryContentValidator(errorMsg, true);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	public FormInputValidator directoryNotEmpty (VisValidatableTextField field, String errorMsg) {
		DirectoryContentValidator validator = new DirectoryContentValidator(errorMsg, false);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	public static class DirectoryValidator extends FormInputValidator {
		public DirectoryValidator (String errorMsg) {
			super(errorMsg);
		}

		@Override
		protected boolean validate (String input) {
			FileHandle file = Gdx.files.absolute(input);
			return file.exists() || file.isDirectory();
		}
	}

	public static class DirectoryContentValidator extends FormInputValidator {
		private final boolean mustBeEmpty;

		public DirectoryContentValidator (String errorMsg, boolean mustBeEmpty) {
			super(errorMsg);
			this.mustBeEmpty = mustBeEmpty;
		}

		@Override
		protected boolean validate (String input) {
			FileHandle file = Gdx.files.absolute(input);
			if (file.exists() == false || file.isDirectory() == false) return false;
			if (mustBeEmpty)
				return file.list().length == 0;
			else
				return file.list().length != 0;
		}
	}

	public static class FileExistsValidator extends FormInputValidator {
		VisTextField relativeTo;
		File relativeToFile;

		boolean existNot;
		boolean errorIfRelativeEmpty;

		public FileExistsValidator (String errorMsg) {
			this(errorMsg, false);
		}

		public FileExistsValidator (VisTextField relativeTo, String errorMsg) {
			this(relativeTo, errorMsg, false);
		}

		public FileExistsValidator (File relativeTo, String errorMsg) {
			this(relativeTo, errorMsg, false);
		}

		public FileExistsValidator (String errorMsg, boolean existNot) {
			super(errorMsg);
			this.existNot = existNot;
		}

		public FileExistsValidator (VisTextField relativeTo, String errorMsg, boolean existNot) {
			super(errorMsg);
			this.relativeTo = relativeTo;
			this.existNot = existNot;
		}

		public FileExistsValidator (File relativeTo, String errorMsg, boolean existNot) {
			super(errorMsg);
			this.relativeToFile = relativeTo;
			this.existNot = existNot;
		}

		public FileExistsValidator (VisTextField relativeTo, String errorMsg, boolean existNot, boolean errorIfRelativeEmpty) {
			super(errorMsg);
			this.relativeTo = relativeTo;
			this.existNot = existNot;
			this.errorIfRelativeEmpty = errorIfRelativeEmpty;
		}

		@Override
		public boolean validate (String input) {
			File file;

			if (relativeTo != null) {
				if (relativeTo.getText().length() == 0 && errorIfRelativeEmpty == false) {
					return true;
				}

				file = new File(relativeTo.getText(), input);
			} else if (relativeToFile != null)
				file = new File(relativeToFile, input);
			else
				file = new File(input);

			if (existNot)
				return !file.exists();
			else
				return file.exists();
		}
	}
}
