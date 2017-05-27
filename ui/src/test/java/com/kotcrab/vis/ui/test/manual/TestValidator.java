/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.test.manual;

import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestValidator extends VisWindow {

	public TestValidator () {
		super("input validator (float number)");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		VisValidatableTextField textField = new VisValidatableTextField(Validators.FLOATS);

		add(textField);

		pack();
		setPosition(600, 18);
	}

}
