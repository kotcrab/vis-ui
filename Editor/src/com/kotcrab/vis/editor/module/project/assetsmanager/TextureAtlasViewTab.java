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

package com.kotcrab.vis.editor.module.project.assetsmanager;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.ui.SearchField;
import com.kotcrab.vis.editor.ui.tab.CloseTabWhenMovingResources;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * Tab used to display all regions from single texture atlas
 * @author Kotcrab
 */
public class TextureAtlasViewTab extends Tab implements CloseTabWhenMovingResources {
	private String name;

	private VisTable contentTable;
	private Array<AtlasItem> items = new Array<>();
	private GridGroup filesView;

	public TextureAtlasViewTab (String relativeAtlasPath, TextureAtlas atlas, String name) {
		super(false, true);
		this.name = name;

		filesView = new GridGroup(92, 4);

		SearchField searchField = new SearchField(newText -> {
			filesView.clear();

			for (AtlasItem item : items) {
				if (item.getRegion().name.contains(newText))
					filesView.addActor(item);
			}

			if (filesView.getChildren().size == 0)
				return false;
			else
				return true;
		});

		VisTable topTable = new VisTable(true);
		topTable.add(name);
		topTable.add().expand().fill();
		topTable.add(searchField).right().row();

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

	@Override
	public void reopenSelfAfterAssetsUpdated () {
		//TODO reopen self
	}
}
