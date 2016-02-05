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
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Adapter used to display items list in {@link ListView}. Classes implementing this interface should store array and
 * provide delegates to methods changing array state, such as add/remove etc. Those delegates should call
 * {@link #invalidateDataSet()}.
 * @author Kotcrab
 * @see ArrayAdapter
 * @see ArrayListAdapter
 * @since 1.0.0
 */
public interface ListAdapter<T> {
	void setListView (ListView view);

	void invalidateDataSet ();

	Iterable<T> iterable ();

	void fillTable (VisTable itemsTable);
}
