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

package com.kotcrab.vis.editor.module.scene.entitymanipulator;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.*;
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.entity.EntityScheme.UUIDPolicy;
import com.kotcrab.vis.editor.entity.ExporterDropsComponent;
import com.kotcrab.vis.editor.entity.PixelsPerUnit;
import com.kotcrab.vis.editor.entity.VisUUID;
import com.kotcrab.vis.editor.event.ToolSwitchedEvent;
import com.kotcrab.vis.editor.event.UndoableModuleEvent;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.module.editor.*;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.module.scene.CameraModule;
import com.kotcrab.vis.editor.module.scene.RendererModule;
import com.kotcrab.vis.editor.module.scene.SceneModule;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.action.*;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.*;
import com.kotcrab.vis.editor.module.scene.system.EntitiesCollector;
import com.kotcrab.vis.editor.module.scene.system.EntityProxyCache;
import com.kotcrab.vis.editor.module.scene.system.GroupIdProviderSystem;
import com.kotcrab.vis.editor.module.scene.system.ZIndexManipulator;
import com.kotcrab.vis.editor.module.scene.system.render.GridRendererSystem.GridSettingsModule;
import com.kotcrab.vis.editor.plugin.api.EditorEntitySupport;
import com.kotcrab.vis.editor.plugin.api.ToolProvider;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.EditorLayer;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.dialog.SelectLayerDialog;
import com.kotcrab.vis.editor.ui.scene.GroupBreadcrumb;
import com.kotcrab.vis.editor.ui.scene.GroupBreadcrumb.GroupBreadcrumbListener;
import com.kotcrab.vis.editor.ui.scene.LayersDialog;
import com.kotcrab.vis.editor.ui.scene.SceneOutline;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.ui.toast.DetailsToast;
import com.kotcrab.vis.editor.util.Holder;
import com.kotcrab.vis.editor.util.gdx.DummyMusic;
import com.kotcrab.vis.editor.util.gdx.RepeatableTimedMove;
import com.kotcrab.vis.editor.util.scene2d.MenuUtils;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;
import com.kotcrab.vis.editor.util.vis.CreatePointPayload;
import com.kotcrab.vis.editor.util.vis.EditorRuntimeException;
import com.kotcrab.vis.runtime.assets.*;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.entity.composer.EntityComposer;
import com.kotcrab.vis.runtime.util.entity.composer.ParticleEntityComposer;
import com.kotcrab.vis.runtime.util.entity.composer.SpriteEntityComposer;
import com.kotcrab.vis.runtime.util.entity.composer.TextEntityComposer;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisTable;
import kotlin.Unit;

/** @author Kotcrab */
@EventBusSubscriber
public class EntityManipulatorModule extends SceneModule {
	private static final int NO_GROUP_SET = -1;

	private StatusBarModule statusBar;
	private ToastModule toastModule;
	private ToolbarModule toolbarModule;
	private EditingSettingsModule editingSettings;
	private GridSettingsModule gridSettings;
	private ExtensionStorageModule extensionStorage;
	private ClonerModule cloner;

	private SceneIOModule sceneIO;
	private ProjectExtensionStorageModule projectExtensionStorage;

	private CameraModule camera;
	private UndoModule undoModule;
	private TextureCacheModule textureCache;
	private ParticleCacheModule particleCache;
	private FontCacheModule fontCache;
	private RendererModule rendererModule;

	private Stage stage;

	private ComponentMapper<VisGroup> groupCm;
	private EntityComposer entityComposer;

	private EntityProxyCache entityProxyCache;
	private ZIndexManipulator zIndexManipulator;
	private EntitiesCollector entitiesCollector;
	private GroupIdProviderSystem groupIdProvider;
	private RenderBatchingSystem renderBatchingSystem;

	private ShapeRenderer shapeRenderer;

	//TODO [misc] create common class for scene ui dialogs
	private EntityProperties entityProperties;
	private GroupBreadcrumb groupBreadcrumb;
	private LayersDialog layersDialog;
	private AlignmentToolsDialog alignmentToolsDialog;
	private SceneOutline sceneOutline;

	private Tool currentTool;
	private ObjectMap<String, Tool> tools = new ObjectMap<>();

	private EntitiesSelection entitiesSelection;

	private float copyAttachX, copyAttachY;
	private Array<EntityScheme> entitiesClipboard = new Array<>();

	private boolean mouseDragged;
	/** popup menu position in scene cords system */
	private float menuX, menuY;
	private PopupMenu generalPopupMenu;
	private PopupMenu entityPopupMenu;

	private RepeatableTimedMove entityMoveTimerTask;
	private MoveEntitiesAction keyMoveAction;

	private VisTable toolPropertiesContainer;

	@Override
	public void init () {
		shapeRenderer = rendererModule.getShapeRenderer();

		entityProperties = new EntityProperties(sceneContainer, sceneTab);
		groupBreadcrumb = new GroupBreadcrumb(new GroupBreadcrumbListener() {
			@Override
			public void clicked (int gid) {
				entitiesSelection = new EntitiesSelection(entitiesCollector, scene.getActiveLayerId(), gid);
				selectAll();
			}

			@Override
			public void rootClicked () {
				hardSelectionReset();
			}
		});
		layersDialog = new LayersDialog(sceneTab, sceneContainer);
		alignmentToolsDialog = new AlignmentToolsDialog(sceneContainer);
		sceneOutline = new SceneOutline(sceneContainer);
		createContextMenus();

		toolPropertiesContainer = new VisTable();

		entityMoveTimerTask = new RepeatableTimedMove(stage, scene.pixelsPerUnit,
				() -> scene.getActiveLayer().locked,
				(deltaX, deltaY) -> {
					if (keyMoveAction == null) keyMoveAction = new MoveEntitiesAction(this);
					for (EntityProxy entity : getSelectedEntities()) {
						entity.setPosition(entity.getX() + deltaX, entity.getY() + deltaY);
					}
					selectedEntitiesChanged();
					return Unit.INSTANCE;
				});
		entityMoveTimerTask.setOnCancel(() -> {
			if (keyMoveAction != null) {
				keyMoveAction.saveNewData();
				undoModule.add(keyMoveAction);
				keyMoveAction = null;
			}
			return Unit.INSTANCE;
		});

		for (ToolProvider<?> provider : extensionStorage.getToolProviders()) {
			Tool tool = provider.createTool();
			tools.put(tool.getToolId(), tool);
		}

		entitiesSelection = new EntitiesSelection(entitiesCollector, scene.getActiveLayerId());

		for (Tool tool : tools.values()) {
			tool.setModules(sceneContainer, scene);
		}

		//switch to SelectionTool or default to first available tool
		switchTool(tools.get(toolbarModule.getActiveToolId(), tools.values().toArray().first()));

		scene.addObservable(notificationId -> {
			if (notificationId == EditorScene.ACTIVE_LAYER_CHANGED) {
				hardSelectionReset();
			}
		});
	}

	@Override
	public void setEntityEngine (EntityEngine entityEngine) {
		super.setEntityEngine(entityEngine);
		entityComposer = new EntityComposer(entityEngine, scene.pixelsPerUnit);
	}

	private void createContextMenus () {
		entityPopupMenu = new PopupMenu();

		generalPopupMenu = new PopupMenu();
		generalPopupMenu.addItem(MenuUtils.createMenuItem("Paste", this::paste));
		generalPopupMenu.addItem(MenuUtils.createMenuItem("Select All", this::selectAll));
	}

	private void buildSelectedEntitiesPopupMenu () {
		entityPopupMenu.clearChildren();

		if (entitiesSelection.isEnterIntoGroupValid()) {
			entityPopupMenu.addItem(MenuUtils.createMenuItem("Enter into Group", () -> {

				if (entitiesSelection.isEnterIntoGroupValid() == false) {
					Dialogs.showErrorDialog(stage, "Group was deselected");
					return;
				}

				int newSelectionGid = entitiesSelection.getSelection().first().getGroupIdBefore(entitiesSelection.getGroupId());
				groupBreadcrumb.addGroup(newSelectionGid);
				entitiesSelection = new EntitiesSelection(entitiesCollector, scene.getActiveLayerId(), newSelectionGid);
				selectAll();
			}));
			entityPopupMenu.addSeparator();
		}

		if (scene.getLayers().size() > 1) {
			entityPopupMenu.addItem(MenuUtils.createMenuItem("Move to Layer", () -> moveToLayer(true)));
			entityPopupMenu.addSeparator();
		}

		entityPopupMenu.addItem(MenuUtils.createMenuItem("Cut", this::cut));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Copy", this::copy));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Paste", this::paste));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Duplicate", this::duplicate));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Remove", this::deleteSelectedEntities));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Select All", this::selectAll));
	}

	private void moveToLayer (boolean showGroupMoveWarning) {
		if (showGroupMoveWarning && entitiesSelection.getGroupId() != -1) {
			Dialogs.showOptionDialog(stage, "Warning", "This will move whole group (including parent groups) to another layer.",
					OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
						@Override
						public void yes () {
							moveToLayer(false);
						}
					}).setYesButtonText("Continue");
			return;
		}

		Array<EntityProxy> targetEntities;

		IntArray groupIds = entitiesSelection.getSelection().first().getGroupsIds();
		if (groupIds.size != 0) {
			targetEntities = entitiesCollector.collect(scene.getActiveLayerId(), groupIds.peek());
		} else {
			targetEntities = new Array<>();
			targetEntities.addAll(entitiesSelection.getSelection().toArray());
		}

		stage.addActor(new SelectLayerDialog(scene.getLayers(), scene.getActiveLayer(), result -> {
			undoModule.execute(new ChangeEntitiesLayerAction(renderBatchingSystem, this, targetEntities, result.id));

			//reselect entities again
			hardSelectionReset();
			for (EntityProxy proxy : targetEntities) {
				selectAppend(proxy);
			}
		}).fadeIn());
	}

	private void copy () {
		if (entitiesSelection.size() > 0) {
			entitiesClipboard.clear();
			entitiesSelection.forEachEntity(entity -> entitiesClipboard.add(EntityScheme.clonedOf(entity, cloner.getCloner())));

			EntityProxy proxy = entitiesSelection.peek();

			if (entityPopupMenu.getParent() != null) { //is menu visible
				copyAttachX = menuX - proxy.getX();
				copyAttachY = menuY - proxy.getY();
			} else {
				copyAttachX = proxy.getWidth() / 2;
				copyAttachY = proxy.getHeight() / 2;
			}
		} else
			statusBar.setText("Nothing to copy!");
	}

	private void paste () {
		paste(true);
	}

	private void paste (boolean changePastePosition) {
		if (entitiesClipboard.size > 0) {
			silentSelectionReset();

			Array<EntityProxy> proxies = new Array<>(entitiesSelection.size());
			ObjectSet<Entity> entities = new ObjectSet<>(entitiesSelection.size());

			IntIntMap groupIdRemap = new IntIntMap();
			Holder<Integer> freeGidHolder = Holder.of(groupIdProvider.getFreeGroupId());

			entitiesClipboard.forEach(protoEntity -> {
				Entity entity = protoEntity.build(entityEngine, cloner.getCloner(), UUIDPolicy.ASSIGN_NEW);
				entities.add(entity);
				if (scene.getActiveLayer().visible == false) entity.edit().add(new Invisible());
				sceneContainer.updateEntitiesStates();

				proxies.add(entityProxyCache.get(entity));

				VisGroup groups = groupCm.get(entity);

				if (groups != null) {
					for (int i = 0; i < groups.groupIds.size; i++) {
						int gid = groups.groupIds.get(i);

						if (groupBreadcrumb.isInHierarchy(gid))
							continue;

						int remapToGid = groupIdRemap.get(gid, Integer.MIN_VALUE);
						if (remapToGid == Integer.MIN_VALUE) {
							remapToGid = freeGidHolder.value;
							groupIdRemap.put(gid, freeGidHolder.value);
							freeGidHolder.value += 1;
						}

						groups.groupIds.set(i, remapToGid);
					}
				}
			});

			if (changePastePosition) {
				float x = camera.getInputX();
				float y = camera.getInputY();

				EntityProxy baseProxy = proxies.peek();
				float xOffset = baseProxy.getX();
				float yOffset = baseProxy.getY();

				for (EntityProxy proxy : proxies) {
					float px = x - copyAttachX + (proxy.getX() - xOffset);
					float py = y - copyAttachY + (proxy.getY() - yOffset);

					proxy.setPosition(px, py);
					proxy.setLayerId(scene.getActiveLayerId());
				}
			} else {
				for (EntityProxy proxy : proxies) {
					proxy.setLayerId(scene.getActiveLayerId());
				}
			}

			undoModule.add(new EntitiesAddedAction(sceneContainer, entityEngine, entities));

			sceneOutline.rebuildOutline();
			selectedEntitiesChanged();
		} else
			statusBar.setText("Nothing to paste!");
	}

	private void cut () {
		copy();
		deleteSelectedEntities();
	}

	private void duplicate () {
		copy();
		paste(false);
		statusBar.setText("Selection duplicated");
	}

	private void deleteSelectedEntities () {
		ObjectSet<Entity> entities = new ObjectSet<>();

		entitiesSelection.forEachEntity(entity -> entities.addAll(entity));
		softSelectionReset();

		undoModule.execute(new EntitiesRemovedAction(sceneContainer, entityEngine, entities));
	}

	public void processDropPayload (Object payload) {
		if (scene.getActiveLayer().locked) {
			statusBar.setText("Layer is locked!");
			return;
		}

		Holder<Boolean> setEntityPosToMouse = Holder.of(true);

		boolean updatePositionLater = false;

		Entity entity = null;

		//TODO: refactor this

		if (payload instanceof CreatePointPayload) {
			CreatePointPayload pointPayload = (CreatePointPayload) payload;
			if (pointPayload.centerPosAfterCreation) setEntityPosToMouse.value = false;

			entity = new EntityBuilder(entityEngine)
					.with(new Point(), new Transform())
					.with(new Renderable(0), new Layer(scene.getActiveLayerId()))
					.with(new ExporterDropsComponent(Renderable.class, Layer.class))
					.build();

		} else if (payload instanceof TextureAssetDescriptor) {
			TextureAssetDescriptor asset = (TextureAssetDescriptor) payload;

			SpriteEntityComposer composer = entityComposer.sprite(textureCache.getRegion(asset), 0, 0);
			composer.getOrigin().setOrigin(composer.getSprite().getWidth() / 2, composer.getSprite().getHeight() / 2);
			composer.getLayer().layerId = scene.getActiveLayerId();
			composer.setAssetDescriptor(asset);
			entity = composer.finish();
		} else if (payload instanceof BmpFontAsset || payload instanceof TtfFontAsset) {
			VisAssetDescriptor asset = (VisAssetDescriptor) payload;

			TextEntityComposer composer = entityComposer.text(fontCache.getGeneric(asset, scene.pixelsPerUnit), FontCacheModule.DEFAULT_TEXT, 0, 0);
			composer.getLayer().layerId = scene.getActiveLayerId();
			composer.setAssetDescriptor(asset);
			entity = composer.finish();
			entity.edit()
					.add(new PixelsPerUnit(scene.pixelsPerUnit))
					.add(new ExporterDropsComponent(PixelsPerUnit.class));

			updatePositionLater = true; //update position later after text bounds has been calculated

			//TODO: [misc] workaround, before text is updated it is rendered at 0, 0 which is visible at one frome, just move it outside camera for now
			entity.getComponent(Transform.class).setPosition(camera.getInputX() - 1000000, camera.getInputY() - 10000);
		} else if (payload instanceof ParticleAsset) {
			ParticleAsset asset = (ParticleAsset) payload;
			float scale = 1f / scene.pixelsPerUnit;

			try {
				ParticleEntityComposer composer = entityComposer.particle(particleCache.get(asset, scale), 0, 0);
				composer.getLayer().layerId = scene.getActiveLayerId();
				composer.setAssetDescriptor(asset);
				entity = composer.finish();

				entity.edit()
						.add(new PixelsPerUnit(scene.pixelsPerUnit))
						.add(new ExporterDropsComponent(PixelsPerUnit.class));
			} catch (EditorRuntimeException e) {
				Log.exception(e);
				toastModule.show(new DetailsToast("Particle system cannot be created.\nProbably, particle texture is missing.", e));
				return;
			}

		} else if (payload instanceof SoundAsset) {
			SoundAsset asset = (SoundAsset) payload;

			entity = new EntityBuilder(entityEngine)
					.with(new VisSound(null), new Transform(), //editor does not require sound to be loaded, we can pass null sound here
							new AssetReference(asset),
							new Renderable(0), new Layer(scene.getActiveLayerId()),
							new ExporterDropsComponent(Transform.class, Renderable.class, Layer.class, VisGroup.class))
					.build();

		} else if (payload instanceof MusicAsset) {
			MusicAsset asset = (MusicAsset) payload;

			entity = new EntityBuilder(entityEngine)
					.with(new VisMusic(new DummyMusic()), new Transform(),
							new AssetReference(asset),
							new Renderable(0), new Layer(scene.getActiveLayerId()),
							new ExporterDropsComponent(Transform.class, Renderable.class, Layer.class, VisGroup.class))
					.build();

		}

		for (EditorEntitySupport support : projectExtensionStorage.getEntitySupports()) {
			Entity supportEntity = support.processDropPayload(entityEngine, scene, payload);
			if (supportEntity != null) {
				entity = supportEntity;
				break;
			}
		}

		if (entity != null) {
			entity.edit().add(new VisUUID());

			sceneContainer.updateEntitiesStates();

			EntityProxy proxy = entityProxyCache.get(entity);

			Runnable updatePosRunnable = () -> {
				if (setEntityPosToMouse.value) {
					if (editingSettings.isSnapEnabledOrKeyPressed()) {
						float gridSize = gridSettings.config.gridSize;
						float x = MathUtils.floor(camera.getInputX() / gridSize) * gridSize;
						float y = MathUtils.floor(camera.getInputY() / gridSize) * gridSize;
						proxy.setPosition(x, y);
					} else {
						float x = camera.getInputX() - proxy.getWidth() / 2;
						float y = camera.getInputY() - proxy.getHeight() / 2;

						proxy.setPosition(x, y);
					}
				} else {
					float x = camera.getX() - proxy.getWidth() / 2;
					float y = camera.getY() - proxy.getHeight() / 2;
					proxy.setPosition(x, y);
				}
			};

			if (updatePositionLater) {
				//delay executions two frames to make sure all systems will update entities state, prolly not the best
				//method but can't find alternative now
				Gdx.app.postRunnable(() -> Gdx.app.postRunnable(updatePosRunnable));
			} else {
				updatePosRunnable.run();
			}

			undoModule.add(new EntitiesAddedAction(sceneContainer, entityEngine, entity));

			if (entitiesSelection.getGroupId() != NO_GROUP_SET) {
				proxy.addGroup(entitiesSelection.getGroupId());
			}
		}
	}

	@Subscribe
	public void handleUndoableModuleEvent (UndoableModuleEvent event) {
		sceneOutline.rebuildOutline();
		entityProperties.selectedEntitiesChanged();
		renderBatchingSystem.markDirty();
	}

	@Subscribe
	public void handleToolSwitch (ToolSwitchedEvent event) {
		switchTool(event.newToolId);
	}

	public void switchTool (String toolId) {
		Tool newTool = tools.get(toolId);
		if (newTool == null) throw new IllegalStateException("Could not find tool with ID: " + toolId);
		switchTool(newTool);
	}

	public void switchTool (Tool tool) {
		if (currentTool != null) currentTool.deactivated();
		currentTool = tool;
		currentTool.activated();

		toolPropertiesContainer.reset();
		VisTable table = currentTool.getToolPropertiesUI();
		if (table != null)
			toolPropertiesContainer.add(table).expandX().fillX();
	}

	public void findEntityBaseGroupAndSelect (EntityProxy proxy) {
		groupBreadcrumb.resetHierarchy();
		IntArray array = proxy.getGroupsIds();
		if (array.size > 0) {
			array.reverse();

			int newGid = array.peek();
			entitiesSelection = new EntitiesSelection(entitiesCollector, scene.getActiveLayerId(), newGid);

			for (int i = 0; i < array.size; i++) {
				int gid = array.get(i);
				groupBreadcrumb.addGroup(gid);
			}
		}

		select(proxy);
	}

	public void select (Entity entity) {
		select(entityProxyCache.get(entity));
	}

	public void select (EntityProxy proxy) {
		EditorLayer layer = scene.getLayerById(proxy.getLayerID());
		if (layer.locked) return;
		scene.setActiveLayer(layer.id);

		entitiesSelection.clearSelection();

		checkProxyGid(proxy);

		entitiesSelection.append(proxy);
		selectedEntitiesChanged();
	}

	/** Appends to current selection, however if entity layer is different than current layer then selection will be reset */
	public void selectAppend (Entity entity) {
		selectAppend(entityProxyCache.get(entity));
	}

	/** Appends to current selection, however if entity layer is different than current layer then selection will be reset */
	public void selectAppend (EntityProxy proxy) {
		fastSelectAppend(proxy);
		selectedEntitiesChanged();
	}

	/**
	 * Appends to current selection without notifying other components, useful when appending multiple entities.
	 * {@link #selectedEntitiesChanged()} must be called after all entities all added to selection.
	 */
	void fastSelectAppend (EntityProxy proxy) {
		EditorLayer layer = scene.getLayerById(proxy.getLayerID());
		if (layer.locked) return;

		if (scene.getActiveLayerId() != layer.id) {
			scene.setActiveLayer(layer.id);
			hardSelectionReset();
		}

		checkProxyGid(proxy);

		entitiesSelection.append(proxy);
	}

	private void checkProxyGid (EntityProxy proxy) {
		if (entitiesSelection.getGroupId() == NO_GROUP_SET) return;

		int proxyGid = proxy.getLastGroupId();
		if (proxy.groupsContains(entitiesSelection.getGroupId()) == false) {
			if (groupBreadcrumb.isInHierarchy(proxyGid)) {
				entitiesSelection = new EntitiesSelection(entitiesCollector, scene.getActiveLayerId(), proxyGid);
				groupBreadcrumb.trimToGid(proxyGid);
			} else {
				hardSelectionReset();
			}
		}
	}

	public void selectAll () {
		if (scene.getActiveLayer().locked) return;

		Array<EntityProxy> proxies = entitiesCollector.collect(entitiesSelection.getLayerId(), entitiesSelection.getGroupId());
		proxies.forEach(this::fastSelectAppend);
		selectedEntitiesChanged();
	}

	public void selectAll (int layerId, int groupId) {
		Array<EntityProxy> proxies = entitiesCollector.collect(layerId, groupId);
		if (proxies.size == 0) return;

		EntityProxy proxy = proxies.first(); //not important what proxy we will use, it just have to belong to hierarchy //TODO collect only one;

		if (scene.getActiveLayerId() != proxy.getLayerID()) {
			scene.setActiveLayer(proxy.getLayerID());
		}

		groupBreadcrumb.resetHierarchy();

		int targetGid = proxy.getGroupIdAfter(groupId);

		if (targetGid != -1) {
			IntArray groupsIds = proxy.getGroupsIds();
			if (groupsIds.size > 1) {
				groupsIds.reverse();

				for (int i = 0; i < groupsIds.size; i++) {
					int gid = groupsIds.get(i);
					groupBreadcrumb.addGroup(gid);
					if (targetGid == gid) break;
				}

			}
		}

		entitiesSelection = new EntitiesSelection(entitiesCollector, layerId, targetGid);
		selectAppend(proxy); //selecting first entity is enough to select whole group
	}

	public boolean isSelected (EntityProxy proxy) {
		return entitiesSelection.isSelected(proxy);
	}

	/** Resets selection along with group breadcrumb and changes current groupId to -1 */
	public void hardSelectionReset () {
		groupBreadcrumb.resetHierarchy();
		entitiesSelection = new EntitiesSelection(entitiesCollector, scene.getActiveLayerId());
		selectedEntitiesChanged();
	}

	/** Resets selection without changing current groupId */
	public void softSelectionReset () {
		entitiesSelection.clearSelection();
		selectedEntitiesChanged();
	}

	/** Resets selection without changing current groupId and without calling {@link #selectedEntitiesChanged()} */
	void silentSelectionReset () {
		entitiesSelection.clearSelection();
	}

	public void deselect (EntityProxy result) {
		entitiesSelection.deselect(result);
		selectedEntitiesChanged();
	}

	/**
	 * Notifies that selected entities has changed. If only entities values was modified you must call {@link #selectedEntitiesValuesChanged()}
	 * because this method will do full ui rebuilt expecting that selection is different.
	 */
	public void selectedEntitiesChanged () {
		entityProperties.selectedEntitiesChanged();
		sceneOutline.selectedEntitiesChanged();
		currentTool.selectedEntitiesChanged();
	}

	/**
	 * Notifies that selected entities values has changed. This must be only called if values has changed if new component
	 * was added or selection itself changed you must call {@link #selectedEntitiesChanged()}
	 */
	public void selectedEntitiesValuesChanged () {
		entityProperties.selectedEntitiesValuesChanged();
		currentTool.selectedEntitiesValuesChanged();
		markSceneDirty();
	}

	public void groupSelection () {
		if (entitiesSelection.size() <= 1) {
			statusBar.setText("Noting to group!");
			return;
		}

		int gid = groupIdProvider.getFreeGroupId();

		undoModule.execute(new GroupAction(entitiesSelection.getSelection(), gid, entitiesSelection.getGroupId(), true));

		sceneOutline.rebuildOutline();

		EntityProxy proxy = entitiesSelection.getSelection().first(); //selecting one proxy will select whole group
		softSelectionReset();
		select(proxy);
	}

	public void ungroupSelection () {
		if (entitiesSelection.size() == 0) {
			statusBar.setText("Noting to ungroup!");
			return;
		}

		//fragment that will be used later to select group objects
		GroupSelectionFragment selectionFragment = null;

		UndoableActionGroup actionGroup = new UndoableActionGroup("Ungroup");

		for (SelectionFragment f : entitiesSelection.getFragmentedSelection()) {
			if (f instanceof GroupSelectionFragment) {
				GroupSelectionFragment fragment = (GroupSelectionFragment) f;
				selectionFragment = fragment;
				actionGroup.add(new GroupAction(fragment.getProxies(), fragment.getGroupId(), entitiesSelection.getGroupId(), false));
			}
		}

		if (selectionFragment != null) {
			actionGroup.finalizeGroup();
			undoModule.execute(actionGroup);

			hardSelectionReset();

			sceneOutline.rebuildOutline();

			selectionFragment.getProxies().forEach(this::selectAppend);
		} else
			statusBar.setText("No group selected!");
	}

	public void markSceneDirty () {
		sceneTab.dirty();
	}

	@Override
	public void render (Batch batch) {
		batch.end();
		shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());

		if (currentTool.isRenderBoundsEnabled() && entitiesSelection.size() > 0) {
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.begin(ShapeType.Line);

			for (SelectionFragment fragment : entitiesSelection.getFragmentedSelection()) {
				Rectangle bounds = fragment.getBoundingRectangle();
				shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			}

			shapeRenderer.end();
		}
		entityMoveTimerTask.update();

		currentTool.render(shapeRenderer);

		batch.begin();

		currentTool.render(batch);
	}

	@Override
	public void dispose () {
		layersDialog.dispose();
	}

	public EntityProperties getEntityProperties () {
		return entityProperties;
	}

	public LayersDialog getLayersDialog () {
		return layersDialog;
	}

	public GroupBreadcrumb getGroupBreadcrumb () {
		return groupBreadcrumb;
	}

	public AlignmentToolsDialog getAlignmentToolsDialog () {
		return alignmentToolsDialog;
	}

	public SceneOutline getSceneOutline () {
		return sceneOutline;
	}

	public VisTable getToolPropertiesContainer () {
		return toolPropertiesContainer;
	}

	public ImmutableArray<EntityProxy> getSelectedEntities () {
		return entitiesSelection.getSelection();
	}

	public ImmutableArray<SelectionFragment> getFragmentedSelection () {
		return entitiesSelection.getFragmentedSelection();
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		if (scene.getActiveLayer().locked) return false;
		return currentTool.touchDown(event, x, y, pointer, button);
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (scene.getActiveLayer().locked) return;
		mouseDragged = true;
		currentTool.touchDragged(event, x, y, pointer);
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		if (scene.getActiveLayer().locked) return;
		currentTool.touchUp(event, x, y, pointer, button);

		if (button == Buttons.RIGHT && mouseDragged == false) {
			if (entitiesSelection.size() > 0) {
				menuX = camera.getInputX();
				menuY = camera.getInputY();

				buildSelectedEntitiesPopupMenu();
				entityPopupMenu.showMenu(event.getStage(), event.getStageX(), event.getStageY());
			} else
				generalPopupMenu.showMenu(event.getStage(), event.getStageX(), event.getStageY());
		}

		mouseDragged = false;
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		if (scene.getActiveLayer().locked) return false;
		return currentTool.mouseMoved(event, x, y);
	}

	@Override
	public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
		if (scene.getActiveLayer().locked) return;
		currentTool.enter(event, x, y, pointer, fromActor);
	}

	@Override
	public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
		if (scene.getActiveLayer().locked) return;
		currentTool.exit(event, x, y, pointer, toActor);
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		if (scene.getActiveLayer().locked) return false;
		return currentTool.scrolled(event, x, y, amount);
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		if (UIUtils.ctrl() && keycode == Keys.S) sceneTab.save();
		if (keycode == Keys.F1) {
			switchTool(SelectionTool.TOOL_ID);
			App.eventBus.post(new ToolSwitchedEvent(SelectionTool.TOOL_ID));
		}
		if (keycode == Keys.F2) {
			switchTool(RotateTool.TOOL_ID);
			App.eventBus.post(new ToolSwitchedEvent(RotateTool.TOOL_ID));
		}
		if (keycode == Keys.F3) {
			switchTool(ScaleTool.TOOL_ID);
			App.eventBus.post(new ToolSwitchedEvent(ScaleTool.TOOL_ID));
		}
		if (keycode == Keys.F4) {
			switchTool(PolygonTool.TOOL_ID);
			App.eventBus.post(new ToolSwitchedEvent(PolygonTool.TOOL_ID));
		}

		if (scene.getActiveLayer().locked) return false;
		boolean result = currentTool.keyDown(event, keycode);

		if (result == false) {
			if (keycode == Keys.FORWARD_DEL) { //Delete
				deleteSelectedEntities();
				return true;
			}

			if (UIUtils.ctrl()) {
				if (keycode == Keys.A) selectAll();
				if (keycode == Keys.C) copy();
				if (keycode == Keys.V) paste();
				if (keycode == Keys.X) cut();
				if (keycode == Keys.D) duplicate();
			}

			if (Gdx.input.isKeyPressed(Keys.PAGE_UP))
				zIndexManipulator.moveSelectedEntities(getSelectedEntities(), true);
			if (Gdx.input.isKeyPressed(Keys.PAGE_DOWN))
				zIndexManipulator.moveSelectedEntities(getSelectedEntities(), false);

			return false;
		}

		return true;
	}

	@Override
	public boolean keyUp (InputEvent event, int keycode) {
		if (scene.getActiveLayer().locked) return false;
		return currentTool.keyUp(event, keycode);
	}

	@Override
	public boolean keyTyped (InputEvent event, char character) {
		if (scene.getActiveLayer().locked) return false;
		return currentTool.keyTyped(event, character);
	}
}
