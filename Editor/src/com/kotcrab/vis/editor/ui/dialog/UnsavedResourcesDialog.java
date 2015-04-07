/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.ui.WindowListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class UnsavedResourcesDialog extends VisWindow {
	private WindowListener listener;

	private Array<Tab> allTabs;
	private Array<Tab> unsavedTabs = new Array<>();
	private VisList<String> tabList;

	public UnsavedResourcesDialog (TabsModule tabsModule, final WindowListener listener) {
		super("Unsaved resources");
		this.listener = listener;

		setModal(true);
		addCloseButton();
		closeOnEscape();

		VisTextButton cancelButton;
		VisTextButton saveButton;
		VisTextButton discardButton;
		final VisTextButton saveAllButton;

		TableUtils.setSpacingDefaults(this);
		defaults().left();

		VisTable buttonsTable = new VisTable(true);
		buttonsTable.defaults().expandX().fillX();
		buttonsTable.add(saveButton = new VisTextButton("Save")).row();
		buttonsTable.add(saveAllButton = new VisTextButton("Save All")).row();
		buttonsTable.add(discardButton = new VisTextButton("Discard All")).row();
		buttonsTable.add(cancelButton = new VisTextButton("Cancel")).row();

		tabList = new VisList<>();
		allTabs = tabsModule.getTabs();

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				listener.canceled();
				fadeOut();
			}
		});

		saveButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				saveCurrent();
				rebuildUnsavedResourcesList();
			}
		});

		discardButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				listener.finished();
				fadeOut();
			}
		});

		saveAllButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				saveAll();
				listener.finished();
			}
		});

		rebuildUnsavedResourcesList();

		add(tabList).expand().fill();
		add(buttonsTable).top();

		pack();
		setHeight(getHeight() + 30);
		if(getWidth() < 200) setWidth(200);
		centerWindow();
	}

	private void saveCurrent () {
		int index = tabList.getSelectedIndex();

		if (index == -1)
			return;

		unsavedTabs.get(index).save();
		rebuildUnsavedResourcesList();

		if (tabList.getItems().size == 0)
			listener.finished();
	}

	private void saveAll () {
		for (Tab tab : unsavedTabs)
			tab.save();
	}

	private void rebuildUnsavedResourcesList () {
		unsavedTabs.clear();
		tabList.clearItems();

		Array<String> names = new Array<>();

		for (Tab tab : allTabs) {
			if (tab.isDirty()) {
				unsavedTabs.add(tab);
				names.add(tab.getTabTitle());
				tabList.setItems(names);
			}
		}
	}

	@Override
	protected void close () {
		super.close();
		listener.canceled();
	}
}
