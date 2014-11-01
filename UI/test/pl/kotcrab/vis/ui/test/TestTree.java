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
import pl.kotcrab.vis.ui.components.VisLabel;
import pl.kotcrab.vis.ui.components.VisTree;
import pl.kotcrab.vis.ui.components.VisWindow;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;

public class TestTree extends VisWindow {

	public TestTree (Stage parent) {
		super(parent, "test tree");

		TableUtils.setSpaceDefaults(this);
		columnDefaults(0).left();

		VisTree tree = new VisTree();
		Node item1 = new Node(new VisLabel("item 1"));
		Node item2 = new Node(new VisLabel("item 2"));
		Node item3 = new Node(new VisLabel("item 3"));
		
		item1.add(new Node(new VisLabel("item 1.1")));
		item1.add(new Node(new VisLabel("item 1.2")));
		item1.add(new Node(new VisLabel("item 1.3")));
		
		item2.add(new Node(new VisLabel("item 2.1")));
		item2.add(new Node(new VisLabel("item 2.2")));
		item2.add(new Node(new VisLabel("item 2.3")));
		
		item3.add(new Node(new VisLabel("item 3.1")));
		item3.add(new Node(new VisLabel("item 3.2")));
		item3.add(new Node(new VisLabel("item 3.3")));
		
		item1.setExpanded(true);
		
		tree.add(item1);
		tree.add(item2);
		tree.add(item3);

		add(tree).expand().fill();

		setSize(150, 380);
		setPositionToCenter();
		setPosition(getX() + 380, getY());
	}
}
