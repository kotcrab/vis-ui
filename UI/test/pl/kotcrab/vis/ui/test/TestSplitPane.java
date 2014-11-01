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
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.components.VisLabel;
import pl.kotcrab.vis.ui.components.VisScrollPane;
import pl.kotcrab.vis.ui.components.VisSplitPane;
import pl.kotcrab.vis.ui.components.VisWindow;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class TestSplitPane extends VisWindow {

	public TestSplitPane (Stage parent) {
		super(parent, "splitpane");

		TableUtils.setSpaceDefaults(this);
		columnDefaults(0).left();

		VisLabel label = new VisLabel("label with some \nlong \ntext");
		VisLabel label2 = new VisLabel("label with some \nlong \ntext");

		VisSplitPane splitPane = new VisSplitPane(label, label2, false);
		add(splitPane).fill().expand();

		setSize(300, 150);
		setPositionToCenter();
		setPosition(getX(), getY() - 250);
	}
}
