/*
 * Copyright 2014-2015 Pawel Pastuszak
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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestCollapsible extends VisWindow {

	public TestCollapsible () {
		super("collapsiblewidget");

		columnDefaults(0).left();

		addVisComponents();

		setPosition(1000, 390);
		pack();
	}

	private void addVisComponents () {
		VisCheckBox collapseCheckBox = new VisCheckBox("show advanced settings");
		collapseCheckBox.setChecked(true);

		VisTable table = new VisTable();
		final CollapsibleWidget collapsibleWidget = new CollapsibleWidget(table);

		VisTable numberTable = new VisTable(true);
		numberTable.add(new VisLabel("2 + 2 * 2 = "));
		numberTable.add(new VisTextField());

		table.defaults().left();
		table.defaults().padLeft(10);
		table.add(new VisCheckBox("advanced option #1")).row();
		table.add(new VisCheckBox("advanced option #2")).row();
		table.add(numberTable).padTop(3).row();

		collapseCheckBox.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				collapsibleWidget.setCollapsed(!collapsibleWidget.isCollapsed());
			}
		});

		VisTable notAdvancedTable = new VisTable(true);

		notAdvancedTable.defaults().left();
		notAdvancedTable.add(new VisLabel("less advanced settings")).expandX().fillX().row();
		notAdvancedTable.add(new VisCheckBox("option #1")).row();
		notAdvancedTable.add(new VisCheckBox("option #2")).row();
		notAdvancedTable.add(new VisTextButton("button"));

		add(collapseCheckBox).row();
		add(collapsibleWidget).expandX().fillX().row();
		add(new Separator()).padTop(10).fillX().expandX().row();
		add(notAdvancedTable).expandX().fillX().padTop(5).row();
		add().expand().fill().padBottom(3);
	}
}
