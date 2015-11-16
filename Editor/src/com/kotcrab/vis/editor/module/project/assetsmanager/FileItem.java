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
import com.kotcrab.vis.editor.assets.AssetFileType;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;

//TODO refactor
//TODO filter system in assets ui module

/**
 * Displays single item inside assets manager.
 * @author Kotcrab
 */
public class FileItem extends Table {
	private ExtensionStorageModule extensionStorage;

	private FileAccessModule fileAccess;
	private TextureCacheModule textureCache;

	private FileHandle file;

	private TextureRegion region;
	private AssetFileType type;

	private VisLabel name;

	private EditorEntitySupport support;

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

		if (ProjectPathUtils.isTrueTypeFont(file)) {
			createDefaultView(AssetFileType.TTF_FONT, "TTF Font", true);
			return;
		}

		if (ProjectPathUtils.isBitmapFont(file)) {
			createDefaultView(AssetFileType.BMP_FONT_FILE, "BMP Font", true);
			return;
		}

		if (ProjectPathUtils.isBitmapFontTexture(file)) {
			createDefaultView(AssetFileType.BMP_FONT_TEXTURE, "BMP Font Texture", true);
			return;
		}

		if (texture || atlas) {
			type = texture ? AssetFileType.TEXTURE : AssetFileType.TEXTURE_ATLAS;

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

		if (ProjectPathUtils.isParticle(file)) {
			createDefaultView(AssetFileType.PARTICLE_EFFECT, "Particle Effect", true);
			return;
		}

		if (relativePath.startsWith("music") && (ext.equals("wav") || ext.equals("ogg") || ext.equals("mp3"))) {
			createDefaultView(AssetFileType.MUSIC, "Music");
			return;
		}

		if (relativePath.startsWith("sound") && (ext.equals("wav") || ext.equals("ogg") || ext.equals("mp3"))) {
			createDefaultView(AssetFileType.SOUND, "Sound");
			return;
		}

		if (relativePath.startsWith("shader") && ext.equals("frag")) {
			createDefaultView(AssetFileType.FRAGMENT_SHADER, "Fragment Shader", true);
			return;
		}

		if (relativePath.startsWith("shader") && ext.equals("vert")) {
			createDefaultView(AssetFileType.VERTEX_SHADER, "Vertex Shader", true);
			return;
		}

		if (relativePath.startsWith("spriter") && ext.equals("scml") && file.parent().child(".vis").exists()) {
			createDefaultView(AssetFileType.SPRITER_SCML, "Spriter Animation", true);
			return;
		}

		if (relativePath.startsWith("scene") && ext.equals("scene")) {
			createDefaultView(AssetFileType.SCENE, "Scene", true);
			return;
		}


		support = findSupportForDirectory(ext, relativePath);
		if (support != null) {
			ContentItemProperties item = support.getContentItemProperties(relativePath, ext);
			if (item != null) {
				createDefaultView(AssetFileType.NON_STANDARD, item.title, item.hideExtension);
				return;
			}
		}

		type = AssetFileType.UNKNOWN;
		name = new VisLabel(file.name());
	}

	private EditorEntitySupport findSupportForDirectory (String ext, String relativePath) {
		for (EditorEntitySupport support : extensionStorage.getEntitiesSupports())
			if (support.isSupportedDirectory(relativePath, ext)) return support;

		return null;
	}

	private void createDefaultView (AssetFileType type, String itemTypeName) {
		createDefaultView(type, itemTypeName, false);
	}

	private void createDefaultView (AssetFileType type, String itemTypeName, boolean hideExtension) {
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

	public AssetFileType getType () {
		return type;
	}

	public EditorEntitySupport getSupport () {
		return support;
	}

	public TextureRegion getRegion () {
		return region;
	}
}
