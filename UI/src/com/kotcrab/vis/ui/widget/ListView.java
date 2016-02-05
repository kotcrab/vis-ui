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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.util.adapter.ArrayListAdapter;
import com.kotcrab.vis.ui.util.adapter.ListAdapter;

/**
 * ListView displays list of scrollable items. Item views are created by using {@link ListAdapter}s.
 * @author Kotcrab
 * @see ListAdapter
 * @see ArrayAdapter
 * @see ArrayListAdapter
 * @since 1.0.0
 */
public class ListView {
	private ListAdapter<?> adapter;

	private VisTable mainTable;
	private VisScrollPane scrollPane;

	private VisTable scrollTable;
	private VisTable itemsTable;

	private Actor header;
	private Actor footer;

	public ListView (ListAdapter<?> adapter) {
		mainTable = new VisTable();
		scrollTable = new VisTable();
		itemsTable = new VisTable();

		scrollPane = new VisScrollPane(scrollTable);
		mainTable.add(scrollPane).grow();

		setAdapter(adapter);
	}

	/**
	 * Notifies view that underlying items data changed and view needs to be refreshed. Note that if modifying items
	 * data using adapter this will be called automatically.
	 */
	public void invalidateDataSet () {
		rebuildView(true);
	}

	private void invalidateView () {
		rebuildView(false);
	}

	private void rebuildView (boolean full) {
		scrollTable.clearChildren();
		scrollTable.top();
		if (header != null) {
			scrollTable.add(header).growX();
			scrollTable.row();
		}

		if (full) {
			itemsTable.clearChildren();
			adapter.fillTable(itemsTable);
		}

		scrollTable.add(itemsTable).growX();
		scrollTable.row();

		if (footer != null) {
			scrollTable.add(header).growX();
			scrollTable.row();
		}
	}

	/**
	 * @return main table containing scroll pane and all items view
	 */
	public VisTable getMainTable () {
		return mainTable;
	}

	/**
	 * @return internal {@link VisScrollPane}. Do NOT add this scroll pane directly to {@link Stage}
	 * use {@link #getMainTable()} instead. Use this only for changing scroll pane properties.
	 */
	public VisScrollPane getScrollPane () {
		return scrollPane;
	}

	public void setAdapter (ListAdapter<?> adapter) {
		if (this.adapter != null) this.adapter.setListView(null);
		this.adapter = adapter;
		adapter.setListView(this);
		invalidateDataSet();
	}

	public Actor getHeader () {
		return header;
	}

	public void setHeader (Actor header) {
		this.header = header;
		invalidateView();
	}

	public Actor getFooter () {
		return footer;
	}

	public void setFooter (Actor footer) {
		this.footer = footer;
		invalidateView();
	}

}
