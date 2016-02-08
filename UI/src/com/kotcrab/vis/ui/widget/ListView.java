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

import com.badlogic.gdx.graphics.g2d.Batch;
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
public class ListView<ItemT> {
	private ListAdapter<ItemT> adapter;
	private ListAdapterListener adapterListener = new ListAdapterListener();

	private UpdatePolicy updatePolicy = UpdatePolicy.IMMEDIATELY;
	private boolean dataInvalidated = false;

	private VisTable mainTable;
	private VisScrollPane scrollPane;

	private VisTable scrollTable;
	private VisTable itemsTable;

	private Actor header;
	private Actor footer;

	public ListView (ListAdapter<ItemT> adapter) {
		mainTable = new VisTable() {
			@Override
			public void draw (Batch batch, float parentAlpha) {
				if (updatePolicy == UpdatePolicy.ON_DRAW && dataInvalidated) rebuildView(true);
				super.draw(batch, parentAlpha);
			}
		};
		scrollTable = new VisTable();
		itemsTable = new VisTable();

		scrollPane = new VisScrollPane(scrollTable);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		mainTable.add(scrollPane).grow();

		setAdapter(adapter);
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

	public void setAdapter (ListAdapter<ItemT> adapter) {
		if (this.adapter != null) this.adapter.setListView(null, null);
		this.adapter = adapter;
		adapter.setListView(this, adapterListener);
		rebuildView(true);
	}

	public void setItemClickListener (ItemClickListener<ItemT> listener) {
		adapter.setItemClickListener(listener);
	}

	public Actor getHeader () {
		return header;
	}

	public void setHeader (Actor header) {
		this.header = header;
		rebuildView(false);
	}

	public Actor getFooter () {
		return footer;
	}

	public void setFooter (Actor footer) {
		this.footer = footer;
		rebuildView(false);
	}

	public void setUpdatePolicy (UpdatePolicy updatePolicy) {
		this.updatePolicy = updatePolicy;
	}

	public UpdatePolicy getUpdatePolicy () {
		return updatePolicy;
	}

	public interface ItemClickListener<ItemT> {
		void clicked (ItemT item);
	}

	public class ListAdapterListener {
		public void invalidateDataSet () {
			if (updatePolicy == UpdatePolicy.IMMEDIATELY) rebuildView(true);
			if (updatePolicy == UpdatePolicy.ON_DRAW) dataInvalidated = true;
		}
	}

	public enum UpdatePolicy {
		/** If list data was was invalidated then views are updated before drawing list. */
		ON_DRAW,
		/** If list data was was invalidated then views are updated immediately after data invalidation. */
		IMMEDIATELY
	}
}
