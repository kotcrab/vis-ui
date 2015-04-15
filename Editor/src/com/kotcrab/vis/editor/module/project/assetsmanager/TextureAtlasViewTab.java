/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.editor.module.project.assetsmanager;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.ui.SearchField;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class TextureAtlasViewTab extends Tab {
	private TextureAtlas atlas;
	private String name;

	private VisTable contentTable;
	private Array<AtlasItem> items = new Array<>();
	private final GridGroup filesView;
	private final SearchField searchField;

	public TextureAtlasViewTab (String relativeAtlasPath, TextureAtlas atlas, String name) {
		super(false, true);
		this.atlas = atlas;
		this.name = name;

		searchField = new SearchField();

		searchField.addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				refreshSearch();
				return true;
			}
		});

		VisTable topTable = new VisTable(true);
		topTable.add(name);
		topTable.add().expand().fill();
		topTable.add(searchField).right().row();

		filesView = new GridGroup(92, 4);

		VisScrollPane scrollPane = new VisScrollPane(filesView);
		scrollPane.setFlickScroll(false);
		scrollPane.setScrollingDisabled(true, false);

		contentTable = new VisTable();
		contentTable.setBackground(VisUI.getSkin().getDrawable("window-bg"));
		contentTable.add(topTable).expandX().fillX().pad(3).row();
		contentTable.addSeparator().pad(0);
		contentTable.add(scrollPane).expand().fill();

		Array<AtlasRegion> regions = atlas.getRegions();

		for (AtlasRegion region : regions) {
			AtlasItem item = new AtlasItem(relativeAtlasPath, region);
			items.add(item);
			filesView.addActor(item);
		}
	}

	private void refreshSearch () {
		filesView.clear();

		for (AtlasItem item : items) {
			if (item.getRegion().name.contains(searchField.getText()))
				filesView.addActor(item);
		}

		if (filesView.getChildren().size == 0)
			searchField.setInputValid(false);
		else
			searchField.setInputValid(true);
	}

	public Array<AtlasItem> getItems () {
		return items;
	}

	@Override
	public String getTabTitle () {
		return name;
	}

	@Override
	public Table getContentTable () {
		return contentTable;
	}
}
