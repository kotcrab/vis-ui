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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

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

	public void fileExists (VisValidableTextField field, String errorMsg) {
		field.addValidator(new FileExistsValidator(errorMsg));
		add(field);
	}

	public void fileExists (VisValidableTextField field, VisTextField relativeTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relativeTo, errorMsg));
		add(field);
	}

	/**
	 * @param errorIfRelativeEmpty if true field input will be valid if 'relativeTo' field is empty, usually used with notEmpty validator on 'relativeTo' to
	 * avoid form errors. Settings this to true improves UX, error are not displayed until user types something in 'relativeTo'
	 * field
	 */
	public void fileExists (VisValidableTextField field, VisTextField relativeTo, String errorMsg, boolean errorIfRelativeEmpty) {
		field.addValidator(new FileExistsValidator(relativeTo, errorMsg, false, errorIfRelativeEmpty));
		add(field);
	}

	public void fileExists (VisValidableTextField field, File relativeTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relativeTo, errorMsg));
		add(field);
	}

	public void fileExists (VisValidableTextField field, FileHandle relativeTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relativeTo.file(), errorMsg));
		add(field);
	}

	public void fileNotExists (VisValidableTextField field, String errorMsg) {
		field.addValidator(new FileExistsValidator(errorMsg, true));
		add(field);
	}

	public void fileNotExists (VisValidableTextField field, VisTextField relativeTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relativeTo, errorMsg, true));
		add(field);
	}

	public void fileNotExists (VisValidableTextField field, File relativeTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relativeTo, errorMsg, true));
		add(field);
	}

	public void fileNotExists (VisValidableTextField field, FileHandle relativeTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relativeTo.file(), errorMsg, true));
		add(field);
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
