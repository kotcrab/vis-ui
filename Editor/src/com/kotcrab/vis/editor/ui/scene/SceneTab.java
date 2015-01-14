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

package com.kotcrab.vis.editor.ui.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.*;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.module.scene.*;
import com.kotcrab.vis.editor.ui.tab.DragAndDropTarget;
import com.kotcrab.vis.editor.ui.tab.Tab;
import com.kotcrab.vis.editor.ui.tab.TabViewMode;
import com.kotcrab.vis.ui.VisTable;

public class SceneTab extends Tab implements DragAndDropTarget, EventListener {
	private EditorScene scene;

	private TextureCacheModule cacheModule;
	private SceneIOModule sceneIOModule;

	private SceneModuleContainer sceneMC;
	private CameraModule cameraModule;

	private VisTable content;

	private SceneOutline outline;
	private ActorProperties actorProperties;

	private Target dropTarget;

	private boolean dirty;

	public SceneTab (EditorScene scene, ProjectModuleContainer projectMC) {
		this.scene = scene;

		cacheModule = projectMC.get(TextureCacheModule.class);
		sceneIOModule = projectMC.get(SceneIOModule.class);

		sceneMC = new SceneModuleContainer(projectMC, scene);
		sceneMC.add(new CameraModule());
		sceneMC.add(new GridRendererModule());
		sceneMC.add(new RendererModule());
		sceneMC.add(new ObjectManagerModule());
		sceneMC.add(new ObjectManipulatorModule());
		sceneMC.init();

		cameraModule = sceneMC.get(CameraModule.class);

		outline = new SceneOutline();
		actorProperties = new ActorProperties();

		content = new ContentTable();

		VisTable leftColumn = new VisTable(false);
		VisTable rightColumn = new VisTable(false);

		leftColumn.top();
		rightColumn.top();

		// dummy widgets allows content table to get input events
		content.add(leftColumn).width(300).fillY().expandY();
		content.add(new Widget()).fill().expand();
		content.add(rightColumn).width(300).fillY().expandY();

		leftColumn.top();
		//leftColumn.add(outline).height(300).fillX().expandX();
		leftColumn.row();
		leftColumn.add(new Widget()).fill().expand();

		rightColumn.top();
		rightColumn.add(actorProperties).height(300).expandX().fillX();
		rightColumn.row();
		rightColumn.add(new Widget()).fill().expand();

		dropTarget = new Target(content) {
			@Override
			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				dropped(payload);
			}

			@Override
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				return true;
			}
		};
	}

	private void dropped (Payload payload) {
		TextureRegion region = (TextureRegion) payload.getObject();

		Sprite sprite = new Sprite(region);
		float x = cameraModule.getInputX() - sprite.getWidth() / 2;
		float y = cameraModule.getInputY() - sprite.getHeight() / 2;

		scene.objects.add(new Object2d(cacheModule.getRelativePath(region), region, x, y));
		setDirty(true);
	}

	private void resize () {
		sceneMC.resize();
	}

	@Override
	public void render (Batch batch) {
		Color oldColor = batch.getColor().cpy();
		batch.setColor(1, 1, 1, 1);
		batch.begin();

		sceneMC.render(batch);

		batch.end();
		batch.setColor(oldColor);
	}

	@Override
	public String getButtonText () {
		String title;

		if (isDirty())
			title = "*";
		else
			title = "";

		return title + scene.getFile().name();
	}

	@Override
	public Table getContentTable () {
		return content;
	}

	@Override
	public Target getDropTarget () {
		return dropTarget;
	}

	@Override
	public float getCameraZoom () {
		return cameraModule.getZoom();
	}

	@Override
	public TabViewMode getViewMode () {
		return TabViewMode.WITH_PROJECT_ASSETS_MANAGER;
	}

	@Override
	public void onHide () {
		super.onHide();
		App.eventBus.unregister(this);
	}

	@Override
	public void onShow () {
		super.onShow();
		App.eventBus.register(this);
	}

	public EditorScene getScene () {
		return scene;
	}

	public boolean isDirty () {
		return dirty;
	}

	public void setDirty (boolean dirty) {
		this.dirty = dirty;
		getPane().updateTabTitle(this);
	}

	@Override
	public boolean onEvent (Event event) {
		if (event instanceof MenuEvent) {
			MenuEventType type = ((MenuEvent) event).type;

			if (type == MenuEventType.FILE_SAVE)
				if (sceneIOModule.save(scene)) setDirty(false);
		}

		if (event instanceof TexturesReloadedEvent) {
			for (SceneObject object : scene.objects) {
				if (object instanceof Object2d) {
					Object2d object2d = (Object2d) object;
					object2d.sprite.setRegion(cacheModule.getRegion(object2d.regionRelativePath));
				}
			}

		}

		return false;
	}

	private class ContentTable extends VisTable {
		public ContentTable () {
			super(false);
			addListener(new SceneInputListener(this, sceneMC));
		}

		@Override
		protected void sizeChanged () {
			super.sizeChanged();
			sceneMC.resize();
			resize();
		}
	}
}
