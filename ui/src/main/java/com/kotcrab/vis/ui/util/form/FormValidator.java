/*
 * Copyright 2014-2016 See AUTHORS file.
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import java.io.File;

/**
 * Utility class made for creating input forms that requires inputting various information and that information cannot be wrong.
 * For example user registration form.
 * <p></p>
 * FromValidator is not GWT compatible, if you need that see {@link SimpleFormValidator}.
 * @author Kotcrab
 */
public class FormValidator extends SimpleFormValidator {
	/** @see SimpleFormValidator#SimpleFormValidator(Disableable) */
	public FormValidator (Disableable targetToDisable) {
		super(targetToDisable);
	}

	/** @see SimpleFormValidator#SimpleFormValidator(Disableable, Label) */
	public FormValidator (Disableable targetToDisable, Label messageLabel) {
		super(targetToDisable, messageLabel);
	}

	/** @see SimpleFormValidator#SimpleFormValidator(Disableable, Label, String) */
	public FormValidator (Disableable targetToDisable, Label messageLabel, String styleName) {
		super(targetToDisable, messageLabel, styleName);
	}

	/** @see SimpleFormValidator#SimpleFormValidator(Disableable, Label, FormValidatorStyle) */
	public FormValidator (Disableable targetToDisable, Label messageLabel, FormValidatorStyle style) {
		super(targetToDisable, messageLabel, style);
	}

	/** Validates if absolute path entered in text field points to an existing file. */
	public FormInputValidator fileExists (VisValidatableTextField field, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	/**
	 * Validates if relative path entered in text field points to an existing file.
	 * @param relativeTo path entered in this field is used to create absolute path from entered in field (see {@link FileExistsValidator}).
	 */
	public FormInputValidator fileExists (VisValidatableTextField field, VisTextField relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo, errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	/**
	 * Validates if relative path entered in text field points to an existing file.
	 * @param relativeTo path entered in this field is used to create absolute path from entered in field (see {@link FileExistsValidator}).
	 * @param errorIfRelativeEmpty if true field input will be valid if 'relativeTo' field is empty, usually used with notEmpty validator on 'relativeTo' field to
	 * avoid form errors. Settings this to true improves UX, errors are not displayed until user types something in 'relativeTo' field.
	 */
	public FormInputValidator fileExists (VisValidatableTextField field, VisTextField relativeTo, String errorMsg, boolean errorIfRelativeEmpty) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo, errorMsg, false, errorIfRelativeEmpty);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	/**
	 * Validates if relative path entered in text field points to an existing file.
	 * @param relativeTo path of this file is used to create absolute path from entered in field (see {@link FileExistsValidator}).
	 */
	public FormInputValidator fileExists (VisValidatableTextField field, File relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo, errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	/**
	 * Validates if relative path entered in text field points to an existing file.
	 * @param relativeTo path of this file is used to create absolute path from entered in field (see {@link FileExistsValidator}).
	 */
	public FormInputValidator fileExists (VisValidatableTextField field, FileHandle relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo.file(), errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;

	}

	/** Validates if relative path entered in text field points to an non existing file. */
	public FormInputValidator fileNotExists (VisValidatableTextField field, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(errorMsg, true);
		field.addValidator(validator);
		add(field);
		return validator;

	}

	/**
	 * Validates if relative path entered in text field points to an non existing file.
	 * @param relativeTo path entered in this field is used to create absolute path from entered in field (see {@link FileExistsValidator}).
	 */
	public FormInputValidator fileNotExists (VisValidatableTextField field, VisTextField relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo, errorMsg, true);
		field.addValidator(validator);
		add(field);
		return validator;

	}

	/**
	 * Validates if relative path entered in text field points to an non existing file.
	 * @param relativeTo path of this file is used to create absolute path from entered in field (see {@link FileExistsValidator}).
	 */
	public FormInputValidator fileNotExists (VisValidatableTextField field, File relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo, errorMsg, true);
		field.addValidator(validator);
		add(field);
		return validator;

	}

	/**
	 * Validates if relative path entered in text field points to an non existing file.
	 * @param relativeTo path of this file is used to create absolute path from entered in field (see {@link FileExistsValidator}).
	 */
	public FormInputValidator fileNotExists (VisValidatableTextField field, FileHandle relativeTo, String errorMsg) {
		FileExistsValidator validator = new FileExistsValidator(relativeTo.file(), errorMsg, true);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	/** Validates if relative path entered in text field points to an existing directory. */
	public FormInputValidator directory (VisValidatableTextField field, String errorMsg) {
		DirectoryValidator validator = new DirectoryValidator(errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	/** Validates if relative path entered in text field points to an existing and empty directory. */
	public FormInputValidator directoryEmpty (VisValidatableTextField field, String errorMsg) {
		DirectoryContentValidator validator = new DirectoryContentValidator(errorMsg, true);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	/** Validates if relative path entered in text field points to an existing and non empty directory. */
	public FormInputValidator directoryNotEmpty (VisValidatableTextField field, String errorMsg) {
		DirectoryContentValidator validator = new DirectoryContentValidator(errorMsg, false);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	/** Validates if entered absolute path points to existing directory. */
	public static class DirectoryValidator extends FormInputValidator {
		public DirectoryValidator (String errorMsg) {
			super(errorMsg);
		}

		@Override
		protected boolean validate (String input) {
			FileHandle file = Gdx.files.absolute(input);
			return file.exists() && file.isDirectory();
		}
	}

	/**
	 * Validates if entered path (absolute) points to an existing directory. Then checks if this directory is empty or if
	 * it has files in it.
	 * @see DirectoryValidator
	 */
	public static class DirectoryContentValidator extends FormInputValidator {
		private boolean mustBeEmpty;

		/** @param mustBeEmpty if true validated directory must be empty, if false that directory must not be empty. */
		public DirectoryContentValidator (String errorMsg, boolean mustBeEmpty) {
			super(errorMsg);
			this.mustBeEmpty = mustBeEmpty;
		}

		@Override
		protected boolean validate (String input) {
			FileHandle file = Gdx.files.absolute(input);
			if (file.exists() == false || file.isDirectory() == false) return false;
			if (mustBeEmpty) {
				return file.list().length == 0;
			} else {
				return file.list().length != 0;
			}
		}

		public void setMustBeEmpty (boolean mustBeEmpty) {
			this.mustBeEmpty = mustBeEmpty;
		}

		public boolean isMustBeEmpty () {
			return mustBeEmpty;
		}
	}

	/**
	 * Validates if entered path points to an existing or non existing file.
	 * <p>
	 * Additionally you can specify relativePath that entered path will be checked against. Relative path can be
	 * either supplied as File or some other VisTextField. In that case path entered in that relative text field is used to check
	 * if file exist in that directory. Eg. if relativePath points to "C:\directory\" and field that
	 * has this validator contains "test.txt" then this validator will check if file ""C:\directory\text.txt" exists (or not).
	 */
	public static class FileExistsValidator extends FormInputValidator {
		VisTextField relativeTo;
		File relativeToFile;

		boolean mustNotExist;
		boolean errorIfRelativeEmpty;

		public FileExistsValidator (String errorMsg) {
			this(errorMsg, false);
		}

		public FileExistsValidator (String errorMsg, boolean mustNotExist) {
			super(errorMsg);
			this.mustNotExist = mustNotExist;
		}

		public FileExistsValidator (File relativeTo, String errorMsg) {
			this(relativeTo, errorMsg, false);
		}

		public FileExistsValidator (File relativeTo, String errorMsg, boolean mustNotExist) {
			super(errorMsg);
			this.relativeToFile = relativeTo;
			this.mustNotExist = mustNotExist;
		}

		public FileExistsValidator (VisTextField relativeTo, String errorMsg) {
			this(relativeTo, errorMsg, false);
		}

		public FileExistsValidator (VisTextField relativeTo, String errorMsg, boolean mustNotExist) {
			super(errorMsg);
			this.relativeTo = relativeTo;
			this.mustNotExist = mustNotExist;
		}

		/** @see FormValidator#fileExists(VisValidatableTextField, VisTextField, String, boolean) */
		public FileExistsValidator (VisTextField relativeTo, String errorMsg, boolean mustNotExist, boolean errorIfRelativeEmpty) {
			super(errorMsg);
			this.relativeTo = relativeTo;
			this.mustNotExist = mustNotExist;
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
			} else if (relativeToFile != null) {
				file = new File(relativeToFile, input);
			} else {
				file = new File(input);
			}

			if (mustNotExist)
				return !file.exists();
			else
				return file.exists();
		}

		public void setRelativeToFile (File relativeToFile) {
			if (relativeTo != null)
				throw new IllegalStateException("This validator already has relativeToTextField set");

			this.relativeToFile = relativeToFile;
		}

		public void setRelativeToTextField (VisTextField relativeTo) {
			if (relativeToFile != null)
				throw new IllegalStateException("This validator already has relativeToFile set.");

			this.relativeTo = relativeTo;
		}

		public void setMustNotExist (boolean notExist) {
			this.mustNotExist = notExist;
		}

		public void setErrorIfRelativeEmpty (boolean errorIfRelativeEmpty) {
			this.errorIfRelativeEmpty = errorIfRelativeEmpty;
		}
	}
}
