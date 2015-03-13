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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.project.AssetsManagerUIModule.FileItem;
import com.kotcrab.vis.editor.module.project.AssetsManagerUIModule.FileType;
import com.kotcrab.vis.editor.scene.ParticleObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.ui.tab.DragAndDropTarget;
import com.kotcrab.vis.ui.widget.VisLabel;

public class AssetDragAndDrop {
	private FontCacheModule fontCache;
	private ParticleCacheModule particleCache;
	private FileAccessModule fileAccess;

	private DragAndDrop dragAndDrop;
	private DragAndDropTarget dropTarget;

	public AssetDragAndDrop (FontCacheModule fontCache, ParticleCacheModule particleCache, FileAccessModule fileAccess) {
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
		if (item.type == FileType.TEXTURE) {
			dragAndDrop.addSource(new Source(item) {
				@Override
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					Payload payload = new Payload();

					payload.setObject(item.region);

					Image img = new Image(item.region);
					payload.setDragActor(img);

					float invZoom = 1.0f / dropTarget.getCameraZoom();
					img.setScale(invZoom);
					dragAndDrop.setDragActorPosition(-img.getWidth() * invZoom / 2, img.getHeight() - img.getHeight() * invZoom / 2);

					return payload;
				}
			});
		}

		if (item.type == FileType.TTF_FONT) {
			dragAndDrop.addSource(new Source(item) {
				@Override
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					Payload payload = new Payload();

					int size = FontCacheModule.DEFAULT_FONT_SIZE;
					EditorFont font = fontCache.get(item.file);
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

		if (item.type == FileType.BMP_FONT_FILE || item.type == FileType.BMP_FONT_TEXTURE) {
			dragAndDrop.addSource(new Source(item) {
				@Override
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					Payload payload = new Payload();

					FileHandle fontFile;

					if (item.type == FileType.BMP_FONT_FILE)
						fontFile = item.file;
					else
						fontFile = item.file.sibling(item.file.nameWithoutExtension() + ".fnt");

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

		if (item.type == FileType.PARTICLE_EFFECT) {
			dragAndDrop.addSource(new Source(item) {
				@Override
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					Payload payload = new Payload();

					ParticleObject obj = new ParticleObject(fileAccess.relativizeToAssetsFolder(item.file), particleCache.get(item.file));
					payload.setObject(obj);

					Label label = new VisLabel("New Particle Effect \n (drop on scene to add)");
					label.setAlignment(Align.center);
					payload.setDragActor(label);

					dragAndDrop.setDragActorPosition(-label.getWidth() / 2, label.getHeight() / 2);

					return payload;
				}
			});
		}

	}

	public void clear () {
		dragAndDrop.clear();
	}
}
