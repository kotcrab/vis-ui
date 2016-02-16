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

package com.kotcrab.vis.ui.util.adapter;

import com.kotcrab.vis.ui.widget.ListView;
import com.kotcrab.vis.ui.widget.ListView.ItemClickListener;
import com.kotcrab.vis.ui.widget.ListView.ListAdapterListener;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Adapter used to display items list in {@link ListView}. Classes implementing this interface should store array and
 * provide delegates to methods that change array state, such as add/remove etc. Those delegates should call
 * {@link ListAdapterListener#invalidateDataSet()}. Single instance of ListAdapter can only be used for one ListView.
 * Implementations must support setting item click listener.
 * @author Kotcrab
 * @see ArrayAdapter
 * @see ArrayListAdapter
 * @since 1.0.0
 */
public interface ListAdapter<ItemT> {
	/** Called by {@link ListView} when this adapter is assigned to it. */
	void setListView (ListView<ItemT> view, ListAdapterListener viewListener);

	/** Called by {@link ListView} when this adapter should create and add all views to provided itemsTable. */
	void fillTable (VisTable itemsTable);

	/** Called by {@link ListView} when it's item click listener changed. */
	void setItemClickListener (ItemClickListener<ItemT> listener);

	/** @return iterable for internal collection */
	Iterable<ItemT> iterable ();

	/** @return size of internal collection */
	int size ();

	/** @return index of element in internal collection */
	int indexOf (ItemT item);

	/** Adds item to internal collection */
	void add (ItemT item);

	/** @return element for given index */
	ItemT get (int index);
}
