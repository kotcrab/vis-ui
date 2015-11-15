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

package com.kotcrab.vis.editor.module.scene.entitymanipulator;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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
import com.kotcrab.vis.editor.entity.*;
import com.kotcrab.vis.editor.event.ToolSwitchedEvent;
import com.kotcrab.vis.editor.event.UndoableModuleEvent;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.module.editor.EditingSettingsModule;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.module.scene.*;
import com.kotcrab.vis.editor.module.scene.GridRendererSystem.GridSettingsModule;
import com.kotcrab.vis.editor.module.scene.action.*;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.PolygonTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.SelectionTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.Tool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.Tools;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.proxy.GroupEntityProxy;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.Layer;
import com.kotcrab.vis.editor.ui.dialog.SelectLayerDialog;
import com.kotcrab.vis.editor.ui.scene.GroupBreadcrumb;
import com.kotcrab.vis.editor.ui.scene.GroupBreadcrumb.GroupBreadcrumbListener;
import com.kotcrab.vis.editor.ui.scene.LayersDialog;
import com.kotcrab.vis.editor.ui.scene.SceneOutline;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.util.Holder;
import com.kotcrab.vis.editor.util.gdx.DummyMusic;
import com.kotcrab.vis.editor.util.scene2d.MenuUtils;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;
import com.kotcrab.vis.editor.util.vis.CreatePointPayload;
import com.kotcrab.vis.editor.util.vis.ProtoEntity;
import com.kotcrab.vis.runtime.assets.*;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisTable;

import static com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityMoveTimerTask.*;

/** @author Kotcrab */
@EventBusSubscriber
public class EntityManipulatorModule extends SceneModule {
	private StatusBarModule statusBar;
	private EditingSettingsModule editingSettings;
	private GridSettingsModule gridSettings;
	private ExtensionStorageModule extensionStorage;

	private SceneIOModule sceneIO;

	private CameraModule camera;
	private UndoModule undoModule;
	private TextureCacheModule textureCache;
	private ParticleCacheModule particleCache;
	private FontCacheModule fontCache;
	private SpriterCacheModule spriterCache;
	private RendererModule rendererModule;

	private Stage stage;

	private ComponentMapper<GroupComponent> groupCm;

	private EntityProxyCache entityProxyCache;
	private ZIndexManipulatorManager zIndexManipulator;
	private GroupIdProviderSystem groupIdProvider;
	private GroupProxyProviderSystem groupProxyProvider;
	private RenderBatchingSystem renderBatchingSystem;

	private ShapeRenderer shapeRenderer;

	//TODO [misc] create common class for scene ui dialogs
	private EntityProperties entityProperties;
	private GroupBreadcrumb groupBreadcrumb;
	private LayersDialog layersDialog;
	private AlignmentToolsDialog alignmentToolsDialog;
	private SceneOutline sceneOutline;

	private Tool currentTool;
	private SelectionTool selectionTool;
	private PolygonTool polygonTool;

	private int currentSelectionGid = -1;
	private Array<EntityProxy> selectedEntities = new Array<>();
	private ImmutableArray<EntityProxy> immutableSelectedEntities = new ImmutableArray<>(selectedEntities);

	private float copyAttachX, copyAttachY;
	private Array<ProtoEntity> entitiesClipboard = new Array<>();

	private boolean mouseDragged;
	/** popup menu position in scene cords system */
	private float menuX, menuY;
	private PopupMenu generalPopupMenu;
	private PopupMenu entityPopupMenu;

	private EntityMoveTimerTask entityMoveTimerTask;
	private MoveEntitiesAction keyMoveAction;

	private VisTable toolPropertiesContainer;

	@Override
	public void init () {
		shapeRenderer = rendererModule.getShapeRenderer();

		entityProperties = new EntityProperties(sceneContainer, sceneTab, selectedEntities);
		groupBreadcrumb = new GroupBreadcrumb(new GroupBreadcrumbListener() {
			@Override
			public void clicked (int gid) {
				currentSelectionGid = gid;
				selectAll();
			}

			@Override
			public void rootClicked () {
				currentSelectionGid = -1;
				groupBreadcrumb.resetHierarchy();
				resetSelection();
			}
		});
		layersDialog = new LayersDialog(sceneTab, engineConfiguration, sceneContainer);
		alignmentToolsDialog = new AlignmentToolsDialog(sceneContainer, selectedEntities);
		sceneOutline = new SceneOutline(sceneContainer, selectedEntities);
		createContextMenus();

		toolPropertiesContainer = new VisTable();

		entityMoveTimerTask = new EntityMoveTimerTask(scene, this, immutableSelectedEntities);

		selectionTool = new SelectionTool();
		polygonTool = new PolygonTool();

		selectionTool.setModules(sceneContainer, scene);
		polygonTool.setModules(sceneContainer, scene);

		switchTool(selectionTool);

		scene.addObservable(notificationId -> {
			if (notificationId == EditorScene.ACTIVE_LAYER_CHANGED) {
				resetSelection();
			}
		});
	}

	@Override
	public void postInit () {
		entityProperties.loadSupportsSpecificTables(projectContainer.get(SupportModule.class));
	}

	private void createContextMenus () {
		entityPopupMenu = new PopupMenu();

		generalPopupMenu = new PopupMenu();
		generalPopupMenu.addItem(MenuUtils.createMenuItem("Paste", this::paste));
		generalPopupMenu.addItem(MenuUtils.createMenuItem("Select All", this::selectAll));
	}

	private void buildEntityPopupMenu (Array<EntityProxy> entities) {
		entityPopupMenu.clearChildren();

		if (isMenuItemEnterIntoGroupValid(entities)) {
			entityPopupMenu.addItem(MenuUtils.createMenuItem("Enter Into Group", () -> {

				if (isMenuItemEnterIntoGroupValid(entities) == false) {
					DialogUtils.showErrorDialog(stage, "Group was deselected");
					return;
				}

				GroupEntityProxy groupEntityProxy = (GroupEntityProxy) entities.peek();
				currentSelectionGid = groupEntityProxy.getGroupId();
				groupBreadcrumb.addGroup(currentSelectionGid);
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
		if (currentSelectionGid != -1 && showGroupMoveWarning) {
			DialogUtils.showOptionDialog(stage, "Warning", "This will move whole group to another layer. Continue moving?",
					OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
						@Override
						public void yes () {
							moveToLayer(false);
						}
					});
			return;
		}

		Array<EntityProxy> targetEntities;

		if (currentSelectionGid != -1) {
			targetEntities = new Array<>();
			targetEntities.add(groupProxyProvider.getGroupEntityProxy(groupBreadcrumb.peekFirstGroupId()));

			resetSelection();
			groupBreadcrumb.resetHierarchy();
			currentSelectionGid = -1;
		} else
			targetEntities = new Array<>(selectedEntities);

		stage.addActor(new SelectLayerDialog(scene.getLayers(), scene.getActiveLayer(), result -> {
			UndoableActionGroup group = new UndoableActionGroup("Move Entity To Layer", "Move Entities To Layer");
			for (EntityProxy proxy : targetEntities) {
				group.add(new ChangeEntityLayerAction(renderBatchingSystem, this, proxy, result.id));
			}

			group.finalizeGroup();
			undoModule.execute(group);

			//reselect entities again
			resetSelection();
			for (EntityProxy proxy : targetEntities)
				selectAppend(proxy);
		}).fadeIn());
	}

	private boolean isMenuItemEnterIntoGroupValid (Array<EntityProxy> entities) {
		return (entities.size == 1 && entities.peek() instanceof GroupEntityProxy);
	}

	private void copy () {
		if (selectedEntities.size > 0) {
			entitiesClipboard.clear();
			selectedEntities.forEach(proxy -> proxy.getEntities().forEach(
					entity -> entitiesClipboard.add(sceneIO.createProtoEntity(entityEngine, entity, false))));

			EntityProxy proxy = selectedEntities.peek();

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
			selectedEntities.clear();

			Array<EntityProxy> proxies = new Array<>(selectedEntities.size);
			ObjectSet<Entity> entities = new ObjectSet<>(selectedEntities.size);

			IntIntMap groupIdRemap = new IntIntMap();
			Holder<Integer> freeGidHolder = new Holder<>(groupIdProvider.getFreeGroupId());

			entitiesClipboard.forEach(protoEntity -> {
				Entity entity = protoEntity.build();
				entities.add(entity);
				if (scene.getActiveLayer().visible == false) entity.edit().add(new InvisibleComponent());
				proxies.add(entityProxyCache.get(entity));

				GroupComponent groups = groupCm.getSafe(entity);

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

		selectedEntities.forEach(proxy -> entities.addAll(proxy.getEntities()));
		selectedEntities.clear();
		selectedEntitiesChanged();

		undoModule.execute(new EntitiesRemovedAction(sceneContainer, entityEngine, entities));
	}

	public void processDropPayload (Object obj) {
		if (scene.getActiveLayer().locked) {
			statusBar.setText("Layer is locked!");
			return;
		}

		boolean setEntityPosToMouse = true;

		Entity entity = null;

		//TODO: refactor this

		if (obj instanceof CreatePointPayload) {
			CreatePointPayload pointPayload = (CreatePointPayload) obj;
			if (pointPayload.centerPosAfterCreation) setEntityPosToMouse = false;

			entity = new EntityBuilder(entityEngine)
					.with(new PointComponent())
					.with(new RenderableComponent(0), new LayerComponent(scene.getActiveLayerId()))
					.with(new ExporterDropsComponent(RenderableComponent.class, LayerComponent.class))
					.build();

		} else if (obj instanceof TextureAssetDescriptor) {
			TextureAssetDescriptor asset = (TextureAssetDescriptor) obj;

			entity = new EntityBuilder(entityEngine)
					.with(new SpriteComponent(textureCache.getSprite(asset, scene.pixelsPerUnit)),
							new AssetComponent(asset),
							new RenderableComponent(0), new LayerComponent(scene.getActiveLayerId()))
					.build();

		} else if (obj instanceof BmpFontAsset || obj instanceof TtfFontAsset) {
			VisAssetDescriptor asset = (VisAssetDescriptor) obj;

			entity = new EntityBuilder(entityEngine)
					.with(new TextComponent(fontCache.getGeneric(asset, scene.pixelsPerUnit), FontCacheModule.DEFAULT_TEXT),
							new PixelsPerUnitComponent(scene.pixelsPerUnit),
							new AssetComponent(asset),
							new RenderableComponent(0), new LayerComponent(scene.getActiveLayerId()),
							new ExporterDropsComponent(PixelsPerUnitComponent.class))
					.build();

		} else if (obj instanceof SpriterAsset) {
			SpriterAsset asset = (SpriterAsset) obj;

			float scale = 1f / scene.pixelsPerUnit;

			entity = new EntityBuilder(entityEngine)
					.with(spriterCache.createComponent(asset, scale), new SpriterPropertiesComponent(scale),
							new AssetComponent(asset),
							new RenderableComponent(0), new LayerComponent(scene.getActiveLayerId()),
							new ExporterDropsComponent(SpriterPropertiesComponent.class))
					.build();
		} else if (obj instanceof PathAsset) {
			PathAsset asset = (PathAsset) obj;

			if (asset.getPath().startsWith("sound/")) {
				entity = new EntityBuilder(entityEngine)
						.with(new SoundComponent(null), new PositionComponent(), //editor does not require sound to be loaded, we can pass null sound here
								new AssetComponent(asset),
								new RenderableComponent(0), new LayerComponent(scene.getActiveLayerId()),
								new ExporterDropsComponent(PositionComponent.class, RenderableComponent.class, LayerComponent.class, GroupComponent.class))
						.build();

			}

			if (asset.getPath().startsWith("music/")) {
				entity = new EntityBuilder(entityEngine)
						.with(new MusicComponent(new DummyMusic()), new PositionComponent(),
								new AssetComponent(asset),
								new RenderableComponent(0), new LayerComponent(scene.getActiveLayerId()),
								new ExporterDropsComponent(PositionComponent.class, RenderableComponent.class, LayerComponent.class, GroupComponent.class))
						.build();

			}

			if (asset.getPath().startsWith("particle/")) {
				float scale = 1f / scene.pixelsPerUnit;

				ParticleEffect particleEffect = particleCache.get(asset, scale);
				if (particleEffect != null) {
					entity = new EntityBuilder(entityEngine)
							.with(new ParticleComponent(particleEffect), new PixelsPerUnitComponent(scene.pixelsPerUnit),
									new AssetComponent(asset),
									new RenderableComponent(0), new LayerComponent(scene.getActiveLayerId()),
									new ExporterDropsComponent(PixelsPerUnitComponent.class))
							.build();
				}
				else {
					statusBar.setText("Particle system cannot be created. Probably, particle texture is missing.");
					return;
				}
			}
		}

		for (EditorEntitySupport support : extensionStorage.getEntitiesSupports()) {
			Entity supportEntity = support.processDropPayload(entityEngine, scene, obj);
			if (supportEntity != null) {
				entity = supportEntity;
				break;
			}
		}

		if (entity != null) {
			entity.edit().add(new UUIDComponent());

			EntityProxy proxy = entityProxyCache.get(entity);

			if (setEntityPosToMouse) {
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

			undoModule.add(new EntitiesAddedAction(sceneContainer, entityEngine, entity));

			if (currentSelectionGid != -1)
				proxy.addGroup(currentSelectionGid);
		}
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

			currentSelectionGid = array.peek();

			for (int i = 0; i < array.size; i++) {
				int gid = array.get(i);
				groupBreadcrumb.addGroup(gid);
			}
		}

		if (proxy instanceof GroupEntityProxy)
			selectAll();
		else
			select(proxy);
	}

	public void select (Entity entity) {
		select(entityProxyCache.get(entity));
	}

	public void select (EntityProxy proxy) {
		Layer layer = scene.getLayerById(proxy.getLayerID());
		if (layer.locked) return;
		scene.setActiveLayer(layer.id);

		selectedEntities.clear();

		checkProxyGid(proxy);

		selectAddToList(proxy);
		selectedEntitiesChanged();
	}

	/** Appends to current selection, however if entity layer is different than current layer then selection will be reset */
	public void selectAppend (Entity entity) {
		selectAppend(entityProxyCache.get(entity));
	}

	/** Appends to current selection, however if entity layer is different than current layer then selection will be reset */
	public void selectAppend (EntityProxy proxy) {
		Layer layer = scene.getLayerById(proxy.getLayerID());
		if (layer.locked) return;

		if (scene.getActiveLayerId() != layer.id) {
			scene.setActiveLayer(layer.id);
			selectedEntities.clear();
		}

		checkProxyGid(proxy);

		selectAddToList(proxy);
		selectedEntitiesChanged();
	}

	private void checkProxyGid (EntityProxy proxy) {
		int proxyGid = proxy.getLastGroupId();
		if (proxy.groupsContains(currentSelectionGid) == false) {
			if (groupBreadcrumb.isInHierarchy(proxyGid)) {
				currentSelectionGid = proxyGid;
				groupBreadcrumb.trimToGid(proxyGid);
			} else {
				currentSelectionGid = -1;
				groupBreadcrumb.resetHierarchy();
			}
		}
	}

	public void selectAll () {
		if (scene.getActiveLayer().locked) return;

		selectedEntities.clear();

		if (currentSelectionGid == -1) {
			int layerId = scene.getActiveLayerId();

			entityProxyCache.getCache().values().forEach(proxy -> {
				if (proxy.getLayerID() == layerId) {
					selectAddToList(proxy);
				}
			});

		} else {
			GroupEntityProxy proxy = groupProxyProvider.getGroupEntityProxy(currentSelectionGid);
			proxy.getProxies().forEach(this::selectAddToList);
		}

		selectedEntitiesChanged();
	}

	private void selectAddToList (EntityProxy proxy) {
		int lastGid = currentSelectionGid == -1 ? proxy.getLastGroupId() : proxy.getGroupIdBefore(currentSelectionGid);
		if (proxy instanceof GroupEntityProxy == false && lastGid != -1) {
			if (isGroupIdAlreadySelected(lastGid) == false) {
				selectedEntities.add(groupProxyProvider.getGroupEntityProxy(lastGid));
			}
		} else
			selectedEntities.add(proxy);
	}

	public boolean isSelected (EntityProxy proxy) {
		return selectedEntities.contains(proxy, true);
	}

	public boolean isGroupIdAlreadySelected (int gid) {
		for (EntityProxy proxy : selectedEntities) {
			if (proxy instanceof GroupEntityProxy) {
				GroupEntityProxy groupProxy = (GroupEntityProxy) proxy;
				if (groupProxy.getGroupId() == gid) return true;
			}
		}

		return false;
	}

	public void resetSelection () {
		selectedEntities.clear();
		selectedEntitiesChanged();
	}

	public void deselect (EntityProxy result) {
		selectedEntities.removeValue(result, true);
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
		markSceneDirty();
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
		if (selectedEntities.size <= 1) {
			statusBar.setText("Noting to group!");
			return;
		}

		int gid = groupIdProvider.getFreeGroupId();

		undoModule.execute(new GroupAction(selectedEntities, gid, currentSelectionGid, true));

		sceneOutline.rebuildOutline();

		GroupEntityProxy groupProxy = new GroupEntityProxy(selectedEntities, gid);
		resetSelection();
		select(groupProxy);
	}

	public void ungroupSelection () {
		if (selectedEntities.size == 0) {
			statusBar.setText("Noting to ungroup!");
			return;
		}

		GroupEntityProxy selectionProxy = null; //proxy that will be used later to select group objects

		UndoableActionGroup actionGroup = new UndoableActionGroup("Ungroup");

		for (EntityProxy entity : selectedEntities) {
			if (entity instanceof GroupEntityProxy) {
				GroupEntityProxy group = (GroupEntityProxy) entity;
				selectionProxy = group;
				actionGroup.add(new GroupAction(group.getProxies(), group.getGroupId(), currentSelectionGid, false));
			}
		}

		if (selectionProxy != null) {
			actionGroup.finalizeGroup();
			undoModule.execute(actionGroup);

			groupBreadcrumb.resetHierarchy();
			currentSelectionGid = -1;

			sceneOutline.rebuildOutline();

			resetSelection();
			selectionProxy.getProxies().forEach(this::selectAppend);
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

		if (selectedEntities.size > 0) {
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.begin(ShapeType.Line);

			for (EntityProxy entity : selectedEntities) {
				Rectangle bounds = entity.getBoundingRectangle();
				shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			}

			shapeRenderer.end();

		}

		currentTool.render(shapeRenderer);

		batch.begin();

		currentTool.render(batch);
	}

	@Subscribe
	public void handleUndoableModuleEvent (UndoableModuleEvent event) {
		sceneOutline.rebuildOutline();
		renderBatchingSystem.markDirty();
	}

	@Subscribe
	public void handleToolSwitch (ToolSwitchedEvent event) {
		if (event.newToolId == Tools.SELECTION_TOOL)
			switchTool(selectionTool);
		if (event.newToolId == Tools.POLYGON_TOOL)
			switchTool(polygonTool);
	}

	@Override
	public void dispose () {
		layersDialog.dispose();
		entityProperties.dispose();
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
		return immutableSelectedEntities;
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
			if (selectedEntities.size > 0) {
				menuX = camera.getInputX();
				menuY = camera.getInputY();

				buildEntityPopupMenu(selectedEntities);
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
		if (scene.getActiveLayer().locked) return false;
		boolean result = currentTool.keyDown(event, keycode);

		if (result == false) {
			if (keycode == Keys.FORWARD_DEL) { //Delete
				deleteSelectedEntities();
				return true;
			}

			if (UIUtils.ctrl() && keycode == Keys.S) sceneTab.save();
			if (keycode == Keys.F1) {
				switchTool(selectionTool);
				App.eventBus.post(new ToolSwitchedEvent(Tools.SELECTION_TOOL));
			}
			if (keycode == Keys.F2) {
				switchTool(polygonTool);
				App.eventBus.post(new ToolSwitchedEvent(Tools.POLYGON_TOOL));
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

			float delta = 10;
			if (UIUtils.shift()) delta *= 10;
			if (UIUtils.ctrl()) delta *= 10;

			delta = delta / scene.pixelsPerUnit;

			int direction = 0;

			if (Gdx.input.isKeyPressed(Keys.UP)) direction = direction | UP;
			else if (Gdx.input.isKeyPressed(Keys.DOWN)) direction = direction | DOWN;
			if (Gdx.input.isKeyPressed(Keys.LEFT)) direction = direction | LEFT;
			else if (Gdx.input.isKeyPressed(Keys.RIGHT)) direction = direction | RIGHT;

			if (direction > 0) {
				entityMoveTimerTask.set(direction, delta);

				if (entityMoveTimerTask.isScheduled() == false) {
					keyMoveAction = new MoveEntitiesAction(this, selectedEntities);

					entityMoveTimerTask.run();
					float keyRepeatInitialTime = 0.4f;
					float keyRepeatTime = 0.05f;
					Timer.schedule(entityMoveTimerTask, keyRepeatInitialTime, keyRepeatTime);
				}
				return true;
			}

			return false;
		}

		return true;
	}

	@Override
	public boolean keyUp (InputEvent event, int keycode) {
		if (scene.getActiveLayer().locked) return false;
		if ((Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.RIGHT)) == false)
			cancelMoveEntityTask(); //do not cancel task untill all keys are released
		return currentTool.keyUp(event, keycode);
	}

	private void cancelMoveEntityTask () {
		entityMoveTimerTask.cancel();
		if (keyMoveAction != null) {
			keyMoveAction.saveNewData();
			undoModule.add(keyMoveAction);
			keyMoveAction = null;
		}
	}

	@Override
	public boolean keyTyped (InputEvent event, char character) {
		if (scene.getActiveLayer().locked) return false;
		return currentTool.keyTyped(event, character);
	}
}
