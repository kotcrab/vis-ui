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

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.function.Supplier;

/**
 * Stores information about directory that is used
 * @author Kotcrab
 */
public final class AssetDirectoryDescriptor {
	private final String id;
	private final String uiName;
	private final Supplier<Drawable> menuItemIcon;
	private final Supplier<Drawable> assetsViewIcon;

	public AssetDirectoryDescriptor (String id, String uiName, Drawable assetsViewIcon) {
		this(id, uiName, assetsViewIcon, null);
	}

	public AssetDirectoryDescriptor (String id, String uiName, Drawable assetsViewIcon, Drawable menuItemIcon) {
		this(id, uiName, () -> assetsViewIcon, () -> menuItemIcon);
	}

	public AssetDirectoryDescriptor (String id, String uiName, Supplier<Drawable> assetsViewIcon) {
		this(id, uiName, assetsViewIcon, () -> null);
	}

	public AssetDirectoryDescriptor (String id, String uiName, Supplier<Drawable> assetsViewIcon, Supplier<Drawable> menuItemIcon) {
		this.id = id;
		this.uiName = uiName;
		this.menuItemIcon = menuItemIcon;
		this.assetsViewIcon = assetsViewIcon;
	}

	public String getId () {
		return id;
	}

	public String getUIName () {
		return uiName;
	}

	public Drawable getMenuItemIcon () {
		return menuItemIcon.get();
	}

	public Drawable getAssetsViewIcon () {
		return assetsViewIcon.get();
	}
}
