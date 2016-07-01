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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.extension.AssetType;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectExtensionStorageModule;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.plugin.api.EditorEntitySupport;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;

//TODO refactor
//TODO filter system in assets ui module

/**
 * Displays single item inside assets manager.
 * @author Kotcrab
 */
public class FileItem extends Table {
	private AssetsMetadataModule assetsMetadata;

	private ProjectExtensionStorageModule projectExtensionStorage;
	private FileAccessModule fileAccess;
	private TextureCacheModule textureCache;

	private FileHandle file;
	private boolean isMainFile;

	private TextureRegion region;
	private String type;

	private VisLabel name;

	private EditorEntitySupport support;

	public FileItem (ModuleInjector injector, FileHandle file, boolean isMainFile) {
		super(VisUI.getSkin());
		injector.injectModules(this);
		this.file = file;
		this.isMainFile = isMainFile;
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

		boolean texture = ProjectPathUtils.isTexture(file);
		boolean atlas = ProjectPathUtils.isTextureAtlas(file);

		if (file.isDirectory()) {
			type = AssetType.DIRECTORY;

			Drawable icon;
			AssetDirectoryDescriptor desc = assetsMetadata.getAsDirectoryDescriptor(file);
			if (desc != null) {
				icon = desc.getAssetsViewIcon();
			} else {
				icon = Icons.FOLDER_MEDIUM.drawable();
			}
			add(new VisImage(icon, Scaling.fillX)).row();

			name = new VisLabel(file.nameWithoutExtension());
		}

		if (ProjectPathUtils.isTrueTypeFont(file)) {
			createDefaultView(AssetType.TTF_FONT, "TTF Font", true);
			return;
		}

		if (ProjectPathUtils.isBitmapFont(file)) {
			createDefaultView(AssetType.BMP_FONT_FILE, "BMP Font", true);
			return;
		}

		if (ProjectPathUtils.isBitmapFontTexture(file)) {
			createDefaultView(AssetType.BMP_FONT_TEXTURE, "BMP Font Texture", true);
			return;
		}

		if (ProjectPathUtils.isTextureAtlasImage(file)) {
			createDefaultView(AssetType.TEXTURE_ATLAS_IMAGE, "TextureAtlas Image", true);
			return;
		}

		if (texture || atlas) {
			type = texture ? AssetType.TEXTURE : AssetType.TEXTURE_ATLAS;

			//don't create region preview for files excluded from texture cache
			AssetDirectoryDescriptor desc = assetsMetadata.getAsDirectoryDescriptorRecursively(file);
			if (desc != null && desc.isExcludeFromTextureCache()) {
				createDefaultView(type, texture ? "Texture" : "Texture Atlas", true);
				return;
			}

			name = new VisLabel(texture ? file.nameWithoutExtension() : file.name(), "small");

			TextureRegion region;

			if (atlas) {
				region = textureCache.getRegion(new AtlasRegionAsset(relativePath, null));
			} else {
				region = textureCache.getRegion(new TextureRegionAsset(relativePath));
			}

			Image img = new Image(region);
			img.setScaling(Scaling.fit);
			add(img).expand().fill().row();

			this.region = region;

			return;
		}

		if (ProjectPathUtils.isParticle(file)) {
			createDefaultView(AssetType.PARTICLE_EFFECT, "Particle Effect", true);
			return;
		}

		if (ProjectPathUtils.isMusicFile(assetsMetadata, file)) {
			createDefaultView(AssetType.MUSIC, "Music");
			return;
		}

		if (ProjectPathUtils.isSoundFile(assetsMetadata, file)) {
			createDefaultView(AssetType.SOUND, "Sound");
			return;
		}

		if (ProjectPathUtils.isFragmentShader(file)) {
			createDefaultView(AssetType.FRAGMENT_SHADER, "Fragment Shader", true);
			return;
		}

		if (ProjectPathUtils.isVertexShader(file)) {
			createDefaultView(AssetType.VERTEX_SHADER, "Vertex Shader", true);
			return;
		}

		if (ProjectPathUtils.isScene(file)) {
			createDefaultView(AssetType.SCENE, "Scene", true);
			return;
		}

		support = findSupportForDirectory(file, relativePath);
		if (support != null) {
			ContentItemProperties item = support.getContentItemProperties(file, relativePath, ext);
			if (item != null) {
				createDefaultView(item.type, item.title, item.hideExtension);
				return;
			}
		}

		type = AssetType.UNKNOWN;
		name = new VisLabel(file.name());
	}

	private EditorEntitySupport findSupportForDirectory (FileHandle file, String relativePath) {
		for (EditorEntitySupport support : projectExtensionStorage.getEntitySupports()) {
			if (support.isSupportedDirectory(file, relativePath)) return support;
		}

		return null;
	}

	private void createDefaultView (String type, String itemTypeName) {
		createDefaultView(type, itemTypeName, false);
	}

	private void createDefaultView (String type, String itemTypeName, boolean hideExtension) {
		this.type = type;

		VisLabel assetTypeLabel = new VisLabel((hideExtension ? "" : file.extension().toUpperCase() + " ") + itemTypeName, Color.GRAY);
		assetTypeLabel.setWrap(true);
		assetTypeLabel.setAlignment(Align.center);
		add(assetTypeLabel).expandX().fillX().row();
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

	public boolean isMainFile () {
		return isMainFile;
	}

	public String getType () {
		return type;
	}

	public EditorEntitySupport getSupport () {
		return support;
	}

	public TextureRegion getRegion () {
		return region;
	}
}
