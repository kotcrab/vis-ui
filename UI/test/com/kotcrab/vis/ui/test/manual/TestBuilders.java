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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.building.CenteredTableBuilder;
import com.kotcrab.vis.ui.building.GridTableBuilder;
import com.kotcrab.vis.ui.building.OneColumnTableBuilder;
import com.kotcrab.vis.ui.building.OneRowTableBuilder;
import com.kotcrab.vis.ui.building.StandardTableBuilder;
import com.kotcrab.vis.ui.building.TableBuilder;
import com.kotcrab.vis.ui.building.utilities.CellWidget;
import com.kotcrab.vis.ui.building.utilities.Padding;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestBuilders extends VisWindow {

	public TestBuilders () {
		super("table builders");

		TableUtils.setSpaceDefaults(this);

		final Padding padding = new Padding(4, 3);

		VisTextButton standardButton;
		VisTextButton centeredButton;
		VisTextButton oneColumnButton;
		VisTextButton oneRowButton;
		VisTextButton gridButton;

		VisTable table = new VisTable(true);
		table.add(standardButton = new VisTextButton("standard"));
		table.add(centeredButton = new VisTextButton("centered"));
		table.add(oneColumnButton = new VisTextButton("one column"));
		table.add(oneRowButton = new VisTextButton("one row"));
		table.add(gridButton = new VisTextButton("grid"));

		standardButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().addActor(new TestBuilder("standard builder", new StandardTableBuilder(padding)));
			}
		});

		centeredButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().addActor(new TestBuilder("centered builder", new CenteredTableBuilder(padding)));
			}
		});

		oneColumnButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().addActor(new TestBuilder("one column builder", new OneColumnTableBuilder(padding)));
			}
		});

		oneRowButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().addActor(new TestBuilder("one row builder", new OneRowTableBuilder(padding)));
			}
		});

		gridButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().addActor(new TestBuilder("grid builder", new GridTableBuilder(padding, 3)));
			}
		});

		add(standardButton);
		add(centeredButton);
		add(oneColumnButton);
		add(oneRowButton);
		add(gridButton);

		pack();
		setPosition(800, 20);

		new GridTableBuilder(4);
	}

	private class TestBuilder extends VisWindow {
		public TestBuilder (String name, TableBuilder builder) {
			super(name);

			//setModal(true);
			setResizable(true);
			closeOnEscape();
			addCloseButton();

			builder.append(new VisLabel("path"));
			builder.append(CellWidget.of(new VisTextField()).expandX().fillX().wrap());
			builder.append(new VisTextButton("choose"));
			builder.row();

			builder.append(new VisLabel("name"));
			builder.append(CellWidget.of(new VisTextField()).expandX().fillX().wrap());
			builder.append((Actor) null);
			builder.row();

			builder.append(new VisLabel("description"));
			builder.append(CellWidget.of(new VisTextField()).expandX().fillX().wrap());
			builder.append((Actor) null);
			builder.row();

			builder.append(
					CellWidget.of(new VisLabel("error information label")).expandX().fillX().wrap(),
					CellWidget.wrap(new VisTextButton("cancel")),
					CellWidget.wrap(new VisTextButton("ok"))
			);

			Table table = builder.build();
			add(table).expand().fill();
			debugAll();

			pack();
			centerWindow();
		}
	}

}
