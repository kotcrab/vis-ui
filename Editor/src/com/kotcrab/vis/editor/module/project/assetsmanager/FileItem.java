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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.editor.ObjectSupportModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.editor.ui.scene.entityproperties.ContentItemProperties;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.ProjectPathUtils;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;

//TODO refactor
public class FileItem extends Table {
	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private ObjectSupportModule supportModule;
	@InjectModule private TextureCacheModule textureCache;
	private FileHandle file;

	private TextureRegion region;
	private FileType type;

	private VisLabel name;

	private ObjectSupport support;

	public FileItem (ModuleInjector injector, FileHandle file) {
		super(VisUI.getSkin());
		injector.injectModules(this);
		this.file = file;
		init();
	}

	private void init () {
		setTouchable(Touchable.enabled);

		createContent();

		setBackground("menu-bg");
		name.setWrap(true);
		name.setAlignment(Align.center);
		add(name).expandX().fillX();
	}

	private void createContent () {
		String ext = file.extension();
		String relativePath = fileAccess.relativizeToAssetsFolder(file);

		boolean texture = ProjectPathUtils.isTexture(relativePath, ext);
		boolean atlas = ProjectPathUtils.isTextureAtlas(file, relativePath);

		if (texture || atlas) {
			type = texture ? FileType.TEXTURE : FileType.TEXTURE_ATLAS;

			name = new VisLabel(file.nameWithoutExtension(), "small");
			TextureRegion region;

			if (atlas)
				region = textureCache.getRegion(new AtlasRegionAsset(relativePath, null));
			else
				region = textureCache.getRegion(new TextureRegionAsset(relativePath));

			Image img = new Image(region);
			img.setScaling(Scaling.fit);
			add(img).expand().fill().row();

			this.region = region;

			return;
		}

		if (ext.equals("ttf")) {
			createDefaultView(FileType.TTF_FONT, "TTF Font", true);
			return;
		}

		if (ext.equals("fnt") && FileUtils.siblingExists(file, "png")) {
			createDefaultView(FileType.BMP_FONT_FILE, "BMP Font", true);
			return;
		}

		if (ext.equals("png") && FileUtils.siblingExists(file, "fnt")) {
			createDefaultView(FileType.BMP_FONT_TEXTURE, "BMP Font Texture", true);
			return;
		}

		if (ext.equals("p")) {
			createDefaultView(FileType.PARTICLE_EFFECT, "Particle Effect", true);
			return;
		}

		if (relativePath.startsWith("music") && (ext.equals("wav") || ext.equals("ogg") || ext.equals("mp3"))) {
			createDefaultView(FileType.MUSIC, "Music");
			return;
		}

		if (relativePath.startsWith("sound") && (ext.equals("wav") || ext.equals("ogg") || ext.equals("mp3"))) {
			createDefaultView(FileType.SOUND, "Sound");
			return;
		}

		support = findSupportForDirectory(ext, relativePath);
		if (support != null) {
			ContentItemProperties item = support.getContentItemProperties(relativePath, ext);
			if (item != null) {
				createDefaultView(FileType.NON_STANDARD, item.title, item.hideExtension);
				return;
			}
		}

		type = FileType.UNKNOWN;
		name = new VisLabel(file.name());
	}

	private ObjectSupport findSupportForDirectory (String ext, String relativePath) {
		for (ObjectSupport support : supportModule.getSupports())
			if (support.isSupportedDirectory(ext, relativePath)) return support;

		return null;
	}

	private void createDefaultView (FileType type, String itemTypeName) {
		createDefaultView(type, itemTypeName, false);
	}

	private void createDefaultView (FileType type, String itemTypeName, boolean hideExtension) {
		this.type = type;

		VisLabel tagLabel = new VisLabel((hideExtension ? "" : file.extension().toUpperCase() + " ") + itemTypeName, Color.GRAY);
		tagLabel.setWrap(true);
		tagLabel.setAlignment(Align.center);
		add(tagLabel).expandX().fillX().row();
		name = new VisLabel(file.nameWithoutExtension());
	}

	public void setSelected (boolean selected) {
		if (selected)
			setBackground("selection");
		else
			setBackground("menu-bg");
	}

	public FileHandle getFile () {
		return file;
	}

	public FileType getType () {
		return type;
	}

	public ObjectSupport getSupport () {
		return support;
	}

	public TextureRegion getRegion () {
		return region;
	}
}
