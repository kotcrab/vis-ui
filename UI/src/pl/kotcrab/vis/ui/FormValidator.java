/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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

package pl.kotcrab.vis.ui;

import java.io.File;

import pl.kotcrab.vis.ui.widget.VisTextField;
import pl.kotcrab.vis.ui.widget.VisValidableTextField;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class FormValidator {
	private ChangeSharedListener changeListener = new ChangeSharedListener();
	private Array<VisValidableTextField> fields = new Array<VisValidableTextField>();

	private Button button;
	private Label errorMsgLabel;

	public FormValidator (Button buttonToDisable, Label errorMsgLabel) {
		this.button = buttonToDisable;
		this.errorMsgLabel = errorMsgLabel;
	}

	public void notEmpty (VisValidableTextField field, String errorMsg) {
		field.addValidator(new EmptyInputValidator(errorMsg));
		add(field);
	}

	public void fileExist (VisValidableTextField field, String errorMsg) {
		field.addValidator(new FileExistsValidator(errorMsg));
		add(field);
	}

	public void fileExist (VisValidableTextField field, VisTextField relavtiveTo, String errorMsg) {
		field.addValidator(new FileExistsValidator(relavtiveTo, errorMsg));
		add(field);
	}

	private void add (VisValidableTextField field) {
		fields.add(field);
		field.addListener(changeListener);
		checkAll();
	}

	private void checkAll () {
		button.setDisabled(false);
		errorMsgLabel.setText("");

		for (VisValidableTextField field : fields)
			field.validateInput();

		for (VisValidableTextField field : fields) {
			if (field.isInputValid() == false) {

				Array<InputValidator> validators = field.getValidators();
				for (InputValidator v : validators) {
					ControllerValidator validator = (ControllerValidator)v;

					if (validator.getResult() == false) {
						errorMsgLabel.setText(validator.getErrorMsg());
						button.setDisabled(true);
						break;
					}
				}

				break;
			}
		}
	}

	private class EmptyInputValidator extends ControllerValidator {
		public EmptyInputValidator (String errorMsg) {
			super(errorMsg);
		}

		@Override
		public boolean validateInput (String input) {
			setResult(!input.isEmpty());
			return super.validateInput(input);
		}
	}

	private class FileExistsValidator extends ControllerValidator {
		VisTextField relativeTo;

		public FileExistsValidator (String errorMsg) {
			super(errorMsg);
		}

		public FileExistsValidator (VisTextField relavativeTo, String errorMsg) {
			super(errorMsg);
			this.relativeTo = relavativeTo;
		}

		@Override
		public boolean validateInput (String input) {
			File f = new File(relativeTo == null ? null : relativeTo.getText(), input);
			setResult(f.exists());
			return super.validateInput(input);
		}
	}

	private abstract class ControllerValidator implements InputValidator {
		private String errorMsg;
		private boolean result;

		public ControllerValidator (String errorMsg) {
			this.errorMsg = errorMsg;
		}

		public String getErrorMsg () {
			return errorMsg;
		}

		protected void setResult (boolean result) {
			this.result = result;
		}

		protected boolean getResult () {
			return result;
		}

		@Override
		public boolean validateInput (String input) {
			return result;
		}
	}

	private class ChangeSharedListener extends ChangeListener {
		@Override
		public void changed (ChangeEvent event, Actor actor) {
			checkAll();
		}
	}
}
