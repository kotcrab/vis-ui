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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.event.MenuEvent;
import com.kotcrab.vis.editor.event.MenuEventType;
import com.kotcrab.vis.editor.event.TexturesReloadedEvent;
import com.kotcrab.vis.editor.module.MenuBarModule;
import com.kotcrab.vis.editor.module.StatusBarModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.module.scene.CameraModule;
import com.kotcrab.vis.editor.module.scene.EntityManipulatorModule;
import com.kotcrab.vis.editor.module.scene.GridRendererModule;
import com.kotcrab.vis.editor.module.scene.RendererModule;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.UndoableAction;
import com.kotcrab.vis.editor.module.scene.ZIndexManipulator;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.SpriteObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.ui.tab.DragAndDropTarget;
import com.kotcrab.vis.editor.ui.tab.Tab;
import com.kotcrab.vis.editor.ui.tab.TabViewMode;
import com.kotcrab.vis.editor.util.SpriteUtils;
import com.kotcrab.vis.ui.VisTable;

public class SceneTab extends Tab implements DragAndDropTarget, EventListener, Disposable, SceneMenuButtonsListener {
	private Editor editor;
	private EditorScene scene;

	private MenuBarModule menuBarModule;
	private StatusBarModule statusBarModule;

	private TextureCacheModule cacheModule;
	private SceneIOModule sceneIOModule;
	private EntityManipulatorModule entityManipulatorModule;

	private SceneModuleContainer sceneMC;
	private UndoModule undoModule;
	private CameraModule cameraModule;

	private ContentTable content;

	private SceneOutline outline;

	private Target dropTarget;

	public SceneTab (EditorScene scene, ProjectModuleContainer projectMC) {
		super(true);
		editor = Editor.instance;
		this.scene = scene;

		menuBarModule = projectMC.getEditorContainer().get(MenuBarModule.class);
		statusBarModule = projectMC.getEditorContainer().get(StatusBarModule.class);

		cacheModule = projectMC.get(TextureCacheModule.class);
		sceneIOModule = projectMC.get(SceneIOModule.class);

		sceneMC = new SceneModuleContainer(projectMC, this, scene);
		sceneMC.add(cameraModule = new CameraModule());
		sceneMC.add(new GridRendererModule());
		sceneMC.add(new RendererModule());

		sceneMC.add(undoModule = new UndoModule());
		sceneMC.add(new ZIndexManipulator());
		sceneMC.add(entityManipulatorModule = new EntityManipulatorModule());
		sceneMC.init();

		outline = new SceneOutline();

		VisTable leftColumn = new VisTable(false);
		VisTable rightColumn = new VisTable(false);

		leftColumn.top();
		rightColumn.top();

		content = new ContentTable();
		content.setTouchable(Touchable.enabled);

		content.add(leftColumn).width(300).fillY().expandY();
		content.add().fill().expand();
		content.add(rightColumn).width(245).fillY().expandY();

		leftColumn.top();
		//leftColumn.add(outline).height(300).fillX().expandX();
		leftColumn.row();
		leftColumn.add().fill().expand();

		rightColumn.top();
		rightColumn.add(sceneMC.get(EntityManipulatorModule.class).getEntityProperties()).expandX().fillX();
		rightColumn.row();
		rightColumn.add().fill().expand();

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

		App.eventBus.register(this);
	}

	private void dropped (Payload payload) {
		Object payloadObject = payload.getObject();

		if (payloadObject instanceof TextureRegion) {
			TextureRegion region = (TextureRegion) payload.getObject();

			Sprite sprite = new Sprite(region);
			float x = cameraModule.getInputX() - sprite.getWidth() / 2;
			float y = cameraModule.getInputY() - sprite.getHeight() / 2;

			final SpriteObject object = new SpriteObject(cacheModule.getRelativePath(region), region, x, y);

			undoModule.execute(new EntityAddedAction(object));
		}

		if (payloadObject instanceof TextObject) {
			TextObject text = (TextObject) payloadObject;
			float x = cameraModule.getInputX() - text.getWidth() / 2;
			float y = cameraModule.getInputY() - text.getHeight() / 2;
			text.setPosition(x, y);

			undoModule.execute(new EntityAddedAction(text));
		}
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
		return scene.getFile().name();
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
	public void onShow () {
		super.onShow();
		sceneMC.onShow();
		menuBarModule.setSceneButtonsListener(this);
		statusBarModule.setInfoLabelText("Scene: " + scene.width + " x " + scene.height);
	}

	@Override
	public void onHide () {
		super.onHide();
		sceneMC.onHide();
		menuBarModule.setSceneButtonsListener(null);
		statusBarModule.setInfoLabelText("");
	}

	public EditorScene getScene () {
		return scene;
	}

	@Override
	public boolean onEvent (Event event) {
		if (isActiveTab()) {
			if (event instanceof MenuEvent) {
				MenuEventType type = ((MenuEvent) event).type;

				if (type == MenuEventType.FILE_SAVE)
					save();
			}
		}

		if (event instanceof TexturesReloadedEvent) {
			for (EditorEntity object : scene.entities) {
				if (object instanceof SpriteObject) {
					SpriteObject spriteObject = (SpriteObject) object;
					SpriteUtils.setRegion(spriteObject.getSprite(), cacheModule.getRegion(spriteObject.getRegionRelativePath()));
				}
			}
		}

		return false;
	}

	@Override
	public boolean save () {
		if (sceneIOModule.save(scene)) {
			setDirty(false);
			sceneMC.save();
			return true;
		}

		return false;
	}

	@Override
	public void dispose () {
		sceneMC.dispose();
		App.eventBus.unregister(this);
	}

	@Override
	public void showSceneSettings () {
		editor.getStage().addActor(new SceneSettingsDialog(this).fadeIn());
	}

	@Override
	public void resetCamera () {
		cameraModule.reset();
	}

	@Override
	public void resetCameraZoom () {
		cameraModule.resetZoom();
	}

	@Override
	public void undo () {
		undoModule.undo();
	}

	@Override
	public void redo () {
		undoModule.redo();
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

	private class EntityAddedAction implements UndoableAction {
		private EditorEntity entity;

		public EntityAddedAction (EditorEntity entity) {
			this.entity = entity;
		}

		@Override
		public void execute () {
			scene.entities.add(entity);
			entityManipulatorModule.select(entity);
			dirty();
		}

		@Override
		public void undo () {
			scene.entities.removeValue(entity, true);
			entityManipulatorModule.resetSelection();
			dirty();
		}
	}
}
