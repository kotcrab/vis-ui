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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.util.adapter.ListSelectionAdapter;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.ListView.ItemClickListener;
import com.kotcrab.vis.ui.widget.ListView.UpdatePolicy;

import java.util.Comparator;

/** @author Kotcrab */
public class TestListView extends VisWindow {
	public TestListView () {
		super("listview");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		addCloseButton();
		closeOnEscape();

		Array<Model> array = new Array<Model>();
		for (int i = 1; i <= 3; i++) {
			array.add(new Model("Windows" + i, VisUI.getSkin().getColor("vis-red")));
			array.add(new Model("Linux" + i, Color.GREEN));
			array.add(new Model("OSX" + i, Color.WHITE));
		}

		final TestAdapter adapter = new TestAdapter(array);
		ListView<Model> view = new ListView<Model>(adapter);
		view.setUpdatePolicy(UpdatePolicy.ON_DRAW);

		VisTable footerTable = new VisTable();
		footerTable.addSeparator();
		footerTable.add("Table Footer");
		view.setFooter(footerTable);

		final VisValidatableTextField nameField = new VisValidatableTextField();
		VisTextButton addButton = new VisTextButton("Add");

		SimpleFormValidator validator = new SimpleFormValidator(addButton);
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

		adapter.setSelectionMode(AbstractListAdapter.SelectionMode.SINGLE);
		view.setItemClickListener(new ItemClickListener<Model>() {
			@Override
			public void clicked (Model item) {
				System.out.println("Clicked: " + item.name);
			}
		});
		adapter.getSelectionManager().setListener(new ListSelectionAdapter<Model, VisTable>() {
			@Override
			public void selected (Model item, VisTable view) {
				System.out.println("ListSelection Selected: " + item.name);
			}

			@Override
			public void deselected (Model item, VisTable view) {
				System.out.println("ListSelection Deselected: " + item.name);
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
		private final Drawable selection = VisUI.getSkin().getDrawable("list-selection");

		public TestAdapter (Array<Model> array) {
			super(array);
			setSelectionMode(SelectionMode.SINGLE);

			setItemsSorter(new Comparator<Model>() {
				@Override
				public int compare (Model o1, Model o2) {
					return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
				}
			});
		}

		@Override
		protected VisTable createView (Model item) {
			VisLabel label = new VisLabel(item.name);
			label.setColor(item.color);

			VisTable table = new VisTable();
			table.left();
			table.add(label);
			return table;
		}

		@Override
		protected void updateView (VisTable view, Model item) {
			super.updateView(view, item);
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
