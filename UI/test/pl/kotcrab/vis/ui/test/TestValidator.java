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

import pl.kotcrab.vis.ui.TableUtils;
import pl.kotcrab.vis.ui.components.VisValidableTextField;
import pl.kotcrab.vis.ui.components.VisWindow;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class TestValidator extends VisWindow {

	public TestValidator (Stage parent) {
		super(parent, "test validator (number)");

		TableUtils.setSpaceDefaults(this);
		columnDefaults(0).left();

		VisValidableTextField textField = new VisValidableTextField(new IntegerValidator());

		add(textField);

		pack();
		setPositionToCenter();
		setPosition(getX() - 300, getY() - 250);
	}
}
