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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.*;
import com.kotcrab.vis.ui.building.utilities.CellWidget;
import com.kotcrab.vis.ui.building.utilities.Padding;
import com.kotcrab.vis.ui.building.utilities.layouts.ActorLayout;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

public class TestBuilders extends VisWindow {

	public TestBuilders () {
		super("table builders");

		TableUtils.setSpacingDefaults(this);

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

		final Padding padding = new Padding(2, 3);

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
				getStage().addActor(new TestBuilder("grid builder (row size == 2)", new GridTableBuilder(padding, 2)));
			}
		});

		add(standardButton);
		add(centeredButton);
		add(oneColumnButton);
		add(oneRowButton);
		add(gridButton);

		pack();
		setPosition(31, 35);

		new GridTableBuilder(4);
	}

	private class TestBuilder extends VisWindow {
		public TestBuilder (String name, TableBuilder builder) {
			super(name);

			RowLayout rowLayout = new RowLayout(new Padding(0, 0, 0, 5));

			setModal(true);
			closeOnEscape();
			addCloseButton();

			final VisCheckBox debugViewCheckBox = new VisCheckBox("toggle debug view");
			debugViewCheckBox.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					setDebug(debugViewCheckBox.isChecked(), true);
				}
			});

			builder.append(debugViewCheckBox);
			builder.row();

			builder.append(new VisLabel("title label"));
			builder.row();

			builder.append(new VisLabel("path"));
			builder.append(rowLayout, CellWidget.builder().fillX(),
					CellWidget.of(new VisTextField()).expandX().fillX().wrap(),
					CellWidget.of(new VisTextButton("choose")).padding(new Padding(0, 0)).wrap());
			builder.row();

			builder.append(new VisLabel("name"));
			builder.append(CellWidget.of(new VisTextField()).expandX().fillX().wrap());
			builder.row();

			builder.append(new VisLabel("description"));
			builder.append(CellWidget.of(new VisTextField()).fillX().wrap());
			builder.row();

			//rest of content won't fit on screen with OneRowTableBuilder
			if (builder instanceof OneRowTableBuilder == false) {
				builder.append(new VisLabel("checkboxes"));
				builder.append(rowLayout, getCheckBoxArray(5));
				builder.row();

				builder.append(CellWidget.of(new Separator()).fillX().wrap());
				builder.row();

				builder.append(new VisLabel("second part"));
				builder.row();

				builder.append(new VisLabel("sliders"));
				builder.append(rowLayout, getSlider(false), getSlider(false), getSlider(false), getSlider(true));
				builder.row();

				builder.append(rowLayout, getCheckBoxArray(8));
			}

			Table table = builder.build();
			add(table).expand().fill();

			pack();
			centerWindow();
		}

		private VisSlider getSlider (boolean vertical) {
			VisSlider slider = new VisSlider(0, 100, 1, vertical);
			slider.setValue(MathUtils.random(20, 80));
			return slider;
		}

		private VisCheckBox[] getCheckBoxArray (int count) {
			VisCheckBox[] array = new VisCheckBox[count];

			for (int i = 0; i < count; i++)
				array[i] = new VisCheckBox("check");

			return array;
		}
	}

	private class RowLayout implements ActorLayout {
		private Padding padding;

		public RowLayout (Padding padding) {
			this.padding = padding;
		}

		@Override
		public Actor convertToActor (Actor... widgets) {
			return convertToActor(CellWidget.wrap(widgets));
		}

		@Override
		public Actor convertToActor (CellWidget<?>... widgets) {
			OneRowTableBuilder builder = new OneRowTableBuilder(padding);

			for (CellWidget<?> widget : widgets)
				builder.append(widget);

			return builder.build();
		}
	}

}
