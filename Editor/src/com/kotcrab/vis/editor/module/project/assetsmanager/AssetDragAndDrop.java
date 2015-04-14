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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.scene.*;
import com.kotcrab.vis.editor.ui.tabbedpane.DragAndDropTarget;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.gdx.VisDropSource;
import com.kotcrab.vis.ui.widget.VisLabel;

public class AssetDragAndDrop {
	private FileAccessModule fileAccess;
	private TextureCacheModule textureCache;
	private FontCacheModule fontCache;
	private ParticleCacheModule particleCache;

	private DragAndDrop dragAndDrop;
	private DragAndDropTarget dropTarget;

	public AssetDragAndDrop (FileAccessModule fileAccess, TextureCacheModule textureCache, FontCacheModule fontCache, ParticleCacheModule particleCache) {
		this.textureCache = textureCache;
		this.fontCache = fontCache;
		this.particleCache = particleCache;
		this.fileAccess = fileAccess;

		dragAndDrop = new DragAndDrop();
		dragAndDrop.setKeepWithinStage(false);
		dragAndDrop.setDragTime(0);
	}

	public void setDropTarget (DragAndDropTarget dropTarget) {
		this.dropTarget = dropTarget;

	}

	public void rebuild (Array<Actor> actors) {
		if (dropTarget != null) {
			dragAndDrop.clear();

			for (Actor actor : actors)
				addSource((FileItem) actor);

			dragAndDrop.addTarget(dropTarget.getDropTarget());
		}
	}

	private void addSource (FileItem item) {
		if (item.getType() == FileType.TEXTURE) {
			dragAndDrop.addSource(new Source(item) {
				@Override
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					Payload payload = new Payload();
					String relativePath = fileAccess.relativizeToAssetsFolder(item.getFile());

					SpriteObject object = new SpriteObject(relativePath, textureCache.getRegion(relativePath), 0, 0);
					payload.setObject(object);

					Image img = new Image(item.getRegion());
					payload.setDragActor(img);

					float invZoom = 1.0f / dropTarget.getCameraZoom();
					img.setScale(invZoom);
					dragAndDrop.setDragActorPosition(-img.getWidth() * invZoom / 2, img.getHeight() - img.getHeight() * invZoom / 2);

					return payload;
				}
			});
		}

		if (item.getType() == FileType.TTF_FONT) {
			dragAndDrop.addSource(new Source(item) {
				@Override
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					Payload payload = new Payload();

					int size = FontCacheModule.DEFAULT_FONT_SIZE;
					EditorFont font = fontCache.get(item.getFile());
					BitmapFont bmpFont = font.get(size);

					TextObject text = new TextObject(font, bmpFont, FontCacheModule.DEFAULT_TEXT, size);
					payload.setObject(text);

					LabelStyle style = new LabelStyle(bmpFont, Color.WHITE);
					Label label = new VisLabel(FontCacheModule.DEFAULT_TEXT, style);
					payload.setDragActor(label);

					float invZoom = 1.0f / dropTarget.getCameraZoom();
					label.setFontScale(invZoom);
					dragAndDrop.setDragActorPosition(-label.getWidth() * invZoom / 2, label.getHeight() / 2);

					return payload;
				}
			});
		}

		if (item.getType() == FileType.BMP_FONT_FILE || item.getType() == FileType.BMP_FONT_TEXTURE) {
			dragAndDrop.addSource(new Source(item) {
				@Override
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					Payload payload = new Payload();

					FileHandle fontFile;

					if (item.getType() == FileType.BMP_FONT_FILE)
						fontFile = item.getFile();
					else
						fontFile = FileUtils.sibling(item.getFile(), "fnt");

					BMPEditorFont font = (BMPEditorFont) fontCache.get(fontFile);

					TextObject text = new TextObject(font, FontCacheModule.DEFAULT_TEXT);
					payload.setObject(text);

					LabelStyle style = new LabelStyle(font.get(), Color.WHITE);
					Label label = new VisLabel(FontCacheModule.DEFAULT_TEXT, style);
					payload.setDragActor(label);

					float invZoom = 1.0f / dropTarget.getCameraZoom();
					label.setFontScale(invZoom);
					dragAndDrop.setDragActorPosition(-label.getWidth() * invZoom / 2, label.getHeight() / 2);

					return payload;
				}
			});
		}

		if (item.getType() == FileType.PARTICLE_EFFECT) {
			dragAndDrop.addSource(new VisDropSource(dragAndDrop, item).defaultView("New Particle Effect \n (drop on scene to add)")
					.setObjectProvider(() -> new ParticleObject(fileAccess.relativizeToAssetsFolder(item.getFile()), particleCache.get(item.getFile()))));
		}

		if (item.getType() == FileType.MUSIC) {
			dragAndDrop.addSource(new VisDropSource(dragAndDrop, item).defaultView("New Music \n (drop on scene to add)").disposeOnNullTarget()
					.setObjectProvider(() -> new MusicObject(fileAccess.relativizeToAssetsFolder(item.getFile()), Gdx.audio.newMusic(item.getFile()))));
		}

		if (item.getType() == FileType.SOUND) {
			dragAndDrop.addSource(new VisDropSource(dragAndDrop, item).defaultView("New Sound \n (drop on scene to add)").disposeOnNullTarget()
					.setObjectProvider(() -> new SoundObject(fileAccess.relativizeToAssetsFolder(item.getFile()), Gdx.audio.newSound(item.getFile()))));
		}

		if (item.getType() == FileType.NON_STANDARD) {
			dragAndDrop.addSource(item.getSupport().createDropSource(dragAndDrop, item.getFile()));
		}
	}

	public void clear () {
		dragAndDrop.clear();
	}
}
