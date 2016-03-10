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

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;

/**
 * Displays single region from texture atlas
 * @author Kotcrab
 */
public class AtlasItem extends Table {
	private AtlasRegionAsset assetDescriptor;
	private String relativeAtlasPath;
	private AtlasRegion region;

	public AtlasItem (String relativeAtlasPath, AtlasRegion region) {
		super(VisUI.getSkin());
		this.relativeAtlasPath = relativeAtlasPath;
		this.region = region;

		assetDescriptor = new AtlasRegionAsset(relativeAtlasPath, region.name);

		setTouchable(Touchable.enabled);
		setBackground("menu-bg");

		Image img = new Image(region);
		img.setScaling(Scaling.fit);
		add(img).expand().fill().row();

		VisLabel name = new VisLabel(region.name, "small");
		name.setWrap(true);
		name.setAlignment(Align.center);
		add(name).expandX().fillX();
	}

	public AtlasRegionAsset getAtlasAsset () {
		return assetDescriptor;
	}

	public AtlasRegion getRegion () {
		return region;
	}
}
