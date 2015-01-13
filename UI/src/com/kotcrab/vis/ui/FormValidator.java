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

package com.kotcrab.vis.ui;

import java.io.File;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

public class FormValidator extends BasicFormValidator {
	public FormValidator (Button buttonToDisable, Label errorMsgLabel) {
		super(buttonToDisable, errorMsgLabel);
	}

	public void fileExists (VisValidableTextField field, String errorMsg) {
		field.addValidator(new FileExistsValidator(errorMsg));
		add(field);
	}

	public void fileExists (VisValidableTextField field, VisTextField relavtiveTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relavtiveTo, errorMsg));
		add(field);
	}

	public void fileExists (VisValidableTextField field, File relavtiveTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relavtiveTo, errorMsg));
		add(field);
	}

	public void fileExists (VisValidableTextField field, FileHandle relavtiveTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relavtiveTo.file(), errorMsg));
		add(field);
	}

	public void fileNotExists (VisValidableTextField field, String errorMsg) {
		field.addValidator(new FileExistsValidator(errorMsg, true));
		add(field);
	}

	public void fileNotExists (VisValidableTextField field, VisTextField relavtiveTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relavtiveTo, errorMsg, true));
		add(field);
	}

	public void fileNotExists (VisValidableTextField field, File relavtiveTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relavtiveTo, errorMsg, true));
		add(field);
	}

	public void fileNotExists (VisValidableTextField field, FileHandle relavtiveTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relavtiveTo.file(), errorMsg, true));
		add(field);
	}

	private class FileExistsValidator extends FormInputValidator {
		VisTextField relativeTo;
		File relativeToFile;
		boolean existNot;

		public FileExistsValidator (String errorMsg) {
			this(errorMsg, false);
		}

		public FileExistsValidator (VisTextField relavativeTo, String errorMsg) {
			this(relavativeTo, errorMsg, false);
		}

		public FileExistsValidator (File relavativeTo, String errorMsg) {
			this(relavativeTo, errorMsg, false);
		}

		public FileExistsValidator (String errorMsg, boolean existNot) {
			super(errorMsg);
			this.existNot = existNot;
		}

		public FileExistsValidator (VisTextField relavativeTo, String errorMsg, boolean existNot) {
			super(errorMsg);
			this.relativeTo = relavativeTo;
			this.existNot = existNot;
		}

		public FileExistsValidator (File relavativeTo, String errorMsg, boolean existNot) {
			super(errorMsg);
			this.relativeToFile = relavativeTo;
			this.existNot = existNot;
		}

		@Override
		public boolean validateInput (String input) {
			File f = null;
			if (relativeTo != null)
				f = new File(relativeTo.getText(), input);
			else if (relativeToFile != null)
				f = new File(relativeToFile, input);
			else
				f = new File(input);

			if (existNot)
				setResult(!f.exists());
			else
				setResult(f.exists());

			return super.validateInput(input);
		}
	}
}
