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

package com.kotcrab.vis.editor.ui.scene;

import com.artemis.EntityManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.*;
import com.kotcrab.vis.editor.event.assetreloaded.BmpFontReloadedEvent;
import com.kotcrab.vis.editor.event.assetreloaded.TtfFontReloadedEvent;
import com.kotcrab.vis.editor.event.bus.Event;
import com.kotcrab.vis.editor.event.bus.EventListener;
import com.kotcrab.vis.editor.module.ContentTable;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.ModuleContainer;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.editor.MenuBarModule;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.SceneTabsModule;
import com.kotcrab.vis.editor.module.project.SupportModule;
import com.kotcrab.vis.editor.module.scene.*;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.GroupBreadcrumb;
import com.kotcrab.vis.editor.plugin.ContainerExtension.ExtensionScope;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.ui.tab.CloseTabWhenMovingResources;
import com.kotcrab.vis.editor.ui.tabbedpane.DragAndDropTarget;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabViewMode;
import com.kotcrab.vis.editor.util.gdx.FocusUtils;
import com.kotcrab.vis.editor.util.gdx.VisValue;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Main tab for scene editor, allows to edit scene, holds and provides all features related to it. Uses it's own
 * {@link ModuleContainer} ({@link SceneModuleContainer})
 * @author Kotcrab
 */
public class SceneTab extends MainContentTab implements DragAndDropTarget, EventListener, SceneMenuButtonsListener, CloseTabWhenMovingResources {
	private EditorScene scene;

	@InjectModule private ExtensionStorageModule pluginContainer;
	@InjectModule private MenuBarModule menuBarModule;
	@InjectModule private StatusBarModule statusBarModule;
	@InjectModule private SceneTabsModule sceneTabs;
	@InjectModule private SceneIOModule sceneIOModule;

	private SceneModuleContainer sceneMC;

	@InjectModule private EntityManipulatorModule entityManipulator;
	@InjectModule private UndoModule undoModule;
	@InjectModule private CameraModule cameraModule;

	private EntityEngine engine;
	private EntityManager entityManager;
	private EntityProxyCache entityProxyCache;

	private ContentTable content;

	private SceneOutline outline;
	private boolean savedAtLeastOnce;

	private Target dropTarget;

	public SceneTab (EditorScene scene, ProjectModuleContainer projectMC) {
		super(true);
		this.scene = scene;

		sceneMC = new SceneModuleContainer(projectMC, this, scene, Editor.instance.getStage().getBatch());
		sceneMC.add(new CameraModule());
		sceneMC.add(new RendererModule());
		sceneMC.add(new UndoModule());
		sceneMC.add(new EntityManipulatorModule());
		sceneMC.addAll(sceneMC.findInHierarchy(ExtensionStorageModule.class).getContainersExtensions(SceneModule.class, ExtensionScope.SCENE));

		engine = sceneMC.getEntityEngine();

		for (EditorEntitySupport support : projectMC.get(SupportModule.class).getSupports()) {
			support.registerSystems(sceneMC, engine);
		}

		sceneMC.init();
		sceneMC.injectModules(this);

		entityManager = engine.getEntityManager();
		entityProxyCache = engine.getManager(EntityProxyCache.class);

		outline = new SceneOutline();

		VisTable leftColumn = new VisTable(false);
		VisTable rightColumn = new VisTable(false);

		leftColumn.top();
		rightColumn.top();

		content = new ContentTable(sceneMC);

		GroupBreadcrumb breadcrumb = entityManipulator.getGroupBreadcrumb();
		EntityProperties entityProperties = entityManipulator.getEntityProperties();

		content.add(breadcrumb).height(new VisValue(context -> breadcrumb.getPrefHeight())).expandX().fillX().colspan(3).row();
		content.add(leftColumn).width(300).fillY().expandY();
		content.add().fill().expand();
		content.add(rightColumn).width(260).fillY().expandY();

		leftColumn.top();
		//leftColumn.add(outline).height(300).fillX().expandX();
		leftColumn.row();
		leftColumn.add().fill().expand();

		rightColumn.top();
		rightColumn.add(entityProperties).height(new VisValue(context -> entityProperties.getPrefHeight())).expandX().fillX().row();
		rightColumn.add().fill().expand().row();
		rightColumn.add(entityManipulator.getLayersDialog()).expandX().fillX();

		dropTarget = new Target(content) {
			@Override
			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				entityManipulator.processDropPayload(payload);
			}

			@Override
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				return true;
			}
		};

		App.eventBus.register(this);
	}

	@Override
	public void render (Batch batch) {
		statusBarModule.setInfoLabelText(getInfoLabelText());

		Color oldColor = batch.getColor().cpy();
		batch.setColor(1, 1, 1, 1);
		batch.begin();

		sceneMC.render(batch);

		batch.end();
		batch.setColor(oldColor);
	}

	@Override
	public String getTabTitle () {
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
	public EntityEngine getEntityEngine () {
		return engine;
	}

	@Override
	public TabViewMode getViewMode () {
		return TabViewMode.SPLIT;
	}

	@Override
	public void onShow () {
		super.onShow();
		sceneMC.onShow();
		menuBarModule.setSceneButtonsListener(this);
		focusSelf();
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
		if (event instanceof TexturesReloadedEvent)
			sceneMC.getEntityEngine().getManager(TextureReloaderManager.class).reloadTextures();

		if (event instanceof ParticleReloadedEvent)
			sceneMC.getEntityEngine().getManager(ParticleReloaderManager.class).reloadParticles();

		if (event instanceof BmpFontReloadedEvent)
			sceneMC.getEntityEngine().getManager(FontReloaderManager.class).reloadFonts(true, false);

		if (event instanceof TtfFontReloadedEvent)
			sceneMC.getEntityEngine().getManager(FontReloaderManager.class).reloadFonts(false, true);

		if (isActiveTab()) {
			if (event instanceof ToolbarEvent) {
				ToolbarEventType type = ((ToolbarEvent) event).type;

				if (type == ToolbarEventType.FILE_SAVE)
					save();
			}

			if (event instanceof UndoEvent) {
				if (undoModule.getUndoSize() == 0 && savedAtLeastOnce == false)
					setDirty(false);
			}
		}

		return false;
	}

	@Override
	public boolean save () {
		super.save();
		scene.setSchemes(sceneMC.getEntityEngine().getManager(EntityProxyCache.class).getSchemes());
		try {

			if (sceneIOModule.save(scene)) {
				setDirty(false);
				sceneMC.save();
				savedAtLeastOnce = true;
				return true;
			} else
				DialogUtils.showErrorDialog(Editor.instance.getStage(), "Unknown error encountered while saving resource");

		} catch (Exception e) {
			Log.exception(e);
			DialogUtils.showErrorDialog(Editor.instance.getStage(), "Unknown error encountered while saving resource", e);
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
		Editor.instance.getStage().addActor(new SceneSettingsDialog(this).fadeIn());
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

	@Override
	public void group () {
		entityManipulator.groupSelection();
	}

	@Override
	public void ungroup () {
		entityManipulator.ungroupSelection();
	}

	public String getInfoLabelText () {
		return "Entities: " + entityManager.getActiveEntityCount() + " FPS: " + Gdx.graphics.getFramesPerSecond() + " Scene: " + scene.width + " x " + scene.height;
	}

	public void centerAround (int entityId) {
		EntityProxy entity = entityProxyCache.get(entityId);
		entityManipulator.findEntityBaseGroupAndSelect(entity);
		cameraModule.setPosition(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight() / 2);
	}

	public void focusSelf () {
		FocusUtils.focus(content);
	}

	@Override
	public void reopenSelfAfterAssetsUpdated () {
		save();
		sceneTabs.open(scene);
	}

	public SceneModuleContainer getSceneMC () {
		return sceneMC;
	}
}
