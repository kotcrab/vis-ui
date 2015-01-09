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

package pl.kotcrab.vis.ui.test;

import pl.kotcrab.vis.ui.FormValidator;
import pl.kotcrab.vis.ui.TableUtils;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisTextButton;
import pl.kotcrab.vis.ui.widget.VisValidableTextField;
import pl.kotcrab.vis.ui.widget.VisWindow;

import com.badlogic.gdx.graphics.Color;

public class TestFormValidator extends VisWindow {

	public TestFormValidator (boolean useVisComponets) {
		super("form validator");

		TableUtils.setSpaceDefaults(this);
		defaults().padRight(1);
		defaults().padLeft(1);

		VisTextButton cancelButton = new VisTextButton("cancel");
		VisTextButton acceptButton = new VisTextButton("accept");

		VisValidableTextField firstNameField = new VisValidableTextField();
		VisValidableTextField lastNameField = new VisValidableTextField();

		VisLabel errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		VisTable buttonTable = new VisTable(true);
		buttonTable.add(errorLabel).expand().fill();
		buttonTable.add(cancelButton);
		buttonTable.add(acceptButton);

		add(new VisLabel("first name: "));
		add(firstNameField).expand().fill();
		row();
		add(new VisLabel("last name: "));
		add(lastNameField).expand().fill();
		row();
		add(buttonTable).fill().expand().colspan(2).padBottom(3);

		FormValidator validator;
		validator = new FormValidator(acceptButton, errorLabel);
		validator.notEmpty(firstNameField, "first name cannot be empty");
		validator.notEmpty(lastNameField, "last name cannot be empty");

		pack();
		setPosition(700, 120);
	}
}
