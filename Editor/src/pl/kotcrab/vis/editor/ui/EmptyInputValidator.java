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
import pl.kotcrab.vis.ui.widget.VisLabel;

public class EmptyInputValidator implements InputValidator {
	private String errorMsg;
	private VisLabel msgTarget;

	public EmptyInputValidator () {

	}

	public EmptyInputValidator (String errorMsg, VisLabel msgTarget) {
		this.errorMsg = errorMsg;
		this.msgTarget = msgTarget;
	}

	@Override
	public boolean validateInput (String input) {
		if (input.isEmpty()) {
			if (msgTarget != null) msgTarget.setText(errorMsg);
			return false;
		}
		
		msgTarget.setText("");
		return true;
	}
}
