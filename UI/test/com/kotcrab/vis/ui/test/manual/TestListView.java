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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.ListView.ItemClickListener;

/** @author Kotcrab */
public class TestListView extends VisWindow {
	public TestListView () {
		super("listview");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		Array<Model> array = new Array<Model>();
		for (int i = 1; i <= 3; i++) {
			array.add(new Model("Windows" + i, VisUI.getSkin().getColor("vis-red")));
			array.add(new Model("Linux" + i, VisUI.getSkin().getColor("vis-blue")));
			array.add(new Model("OSX" + i, Color.WHITE));
		}

		final TestAdapter adapter = new TestAdapter(array);
		ListView<Model> view = new ListView<Model>(adapter);

		final VisValidatableTextField nameField = new VisValidatableTextField();
		VisTextButton addButton = new VisTextButton("Add");

		FormValidator validator = new FormValidator(addButton);
		validator.notEmpty(nameField, "");

		add(new VisLabel("New Name:"));
		add(nameField);
		add(addButton);
		row();
		add(view.getMainTable()).colspan(3).grow();

		addButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				//by changing array using adapter view will be invalidated automatically
				adapter.add(new Model(nameField.getText(), Color.GRAY));
				nameField.setText("");
			}
		});

		view.setItemClickListener(new ItemClickListener<Model>() {
			@Override
			public void clicked (Model item) {
				System.out.println("Clicked: " + item.name);
			}
		});

		setSize(300, 300);
		setPosition(458, 245);
	}

	private static class Model {
		public String name;
		public Color color;

		public Model (String name, Color color) {
			this.name = name;
			this.color = color;
		}
	}

	private static class TestAdapter extends ArrayAdapter<Model, VisTable> {
		private final Drawable bg = VisUI.getSkin().getDrawable("window-bg");
		private final Drawable selection = VisUI.getSkin().getDrawable("selection");

		public TestAdapter (Array<Model> array) {
			super(array);
			setSelectionPolicy(new SelectionPolicy.MultipleSelection<Model, VisTable>());
		}

		@Override
		protected VisTable createView (Model item) {
//			System.out.println("Create " + item.name);
			VisLabel label = new VisLabel(item.name);
			label.setColor(item.color);

			VisTable table = new VisTable();
			table.left();
			table.add(label);
			return table;
		}

		@Override
		protected void selectView (VisTable view) {
			view.setBackground(selection);
		}

		@Override
		protected void deselectView (VisTable view) {
			view.setBackground(bg);
		}
	}
}
