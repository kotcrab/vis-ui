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

import pl.kotcrab.vis.ui.InputValidator;
import pl.kotcrab.vis.ui.widget.VisValidableTextField;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

public class FormValidator {
	private SharedListener listener = new SharedListener();
	private Array<VisValidableTextField> fields = new Array<VisValidableTextField>();;

	private Button button;
	private Label errorMsgLabel;

	public FormValidator (Button buttonToDisable, Label errorMsgLabel) {
		this.button = buttonToDisable;
		this.errorMsgLabel = errorMsgLabel;
	}

	public void notEmpty (VisValidableTextField field, String errorMsg) {
		fields.add(field);
		field.addValidator(new EmptyInputValidator(errorMsg));
		field.addListener(listener);
	}

	private void checkAll () {
		button.setDisabled(false);

		for (VisValidableTextField f : fields) {
			if (f.isInputValid() == false) {
				button.setDisabled(true);
				break;
			}
		}

		errorMsgLabel.setText("");

		for (VisValidableTextField field : fields) {
			if (field.isInputValid() == false) {
				ControllerValidator validator = (ControllerValidator)field.getValidator();
				errorMsgLabel.setText(validator.getErrorMsg());
				break;
			}
		}

	}

	private class SharedListener extends InputListener {
		@Override
		public boolean keyTyped (InputEvent event, char character) {
			checkAll();
			return false;
		}

	}

	private class EmptyInputValidator implements ControllerValidator {
		private String errorMsg;

		public EmptyInputValidator (String errorMsg) {
			this.errorMsg = errorMsg;
		}

		@Override
		public boolean validateInput (String input) {
			if (input.isEmpty()) return false;
			return true;
		}

		@Override
		public String getErrorMsg () {
			return errorMsg;
		}
	}

	private interface ControllerValidator extends InputValidator {
		public String getErrorMsg ();
	}
}
