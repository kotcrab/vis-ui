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

import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.kotcrab.vis.editor.assets.AssetFileType;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.scheme.SpriterAssetData;
import com.kotcrab.vis.editor.ui.tabbedpane.DragAndDropTarget;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.scene2d.VisDragAndDrop;
import com.kotcrab.vis.editor.util.scene2d.VisDropSource;
import com.kotcrab.vis.runtime.assets.*;
import com.kotcrab.vis.ui.widget.VisLabel;

/**
 * Assets manager drag and drop helper class.
 * @author Kotcrab
 */
public class AssetDragAndDrop implements Disposable {
	private FileAccessModule fileAccess;
	private TextureCacheModule textureCache;
	private FontCacheModule fontCache;
	private ParticleCacheModule particleCache;
	private SpriterDataIOModule spriterDataIO;

	private VisDragAndDrop dragAndDrop;
	private DragAndDropTarget dropTarget;

	public AssetDragAndDrop (ModuleInjector injector) {
		injector.injectModules(this);

		dragAndDrop = new VisDragAndDrop(injector);
		dragAndDrop.setKeepWithinStage(false);
		dragAndDrop.setDragTime(0);
	}

	public void setDropTarget (DragAndDropTarget dropTarget) {
		this.dropTarget = dropTarget;
	}

	public void rebuild (Array<Actor> actors, Values<TextureAtlasViewTab> atlasesViews) {
		if (dropTarget != null) {
			dragAndDrop.clear();

			for (Actor actor : actors)
				addSource((FileItem) actor);

			dragAndDrop.addTarget(dropTarget.getDropTarget());
		}

		for (TextureAtlasViewTab view : atlasesViews) {
			Array<AtlasItem> items = view.getItems();

			for (AtlasItem item : items)
				addAtlasSource(item);
		}
	}

	public void addSources (Array<AtlasItem> items) {
		for (AtlasItem item : items)
			addAtlasSource(item);
	}

	private void addAtlasSource (AtlasItem item) {
		dragAndDrop.addSource(new Source(item) {
			@Override
			public Payload dragStart (InputEvent event, float x, float y, int pointer) {
				return createTexturePayload(item.getRegion(), item.getAtlasAsset());
			}
		});
	}

	private void addSource (FileItem item) {
		String relativePath = fileAccess.relativizeToAssetsFolder(item.getFile());

		if (item.getType() == AssetFileType.TEXTURE) {
			dragAndDrop.addSource(new Source(item) {
				@Override
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					TextureRegionAsset asset = new TextureRegionAsset(fileAccess.relativizeToAssetsFolder(item.getFile()));
					return createTexturePayload(item.getRegion(), asset);
				}
			});
		}

		if (item.getType() == AssetFileType.TTF_FONT) {
			dragAndDrop.addSource(new Source(item) {
				@Override
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					Payload payload = new Payload();

					TtfFontAsset asset = new TtfFontAsset(fileAccess.relativizeToAssetsFolder(item.getFile()), FontCacheModule.DEFAULT_FONT_SIZE);
					payload.setObject(asset);

					BitmapFont font = fontCache.get(asset, 1);

					LabelStyle style = new LabelStyle(font, Color.WHITE);
					Label label = new VisLabel(FontCacheModule.DEFAULT_TEXT, style);
					payload.setDragActor(label);

					float invZoom = 1.0f / dropTarget.getCameraZoom();
					label.setFontScale(invZoom);
					dragAndDrop.setDragActorPosition(-label.getWidth() * invZoom / 2, label.getHeight() / 2);

					return payload;
				}
			});
		}

		if (item.getType() == AssetFileType.BMP_FONT_FILE || item.getType() == AssetFileType.BMP_FONT_TEXTURE) {
			dragAndDrop.addSource(new Source(item) {
				@Override
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					Payload payload = new Payload();

					FileHandle fontFile;

					if (item.getType() == AssetFileType.BMP_FONT_FILE)
						fontFile = item.getFile();
					else
						fontFile = FileUtils.sibling(item.getFile(), "fnt");

					BmpFontAsset asset = new BmpFontAsset(fileAccess.relativizeToAssetsFolder(fontFile), new BitmapFontParameter());

					payload.setObject(asset);

					LabelStyle style = new LabelStyle(fontCache.get(asset, 1), Color.WHITE);
					Label label = new VisLabel(FontCacheModule.DEFAULT_TEXT, style);
					payload.setDragActor(label);

					float invZoom = 1.0f / dropTarget.getCameraZoom();
					label.setFontScale(invZoom);
					dragAndDrop.setDragActorPosition(-label.getWidth() * invZoom / 2, label.getHeight() / 2);

					return payload;
				}
			});
		}

		if (item.getType() == AssetFileType.PARTICLE_EFFECT) {
			dragAndDrop.addSource(new VisDropSource(dragAndDrop, item).defaultView("New Particle Effect \n (drop on scene to add)").setPayload(new PathAsset(relativePath)));
		}

		if (item.getType() == AssetFileType.MUSIC) {
			dragAndDrop.addSource(new VisDropSource(dragAndDrop, item).defaultView("New Music \n (drop on scene to add)").setPayload(new PathAsset(relativePath)));
		}

		if (item.getType() == AssetFileType.SOUND) {
			dragAndDrop.addSource(new VisDropSource(dragAndDrop, item).defaultView("New Sound \n (drop on scene to add)").setPayload(new PathAsset(relativePath)));
		}

		if (item.getType() == AssetFileType.SPRITER_SCML) {
			FileHandle dataFile = item.getFile().parent().child(".vis").child("data.json");
			if (dataFile.exists() == false) return;
			SpriterAssetData data = spriterDataIO.loadData(dataFile);
			dragAndDrop.addSource(new VisDropSource(dragAndDrop, item).defaultView("New Spriter Animation \n (drop on scene to add)").setPayload(new SpriterAsset(relativePath, data.imageScale)));
		}

		if (item.getType() == AssetFileType.NON_STANDARD) {
			dragAndDrop.addSource(item.getSupport().createDropSource(dragAndDrop, item));
		}
	}

	private Payload createTexturePayload (TextureRegion region, TextureAssetDescriptor asset) {
		Payload payload = new Payload();

		payload.setObject(asset);

		//image creation
		Image img = new Image(region);
		payload.setDragActor(img);

		float invZoom = 1.0f / dropTarget.getCameraZoom();
		img.setScale(invZoom);
		dragAndDrop.setDragActorPosition(-img.getWidth() * invZoom / 2, img.getHeight() - img.getHeight() * invZoom / 2);

		return payload;

	}

	public void clear () {
		dragAndDrop.clear();
	}

	@Override
	public void dispose () {
		dragAndDrop.dispose();
	}
}
