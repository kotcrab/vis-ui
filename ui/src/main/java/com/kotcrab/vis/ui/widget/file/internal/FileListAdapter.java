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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/** @author Kotcrab */
public class FileListAdapter extends ArrayAdapter<FileHandle, FileChooser.FileItem> {
	private final FileChooser chooser;
	private final Array<FileChooser.FileItem> orderedViews = new Array<FileChooser.FileItem>();
	private GridGroup gridGroup;

	public FileListAdapter (FileChooser chooser, Array<FileHandle> files) {
		super(files);
		this.chooser = chooser;
		gridGroup = new GridGroup(128f, 2f);
	}

	@Override
	protected FileChooser.FileItem createView (FileHandle item) {
		return chooser.new FileItem(item, chooser.getViewMode());
	}

	@Override
	public void fillTable (VisTable itemsTable) {
		getViews().clear(); //clear cache
		orderedViews.clear();
		gridGroup.clear();

		if (getItemsSorter() != null) sort(getItemsSorter());

		FileChooser.ViewMode viewMode = chooser.getViewMode();

		if (viewMode.isGridMode()) {
			viewMode.setupGridGroup(chooser.getSizes(), gridGroup);
			for (final FileHandle item : iterable()) {
				final FileChooser.FileItem view = getView(item);
				orderedViews.add(view);
				prepareViewBeforeAddingToTable(item, view);
				gridGroup.addActor(view);
			}

			itemsTable.add(gridGroup).growX().minWidth(0);
		} else {
			for (final FileHandle item : iterable()) {
				final FileChooser.FileItem view = getView(item);
				orderedViews.add(view);
				prepareViewBeforeAddingToTable(item, view);
				itemsTable.add(view).growX();
				itemsTable.row();
			}
		}
	}

	@Override
	public ObjectMap<FileHandle, FileChooser.FileItem> getViews () {
		return super.getViews();
	}

	public Array<FileChooser.FileItem> getOrderedViews () {
		return orderedViews;
	}
}
