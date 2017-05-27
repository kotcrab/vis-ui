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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.file.FileChooser;

import static com.kotcrab.vis.ui.widget.file.internal.FileChooserText.*;

/** @author Kotcrab */
public class SortingPopupMenu extends PopupMenu {
	private final FileChooser chooser;
	private final Drawable selectedMenuItem;

	private MenuItem sortByName;
	private MenuItem sortByDate;
	private MenuItem sortBySize;
	private MenuItem sortByAscending;
	private MenuItem sortByDescending;

	private Image sortByNameImage;
	private Image sortByDateImage;
	private Image sortBySizeImage;
	private Image sortByAscendingImage;
	private Image sortByDescendingImage;


	public SortingPopupMenu (final FileChooser chooser) {
		selectedMenuItem = chooser.getChooserStyle().contextMenuSelectedItem;
		this.chooser = chooser;

		addItem(sortByName = new MenuItem(SORT_BY_NAME.get(), selectedMenuItem, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				chooser.setSorting(FileChooser.FileSorting.NAME, true);
			}
		}));
		addItem(sortByDate = new MenuItem(SORT_BY_DATE.get(), selectedMenuItem, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				chooser.setSorting(FileChooser.FileSorting.MODIFIED_DATE, false);
			}
		}));
		addItem(sortBySize = new MenuItem(SORT_BY_SIZE.get(), selectedMenuItem, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				chooser.setSorting(FileChooser.FileSorting.SIZE, true);
			}
		}));

		addSeparator();

		addItem(sortByAscending = new MenuItem(SORT_BY_ASCENDING.get(), selectedMenuItem, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				chooser.setSortingOrderAscending(true);
			}
		}));
		addItem(sortByDescending = new MenuItem(SORT_BY_DESCENDING.get(), selectedMenuItem, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				chooser.setSortingOrderAscending(false);
			}
		}));

		sortByNameImage = sortByName.getImage();
		sortByDateImage = sortByDate.getImage();
		sortBySizeImage = sortBySize.getImage();
		sortByAscendingImage = sortByAscending.getImage();
		sortByDescendingImage = sortByDescending.getImage();

		sortByNameImage.setScaling(Scaling.none);
		sortByDateImage.setScaling(Scaling.none);
		sortBySizeImage.setScaling(Scaling.none);
		sortByAscendingImage.setScaling(Scaling.none);
		sortByDescendingImage.setScaling(Scaling.none);
	}

	public void build () {
		sortByNameImage.setDrawable(null);
		sortByDateImage.setDrawable(null);
		sortBySizeImage.setDrawable(null);
		sortByAscendingImage.setDrawable(null);
		sortByDescendingImage.setDrawable(null);
		switch (chooser.getSorting()) {
			case NAME:
				sortByNameImage.setDrawable(selectedMenuItem);
				break;
			case MODIFIED_DATE:
				sortByDateImage.setDrawable(selectedMenuItem);
				break;
			case SIZE:
				sortBySizeImage.setDrawable(selectedMenuItem);
				break;
		}

		if (chooser.isSortingOrderAscending()) {
			sortByAscendingImage.setDrawable(selectedMenuItem);
		} else {
			sortByDescendingImage.setDrawable(selectedMenuItem);
		}
	}
}
