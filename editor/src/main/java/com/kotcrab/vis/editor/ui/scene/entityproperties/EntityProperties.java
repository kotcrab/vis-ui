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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterable;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.entity.EntityScheme.CloningPolicy;
import com.kotcrab.vis.editor.entity.EntityScheme.UUIDPolicy;
import com.kotcrab.vis.editor.module.editor.*;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.action.ComponentAddAction;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.GroupSelectionFragment;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.SelectionFragment;
import com.kotcrab.vis.editor.module.scene.system.VisComponentManipulator;
import com.kotcrab.vis.editor.plugin.api.ComponentTableProvider;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.toast.DetailsToast;
import com.kotcrab.vis.editor.util.gdx.ArrayUtils;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.undo.MonoUndoableActionGroup;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ActorUtils;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

/**
 * Entity properties dialog, used to display and change all data about currently selected entities. Multiple selection
 * is supported, even when entities have different values, <?> is used in float input fields, and intermediate checkbox are used
 * for boolean support. Undo is supported. Plugin can add custom properties tables (see {@link ComponentTable}),
 * but they must support all base features of this dialog (multiple selection support, undo, etc.). See any class
 * from 'specifictable' and 'components' child packages for examples.
 * @author Kotcrab
 */
public class EntityProperties extends VisTable {
	public static final int LABEL_WIDTH = 60;
	public static final int AXIS_LABEL_WIDTH = 10;
	public static final int FIELD_WIDTH = 65;
	public static final int ROW_WIDTH = 245;

	private StatusBarModule statusBarModule;
	private ToastModule toastModule;
	private ColorPickerModule colorPickerModule;
	private ExtensionStorageModule extensionStorage;
	private ClonerModule cloner;

	private UndoModule undoModule;
	private EntityManipulatorModule entityManipulator;

	private VisComponentManipulator componentManipulator;

	private Tab parentTab;

	private boolean groupSelected;

	private ChangeListener sharedChangeListener;
	private ChangeListener sharedChckAndSelectBoxChangeListener;
	private FocusListener sharedFocusListener;
	private InputListener sharedInputListener;

	private boolean snapshotInProgress;
	private SnapshotUndoableActionGroup snapshots;

	private boolean uiValuesUpdateRequested;

	private Array<ComponentTable<?>> componentTables = new Array<>();
	private Array<ComponentTable<?>> activeComponentTables = new Array<>();

	private SceneModuleContainer sceneMC;

	//UI
	private VisTable propertiesTable;
	private BasicEntityPropertiesTable basicProperties;
	private GroupPropertiesTable groupProperties;

	private ComponentSelectDialog componentSelectDialog;
	private VisTextButton addComponentButton;

	public EntityProperties (SceneModuleContainer sceneMC, Tab parentSceneTab) {
		super(true);
		sceneMC.injectModules(this);

		this.sceneMC = sceneMC;
		this.parentTab = parentSceneTab;

		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.enabled);
		setVisible(false);

		sharedChangeListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (actor instanceof VisCheckBox)
					throw new IllegalStateException("sharedChangeListener cannot be used for checkboxes, use sharedCheckBoxChangeListener instead");
				if (actor instanceof VisSelectBox)
					throw new IllegalStateException("sharedChangeListener cannot be used for selectBoxes, use sharedSelectBoxChangeListener instead");

				setValuesToEntities();
				parentTab.dirty();
			}
		};

		sharedChckAndSelectBoxChangeListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				beginSnapshot();
				setValuesToEntities();
				parentTab.dirty();
				endSnapshot();
			}
		};

		sharedFocusListener = new FocusListener() {
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					beginSnapshot();
				} else {
					endSnapshot();
				}
			}
		};

		sharedInputListener = new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					if (snapshotInProgress == false) beginSnapshot();
					setValuesToEntities();
					parentTab.dirty();
					endSnapshot();
					return true;
				}

				return false;
			}
		};

		basicProperties = new BasicEntityPropertiesTable(this, colorPickerModule.getPicker());
		groupProperties = new GroupPropertiesTable(this);

		componentSelectDialog = new ComponentSelectDialog(sceneMC, this, clazz -> {
			try {
				ImmutableArray<EntityProxy> entities = getSelectedEntities();

				if (entities.size() == 0) return; //nothing is selected
				undoModule.execute(new ComponentAddAction(sceneMC, entities, clazz));
			} catch (ReflectiveOperationException e) {
				Log.exception(e);
				toastModule.show(new DetailsToast("Component creation failed!", e));
			}
		});

		addComponentButton = new VisTextButton("Add Component");
		addComponentButton.addListener(new VisChangeListener((event, actor) -> {
			boolean anyComponentAvailable = componentSelectDialog.build();
			if (anyComponentAvailable == false) {
				statusBarModule.setText("There isn't any available component");
				return;
			}
			getStage().addActor(componentSelectDialog);
			Vector2 pos = getStage().screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY() + componentSelectDialog.getHeight()));
			componentSelectDialog.setPosition(pos.x, pos.y);
			ActorUtils.keepWithinStage(getStage(), componentSelectDialog);
		}));

		reloadComponentTables();

		propertiesTable = new VisTable(true);

		VisScrollPane scrollPane = new VisScrollPane(propertiesTable);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setFlickScroll(false);

		top();
		add(new VisLabel("Entity Properties")).row();
		add(scrollPane).fillX().expandX().padLeft(3).padRight(3);

		pack();
	}

	private void rebuildPropertiesTable () {
		propertiesTable.reset();
		TableUtils.setSpacingDefaults(propertiesTable);

		ImmutableArray<EntityProxy> entities = getSelectedEntities();

		basicProperties.rebuildPropertiesTable();
		propertiesTable.add(basicProperties).row();
		if (groupSelected) {
			propertiesTable.addSeparator().padTop(0).padBottom(0).spaceTop(3).spaceBottom(3);
			propertiesTable.add(groupProperties).row();
		}

		activeComponentTables.clear();
		if (entities.size() > 0) {
			Bag<Component> components = entities.get(0).getEntity().getComponents(new Bag<>());

			for (Component component : components) {
				if (component == null) continue;

				if (EntityUtils.isComponentCommon(component, entities)) {
					ComponentTable<?> componentTable = getComponentTable(component);

					if (componentTable != null) {
						activeComponentTables.add(componentTable);
						propertiesTable.add(new ComponentPanel(sceneMC, componentManipulator, component.getClass().getSimpleName(), componentTable)).growX();
						propertiesTable.row();
					}
				}
			}
		}

		revalidateFieldLocks();

		if (groupSelected == false) {
			propertiesTable.addSeparator().padTop(0).padBottom(0).spaceTop(3).spaceBottom(3);
			propertiesTable.add(addComponentButton).spaceBottom(3).fill(false);
		}

		invalidateHierarchy();
	}

	private <T extends Component> ComponentTable<T> getComponentTable (T component) {
		if (componentTables.size == 0) return null;

		for (ComponentTable<?> table : componentTables) {
			if (table.getComponentClass().equals(component.getClass())) {
				return (ComponentTable<T>) table;
			}
		}

		return null;
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

	/** This must not be called from {@link ComponentTable} */
	public void selectedEntitiesChanged () {
		groupSelected = ArrayUtils.has(getFragmentedSelection(), GroupSelectionFragment.class);
		rebuildPropertiesTable();
		updateUIValues(true);
	}

	/** This should not be called from {@link ComponentTable} */
	public void selectedEntitiesValuesChanged () {
		updateUIValues(true);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (uiValuesUpdateRequested) {
			uiValuesUpdateRequested = false;
			updateUIValues(false);
		}
	}

	/**
	 * Called from {@link #setValuesToEntities()} when some value was modified and UI requires update. Update will be
	 * performed on next frame.
	 */
	public void requestUIValuesUpdate () {
		uiValuesUpdateRequested = true;
	}

	public void beginSnapshot () {
		if (snapshotInProgress) endSnapshot();
		snapshotInProgress = true;

		snapshots = new SnapshotUndoableActionGroup();

		for (EntityProxy entity : getSelectedEntities()) {
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
		entityManipulator.getSceneOutline().rebuildOutline();
	}

	public NumberInputField createNewNumberField () {
		return new NumberInputField(sharedFocusListener, sharedChangeListener);
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

	public void setupStdCheckbox (IndeterminateCheckbox checkBox) {
		checkBox.setProgrammaticChangeEvents(false);
		checkBox.addListener(sharedChckAndSelectBoxChangeListener);
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

	private void setValuesToEntities () {
		basicProperties.setValuesToEntity();
		if (groupSelected) groupProperties.setValuesToSceneGroupData();

		for (ComponentTable<?> table : new ArrayIterable<>(activeComponentTables))
			table.setValuesToEntities();
	}

	private void updateUIValues (boolean updateInvalidFields) {
		ImmutableArray<EntityProxy> entities = getSelectedEntities();

		if (entities.size() == 0) {
			setVisible(false);
		} else {
			setVisible(true);

			basicProperties.updateUIValues(updateInvalidFields);
			if (groupSelected) groupProperties.updateUIValues();

			for (ComponentTable<?> table : activeComponentTables) {
				table.updateUIValues();
			}
		}
	}
	
	public void reloadComponents() {
		reloadComponentTables();
		componentSelectDialog.reloadComponents();
		rebuildPropertiesTable();
	}
	
	private void reloadComponentTables() {
		componentTables.clear();
		for (ComponentTableProvider provider : extensionStorage.getComponentTableProviders()) {
			ComponentTable<?> table = provider.provide(sceneMC);
			componentTables.add(table);
			table.setProperties(this);
		}
	}

	public void lockField (BasicEntityPropertiesTable.LockableField field) {
		basicProperties.lockField(field);
	}

	/**
	 * Notifies {@link EntityProperties} that new values will change field lock status and revalidation is needed.
	 */
	public void revalidateFieldLocks () {
		basicProperties.unlockAllFields();
		for (EntityProxy proxy : getSelectedEntities()) {
			Bag<Component> components = proxy.getEntity().getComponents(new Bag<>());

			for (Component component : components) {
				if (component == null) continue;
				Optional.ofNullable(getComponentTable(component))
						.ifPresent(componentTable -> componentTable.lockFields(component));
			}
		}
	}

	public boolean isGroupSelected () {
		return groupSelected;
	}

	public ImmutableArray<EntityProxy> getSelectedEntities () {
		return entityManipulator.getSelectedEntities();
	}

	public ImmutableArray<SelectionFragment> getFragmentedSelection () {
		return entityManipulator.getFragmentedSelection();
	}

	private static class SnapshotUndoableActionGroup extends MonoUndoableActionGroup<SnapshotUndoableAction> {
		public SnapshotUndoableActionGroup () {
			super("Change Entity Properties", "Change Entities Properties");
		}

		public void dropUnchanged () {
			Iterator<SnapshotUndoableAction> iterator = actions.iterator();

			while (iterator.hasNext()) {
				SnapshotUndoableAction action = iterator.next();
				if (action.isSnapshotsEquals()) iterator.remove();
			}
		}

		public void takeSecondSnapshot () {
			for (SnapshotUndoableAction action : actions) {
				action.takeSecondSnapshot();
			}
		}
	}

	private class SnapshotUndoableAction implements UndoableAction {
		private EntityProxy proxy;

		private ObjectMap<UUID, EntityScheme> snapshot1 = new ObjectMap<>();
		private ObjectMap<UUID, EntityScheme> snapshot2 = new ObjectMap<>();

		public SnapshotUndoableAction (EntityProxy proxy) {
			this.proxy = proxy;
			createSnapshot(snapshot1);
		}

		public void takeSecondSnapshot () {
			createSnapshot(snapshot2);
		}

		private void createSnapshot (ObjectMap<UUID, EntityScheme> target) {
			target.put(proxy.getUUID(), EntityScheme.clonedOf(proxy.getEntity(), cloner.getCloner(), CloningPolicy.SKIP_INVISIBLE));
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

		private void replaceComponents (ObjectMap<UUID, EntityScheme> source) {
			Entity entity = proxy.getEntity();
			entity.deleteFromWorld();

			EntityScheme newScheme = source.get(proxy.getUUID());
			newScheme.build(sceneMC.getEntityEngine(), cloner.getCloner(), UUIDPolicy.PRESERVE);

			sceneMC.updateEntitiesStates();
			proxy.reload();
		}

		@Override
		public String getActionName () {
			return "Change Entity Property";
		}
	}
}
