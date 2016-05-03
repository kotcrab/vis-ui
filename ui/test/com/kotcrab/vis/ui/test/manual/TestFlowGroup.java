/*
 * Copyright 2014-2016 See AUTHORS file.
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

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import com.kotcrab.vis.ui.layout.VerticalFlowGroup;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisWindow;

/** @author Kotcrab */
public class TestFlowGroup extends VisWindow {
	public TestFlowGroup () {
		super("flow groups");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		setResizable(true);
		addCloseButton();
		closeOnEscape();

		WidgetGroup group = new VerticalFlowGroup(2);

		String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi luctus magna sit amet tellus egestas tincidunt. " +
				"Morbi tempus eleifend dictum. Nunc ex nisl, dignissim eget gravida vel, rutrum a nibh. Fusce congue odio ac elit " +
				"rhoncus rutrum. Donec nec lectus leo. Phasellus et consectetur ante. Cras vel consectetur mauris, sed semper lectus. ";
		String[] parts = lorem.split(" ");
		for (String part : parts) {
			group.addActor(new VisLabel(part));
		}

//		group.addActor(new VisLabel("Lorem ipsum"));
//		group.addActor(new VisLabel("dolor sit"));
//		group.addActor(new VisLabel("amet"));
//		group.addActor(new VisLabel("a\nb\nc"));
//		group.addActor(new VisLabel("Lorem ipsum"));
//		group.addActor(new VisLabel("dolor sit"));
//		group.addActor(new VisLabel("amet"));
//		group.addActor(new VisLabel("a\nb\nc"));
//		group.addActor(new VisLabel("Lorem ipsum"));
//		group.addActor(new VisLabel("dolor sit"));
//		group.addActor(new VisLabel("amet"));
//		group.addActor(new VisLabel("a\nb\nc"));

		VisScrollPane scrollPane = new VisScrollPane(group);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setFlickScroll(false);
		scrollPane.setOverscroll(false, false);
		scrollPane.setScrollingDisabled(group instanceof HorizontalFlowGroup, group instanceof VerticalFlowGroup);
		add(scrollPane).grow();

		setSize(300, 150);
		centerWindow();
	}
}
