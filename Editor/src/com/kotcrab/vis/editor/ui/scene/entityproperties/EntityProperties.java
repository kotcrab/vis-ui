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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.artemis.Component;
import com.artemis.EntityEdit;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.UndoableModuleEvent;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ColorPickerModule;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.editor.ToastModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.SupportModule;
import com.kotcrab.vis.editor.module.scene.CameraModule;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.VisComponentManipulator;
import com.kotcrab.vis.editor.module.scene.action.ComponentAddAction;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.proxy.GroupEntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.components.AutoComponentTable;
import com.kotcrab.vis.editor.ui.scene.entityproperties.components.RenderableComponentTable;
import com.kotcrab.vis.editor.ui.scene.entityproperties.components.SpriterPropertiesComponentTable;
import com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable.*;
import com.kotcrab.vis.editor.ui.toast.DetailsToast;
import com.kotcrab.vis.editor.util.gdx.EventStopper;
import com.kotcrab.vis.editor.util.gdx.FieldUtils;
import com.kotcrab.vis.editor.util.gdx.VisChangeListener;
import com.kotcrab.vis.editor.util.gdx.VisValue;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;
import com.kotcrab.vis.editor.util.value.FloatProxyValue;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.component.PhysicsPropertiesComponent;
import com.kotcrab.vis.runtime.component.PolygonComponent;
import com.kotcrab.vis.runtime.component.ShaderComponent;
import com.kotcrab.vis.runtime.component.VariablesComponent;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ActorUtils;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.lang.reflect.Constructor;
import java.util.Iterator;

/**
 * Entity properties dialog, used to display and change all data about currently selected entities. Multiple selection
 * is supported, even when entities have different values, <?> is used in float input fields, and intermediate checkbox are used
 * for boolean support. Undo is supported. Plugin can add custom properties tables (see {@link SpecificUITable} and {@link SpecificComponentTable}),
 * but they must support all base features of this dialog (multiple selection support, undo, etc.). See any class
 * from 'specifictable' and 'components' child packages for examples.
 * @author Kotcrab
 */
public class EntityProperties extends VisTable implements Disposable {
	public static final int LABEL_WIDTH = 60;
	public static final int AXIS_LABEL_WIDTH = 10;
	public static final int FIELD_WIDTH = 70;

	@InjectModule private StatusBarModule statusBarModule;
	@InjectModule private ToastModule toastModule;
	@InjectModule private ColorPickerModule colorPickerModule;

	@InjectModule private FileAccessModule fileAccessModule;
	@InjectModule private FontCacheModule fontCacheModule;
	@InjectModule private SceneIOModule sceneIO;

	@InjectModule private UndoModule undoModule;
	@InjectModule private CameraModule cameraModule;
	@InjectModule private EntityManipulatorModule entityManipulator;

	private VisComponentManipulator componentManipulator;

	private ColorPicker picker;
	private Tab parentTab;

	private Array<EntityProxy> entities;
	private boolean groupSelected;

	private ChangeListener sharedChangeListener;
	private ChangeListener sharedChckAndSelectBoxChangeListener;
	private FocusListener sharedFocusListener;
	private InputListener sharedInputListener;

	private ColorPickerListener pickerListener;
	private TintImage tint;

	private boolean snapshotInProgress;
	private SnapshotUndoableActionGroup snapshots;

	//UI
	private boolean rebuiltScheduled;

	private VisTable propertiesTable;

	private VisTable idTable;

	private VisTable positionTable;
	private VisTable scaleTable;
	private VisTable originTable;
	private VisTable rotationTable;
	private VisTable tintTable;
	private VisTable flipTable;

	private ComponentSelectDialog componentSelectDialog;
	private VisTextButton addComponentButton;

	private Array<SpecificUITable> specificTables = new Array<>();
	private SpecificUITable activeSpecificTable;

	private Array<SpecificComponentTable> componentTables = new Array<>();
	private Array<SpecificComponentTable> activeComponentTables = new Array<>();

	private VisValidableTextField idField;
	private NumberInputField xField;
	private NumberInputField yField;
	private NumberInputField xScaleField;
	private NumberInputField yScaleField;
	private NumberInputField xOriginField;
	private NumberInputField yOriginField;
	private NumberInputField rotationField;
	private IndeterminateCheckbox xFlipCheck;
	private IndeterminateCheckbox yFlipCheck;
	private SceneModuleContainer sceneMC;

	public EntityProperties (SceneModuleContainer sceneMC, Tab parentSceneTab, Array<EntityProxy> entities) {
		super(true);
		sceneMC.injectModules(this);

		componentManipulator = sceneMC.getEntityEngineConfiguration().getSystem(VisComponentManipulator.class);

		this.sceneMC = sceneMC;
		this.entities = entities;
		this.parentTab = parentSceneTab;
		this.picker = colorPickerModule.getPicker();

		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.childrenOnly);
		setVisible(false);

		sharedChangeListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (actor instanceof VisCheckBox)
					throw new IllegalStateException("sharedChangeListener cannot be used for checkboxes, use sharedCheckBoxChangeListener instead");
				if (actor instanceof VisSelectBox)
					throw new IllegalStateException("sharedChangeListener cannot be used for selectBoxes, use sharedSelectBoxChangeListener instead");

				setValuesToEntity();
				parentTab.dirty();
			}
		};

		sharedChckAndSelectBoxChangeListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				beginSnapshot();
				setValuesToEntity();
				parentTab.dirty();
				endSnapshot();
			}
		};

		sharedFocusListener = new FocusListener() {
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (focused)
					beginSnapshot();
				else
					endSnapshot();
			}
		};

		sharedInputListener = new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					if (snapshotInProgress == false) beginSnapshot();
					setValuesToEntity();
					parentTab.dirty();
					endSnapshot();
					return true;
				}

				return false;
			}
		};

		pickerListener = new ColorPickerAdapter() {
			@Override
			public void finished (Color newColor) {
				for (EntityProxy entity : entities)
					entity.setColor(newColor);

				parentTab.dirty();
				tint.setColor(newColor);
				tint.setUnknown(false);
				picker.setListener(null);
				endSnapshot();
			}
		};

		createIdTable();
		createPositionTable();
		createScaleTable();
		createOriginTable();
		createRotationTintTable();
		createFlipTable();

		componentSelectDialog = new ComponentSelectDialog(this, clazz -> {
			try {
				if (getProxies().size == 0) return; //nothing is selected
				undoModule.execute(new ComponentAddAction(componentManipulator, getProxies(), clazz));
				rebuiltScheduled = true;
			} catch (ReflectiveOperationException e) {
				Log.exception(e);
				toastModule.show(new DetailsToast("Component creation failed!", e));
			}
		});

		addComponentButton = new VisTextButton("Add Component");
		addComponentButton.addListener(new VisChangeListener((event, actor) -> {
			boolean anyComponent = componentSelectDialog.build();
			if (anyComponent == false) {
				statusBarModule.setText("There isn't any available component");
				return;
			}
			getStage().addActor(componentSelectDialog);
			Vector2 pos = getStage().screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY() + componentSelectDialog.getHeight()));
			componentSelectDialog.setPosition(pos.x, pos.y);
			ActorUtils.keepWithinStage(getStage(), componentSelectDialog);
		}));

		registerSpecificTable(new TtfTextUITable());
		registerSpecificTable(new BMPTextUITable());
		registerSpecificTable(new MusicUITable());
		registerSpecificTable(new SoundUITable());
		registerSpecificTable(new ParticleEffectTable());
		registerSpecificTable(new GroupUITable());

		//TODO: [plugin] plugin entry point
		registerComponentTable(new RenderableComponentTable(sceneMC));
		registerComponentTable(new AutoComponentTable<>(sceneMC, ShaderComponent.class, true));
		registerComponentTable(new AutoComponentTable<>(sceneMC, PolygonComponent.class, true));
		registerComponentTable(new AutoComponentTable<>(sceneMC, PhysicsPropertiesComponent.class, true));
		registerComponentTable(new AutoComponentTable<>(sceneMC, VariablesComponent.class, true));
		registerComponentTable(new SpriterPropertiesComponentTable(sceneMC));

		propertiesTable = new VisTable(true);

		VisScrollPane scrollPane = new VisScrollPane(propertiesTable);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setFlickScroll(false);

		top();
		add(new VisLabel("Entity Properties")).row();
		add(scrollPane).fillX().expandX().padLeft(3).padRight(3);

		addListener(new EventStopper());

		pack();

		App.eventBus.register(this);
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	@Subscribe
	public void handleUndoableModuleEvent (UndoableModuleEvent event) {
		selectedEntitiesChanged();
	}

	private void createIdTable () {
		idTable = new VisTable(true);
		idTable.add(new VisLabel("ID"));
		idTable.add(idField = new VisValidableTextField()).expandX().fillX();
		idField.setProgrammaticChangeEvents(false);

		idField.addListener(sharedChangeListener);
		idField.addListener(sharedFocusListener);
		idField.addListener(sharedInputListener);
	}

	private void createPositionTable () {
		positionTable = new VisTable(true);
		positionTable.add(new VisLabel("Position")).width(LABEL_WIDTH);
		positionTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		positionTable.add(xField = createNewNumberField()).width(FIELD_WIDTH);
		positionTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		positionTable.add(yField = createNewNumberField()).width(FIELD_WIDTH);
	}

	private void createScaleTable () {
		scaleTable = new VisTable(true);
		scaleTable.add(new VisLabel("Scale")).width(LABEL_WIDTH);
		scaleTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		scaleTable.add(xScaleField = createNewNumberField()).width(FIELD_WIDTH);
		scaleTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		scaleTable.add(yScaleField = createNewNumberField()).width(FIELD_WIDTH);
	}

	private void createOriginTable () {
		originTable = new VisTable(true);
		originTable.add(new VisLabel("Origin")).width(LABEL_WIDTH);
		originTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		originTable.add(xOriginField = createNewNumberField()).width(FIELD_WIDTH);
		originTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		originTable.add(yOriginField = createNewNumberField()).width(FIELD_WIDTH);
	}

	private void createRotationTintTable () {
		tint = new TintImage();
		tint.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				beginSnapshot();
				picker.setColor(tint.getColor());
				picker.setListener(pickerListener);
				getStage().addActor(picker.fadeIn());
			}
		});

		tintTable = new VisTable(true);
		tintTable.add(new VisLabel("Tint"));
		tintTable.add(tint).size(20);

		rotationTable = new VisTable(true);
		rotationTable.add(new VisLabel("Rotation")).width(LABEL_WIDTH);
		rotationTable.add(new VisLabel(" ")).width(AXIS_LABEL_WIDTH);
		rotationTable.add(rotationField = createNewNumberField()).width(FIELD_WIDTH);
	}

	private void createFlipTable () {
		flipTable = new VisTable(true);

		flipTable.add(new VisLabel("Flip"));
		flipTable.add(xFlipCheck = new IndeterminateCheckbox("X"));
		flipTable.add(yFlipCheck = new IndeterminateCheckbox("Y"));

		xFlipCheck.addListener(sharedChckAndSelectBoxChangeListener);
		yFlipCheck.addListener(sharedChckAndSelectBoxChangeListener);
	}

	private void rebuildPropertiesTable () {
		propertiesTable.reset();
		TableUtils.setSpacingDefaults(propertiesTable);

		VisTable rotationTintTable = new VisTable(true);
		if (EntityUtils.isRotationSupportedForEntities(entities)) rotationTintTable.add(rotationTable);
		rotationTintTable.add().expand().fill();
		if (EntityUtils.isTintSupportedForEntities(entities)) rotationTintTable.add(tintTable);

		propertiesTable.defaults().padRight(0).fillX();
		propertiesTable.add(idTable).row();
		propertiesTable.add(positionTable).row();
		if (EntityUtils.isScaleSupportedForEntities(entities)) propertiesTable.add(scaleTable).row();
		if (EntityUtils.isOriginSupportedForEntities(entities)) propertiesTable.add(originTable).row();
		if (EntityUtils.isRotationSupportedForEntities(entities) || EntityUtils.isTintSupportedForEntities(entities))
			propertiesTable.add(rotationTintTable).maxWidth(new VisValue(context -> positionTable.getPrefWidth())).row();
		if (EntityUtils.isFlipSupportedForEntities(entities))
			propertiesTable.add(flipTable).right().fill(false).spaceBottom(2).row();

		activeSpecificTable = null;
		for (SpecificUITable table : specificTables) {
			if (checkEntityList(table)) {
				activeSpecificTable = table;
				propertiesTable.add(new Separator()).fillX().row();
				propertiesTable.add(table).row();
				break;
			}
		}

		activeComponentTables.clear();
		if (entities.size > 0) {
			Bag<Component> components = entities.get(0).getEntities().get(0).getComponents(new Bag<>());

			for (Component component : components) {
				if (component == null) continue;

				if (EntityUtils.isComponentCommon(component, entities)) {
					SpecificComponentTable componentTable = getComponentTable(component);

					if (componentTable != null) {
						activeComponentTables.add(componentTable);
						propertiesTable.add(new ComponentPanel(sceneMC, componentManipulator, component.getClass().getSimpleName(), componentTable));
						propertiesTable.row();
					}
				}
			}
		}

		if (groupSelected == false) {
			propertiesTable.addSeparator().padTop(0).padBottom(0).spaceTop(3).spaceBottom(3);
			propertiesTable.add(addComponentButton).spaceBottom(3).fill(false);
		}

		invalidateHierarchy();
	}

	private <T extends Component> SpecificComponentTable getComponentTable (T component) {
		if (componentTables.size == 0) return null;

		for (SpecificComponentTable table : componentTables) {
			if (table.getComponentClass().equals(component.getClass())) return table;
		}

		return null;
	}

	private boolean checkEntityList (SpecificUITable table) {
		if (entities.size == 0) return false;

		for (EntityProxy entity : entities) {
			if (table.isSupported(entity) == false) return false;
		}

		return true;
	}

	@Override
	public void setVisible (boolean visible) {
		super.setVisible(visible);
		invalidateHierarchy();
	}

	@Override
	public float getPrefHeight () {
		if (isVisible())
			return super.getPrefHeight() + 5;
		else
			return 0;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (rebuiltScheduled == true) {
			selectedEntitiesChanged();
			rebuiltScheduled = false;
		}
	}

	public void selectedEntitiesChanged () {
		rebuildPropertiesTable();
		updateValues();
	}

	public void selectedEntitiesValuesChanged () {
		updateValues();
	}

	public void beginSnapshot () {
		if (snapshotInProgress) endSnapshot();
		snapshotInProgress = true;

		snapshots = new SnapshotUndoableActionGroup();

		for (EntityProxy entity : entities) {
			snapshots.add(new SnapshotUndoableAction(entity));
		}
	}

	public void endSnapshot () {
		if (!snapshotInProgress) return;
		snapshotInProgress = false;

		snapshots.takeSecondSnapshot();
		snapshots.dropUnchanged();
		snapshots.finalizeGroup();
		if (snapshots.size() > 0)
			undoModule.add(snapshots);
	}

	public NumberInputField createNewNumberField () {
		return new NumberInputField(sharedFocusListener, sharedChangeListener);
	}

	public Array<EntityProxy> getProxies () {
		return entities;
	}

	public ChangeListener getSharedChangeListener () {
		return sharedChangeListener;
	}

	public ChangeListener getSharedCheckBoxChangeListener () {
		return sharedChckAndSelectBoxChangeListener;
	}

	public ChangeListener getSharedSelectBoxChangeListener () {
		return sharedChckAndSelectBoxChangeListener;
	}

	public FocusListener getSharedFocusListener () {
		return sharedFocusListener;
	}

	public InputListener getSharedInputListener () {
		return sharedInputListener;
	}

	public SceneModuleContainer getSceneModuleContainer () {
		return sceneMC;
	}

	public void setupStdPropertiesTextField (VisTextField textField) {
		textField.setProgrammaticChangeEvents(false);
		textField.addListener(sharedChangeListener);
		textField.addListener(sharedFocusListener);
		textField.addListener(sharedInputListener);
	}

	public Tab getParentTab () {
		return parentTab;
	}

	private void setTintUIForEntities () {
		Color firstColor = entities.first().getColor();
		for (EntityProxy entity : entities) {
			if (!firstColor.equals(entity.getColor())) {
				tint.setUnknown(true);
				return;
			}
		}

		tint.setUnknown(false);
		tint.setColor(firstColor);
	}

	private String getEntitiesFieldValue (FloatProxyValue floatProxyValue) {
		return EntityUtils.getEntitiesCommonFloatValue(entities, floatProxyValue);
	}

	private void setValuesToEntity () {
		for (int i = 0; i < entities.size; i++) {
			EntityProxy entity = entities.get(i);

			//TODO support indeterminate textfield
			if (groupSelected == false && idField.getText().equals("<?>") == false)
				entity.setId(idField.getText().equals("") ? null : idField.getText());

			entity.setPosition(FieldUtils.getFloat(xField, entity.getX()), FieldUtils.getFloat(yField, entity.getY()));

			if (EntityUtils.isScaleSupportedForEntities(entities))
				entity.setScale(FieldUtils.getFloat(xScaleField, entity.getScaleX()), FieldUtils.getFloat(yScaleField, entity.getScaleY()));

			if (EntityUtils.isOriginSupportedForEntities(entities))
				entity.setOrigin(FieldUtils.getFloat(xOriginField, entity.getOriginX()), FieldUtils.getFloat(yOriginField, entity.getOriginY()));

			if (EntityUtils.isRotationSupportedForEntities(entities))
				entity.setRotation(FieldUtils.getFloat(rotationField, entity.getRotation()));

			if (EntityUtils.isFlipSupportedForEntities(entities)) {
				if (xFlipCheck.isIndeterminate() == false)
					entity.setFlip(xFlipCheck.isChecked(), entity.isFlipY());

				if (yFlipCheck.isIndeterminate() == false)
					entity.setFlip(entity.isFlipX(), yFlipCheck.isChecked());
			}
		}

		if (activeSpecificTable != null) activeSpecificTable.setValuesToEntities();
		for (SpecificComponentTable table : activeComponentTables)
			table.setValuesToEntities();
	}

	private void updateValues () {
		groupSelected = false;
		entities.forEach(proxy -> {
			if (proxy instanceof GroupEntityProxy) groupSelected = true;
		});

		if (entities.size == 0) {
			setVisible(false);
		} else {
			setVisible(true);

			if (groupSelected) {
				idField.setText("<id cannot be set for group>");
				idField.setDisabled(true);
			} else {
				idField.setText(EntityUtils.getCommonId(entities));
				idField.setDisabled(false);
			}

			xField.setText(getEntitiesFieldValue(EntityProxy::getX));
			yField.setText(getEntitiesFieldValue(EntityProxy::getY));

			if (EntityUtils.isScaleSupportedForEntities(entities)) {
				xScaleField.setText(getEntitiesFieldValue(EntityProxy::getScaleX));
				yScaleField.setText(getEntitiesFieldValue(EntityProxy::getScaleY));
			}

			if (EntityUtils.isOriginSupportedForEntities(entities)) {
				xOriginField.setText(getEntitiesFieldValue(EntityProxy::getOriginX));
				yOriginField.setText(getEntitiesFieldValue(EntityProxy::getOriginY));
			}

			if (EntityUtils.isRotationSupportedForEntities(entities))
				rotationField.setText(getEntitiesFieldValue(EntityProxy::getRotation));

			if (EntityUtils.isTintSupportedForEntities(entities)) setTintUIForEntities();

			if (EntityUtils.isFlipSupportedForEntities(entities)) {
				EntityUtils.setCommonCheckBoxState(entities, xFlipCheck, EntityProxy::isFlipX);
				EntityUtils.setCommonCheckBoxState(entities, yFlipCheck, EntityProxy::isFlipY);
			}

			if (activeSpecificTable != null) activeSpecificTable.updateUIValues();

			for (SpecificComponentTable table : activeComponentTables) {
				table.updateUIValues();
			}
		}
	}

	public void loadSupportsSpecificTables (SupportModule supportModule) {
		for (EditorEntitySupport support : supportModule.getSupports()) {
			Array<SpecificUITable> uiTables = support.getUIPropertyTables();
			if (uiTables != null) {
				for (SpecificUITable table : uiTables)
					registerSpecificTable(table);
			}

			Array<SpecificComponentTable> componentTables = support.getComponentsUITables();
			if (componentTables != null) {
				for (SpecificComponentTable table : componentTables)
					registerComponentTable(table);
			}
		}
	}

	private void registerSpecificTable (SpecificUITable specificUITable) {
		specificTables.add(specificUITable);
		specificUITable.setProperties(this);
	}

	private void registerComponentTable (SpecificComponentTable table) {
		componentTables.add(table);
		table.setProperties(this);
	}

	private static class SnapshotUndoableActionGroup extends UndoableActionGroup {
		public SnapshotUndoableActionGroup () {
			super("Change Entities Properties");
		}

		public void dropUnchanged () {
			Iterator<UndoableAction> iterator = actions.iterator();

			while (iterator.hasNext()) {
				SnapshotUndoableAction action = (SnapshotUndoableAction) iterator.next();
				if (action.isSnapshotsEquals()) iterator.remove();
			}
		}

		public void takeSecondSnapshot () {
			for (UndoableAction action : actions) {
				SnapshotUndoableAction sAction = (SnapshotUndoableAction) action;
				sAction.takeSecondSnapshot();
			}
		}
	}

	private class SnapshotUndoableAction implements UndoableAction {
		private EntityProxy proxy;

		private IntMap<Bag<Component>> snapshot1 = new IntMap<>();
		private IntMap<Bag<Component>> snapshot2 = new IntMap<>();

		public SnapshotUndoableAction (EntityProxy proxy) {
			this.proxy = proxy;
			createSnapshot(snapshot1);
		}

		public void takeSecondSnapshot () {
			createSnapshot(snapshot2);
		}

		private void createSnapshot (IntMap<Bag<Component>> target) {
			proxy.getEntities().forEach(entity -> target.put(entity.getId(), sceneIO.cloneEntityComponents(entity.getComponents(new Bag<>()))));
		}

		public boolean isSnapshotsEquals () {
			return EqualsBuilder.reflectionEquals(snapshot1, snapshot2, true);
		}

		@Override
		public void execute () {
			proxy.reload();
			replaceComponents(snapshot2);
		}

		@Override
		public void undo () {
			proxy.reload();
			replaceComponents(snapshot1);
		}

		private void replaceComponents (IntMap<Bag<Component>> source) {
			proxy.getEntities().forEach(entity -> {
				Bag<Component> oldComponents = new Bag<>();
				entity.getComponents(oldComponents);
				EntityEdit editor = entity.edit();
				oldComponents.forEach(editor::remove);

				Bag<Component> newComponent = source.get(entity.getId());
				newComponent.forEach(editor::add);
			});
			proxy.reload();
		}

		@Override
		public String getActionName () {
			return "Change Entity Property";
		}
	}
}
