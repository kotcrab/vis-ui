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

import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

/** @author Kotcrab */
public class TestIssue131 extends VisWindow {
	public TestIssue131 () {
		super("issue #131");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		VisTextField field1 = new VisTextField("0.1234");
		VisTextField field2 = new VisTextField("4.5678");
		field1.setTextFieldFilter(new FloatDigitsOnlyFilter(true));
		field2.setTextFieldFilter(new FloatDigitsOnlyFilter(true));

		add(new LinkLabel("issue #131 - decimal point lost", "https://github.com/kotcrab/vis-ui/issues/131")).colspan(2).row();
		add(field1);
		add(field2);

		setResizable(true);
		setModal(false);
		addCloseButton();
		closeOnEscape();
		pack();
		centerWindow();
	}
}
