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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.scene.EntityManipulatorModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.Layer;
import com.kotcrab.vis.editor.util.DefaultInputDialogListener;
import com.kotcrab.vis.editor.util.gdx.EventStopper;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.editor.util.gdx.VisChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;

public class LayersDialog extends VisTable {
	private static final Drawable SELECTION = VisUI.getSkin().getDrawable("list-selection");
	private static final VisImageButtonStyle BUTTON_STYLE = VisUI.getSkin().get("default", VisImageButtonStyle.class);
	private static final VisImageButtonStyle BUTTON_BLUE_STYLE = VisUI.getSkin().get("blue", VisImageButtonStyle.class);

	private final EditorScene scene;
	private EntityManipulatorModule entityManipulator;

	private VisTable layersTable;
	private VisImageButton layerUpButton;
	private VisImageButton layerDownButton;
	private VisImageButton layerRemoveButton;

	public LayersDialog (EntityManipulatorModule entityManipulator, EditorScene scene) {
		super(true);
		this.entityManipulator = entityManipulator;
		this.scene = scene;
		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.enabled);

		VisImageButton layerAddButton = new VisImageButton(Assets.getIcon(Icons.LAYER_ADD));
		layerUpButton = new VisImageButton(Assets.getIcon(Icons.LAYER_UP));
		layerDownButton = new VisImageButton(Assets.getIcon(Icons.LAYER_DOWN));
		layerRemoveButton = new VisImageButton(Assets.getIcon(Icons.LAYER_REMOVE));

		layerUpButton.setGenerateDisabledImage(true);
		layerDownButton.setGenerateDisabledImage(true);
		layerRemoveButton.setGenerateDisabledImage(true);

		layerAddButton.addListener(new VisChangeListener((event, actor) ->
				DialogUtils.showInputDialog(getStage(), "New Layer", "Name:", true,
						input -> scene.getLayerByName(input) == null,
						(DefaultInputDialogListener) input -> {
							scene.layers.add(new Layer(input));
							rebuildLayersTable();
							entityManipulator.sceneDirty();
						})));

		layerUpButton.addListener(new VisChangeListener((event, actor) -> {
			int index = scene.layers.indexOf(scene.activeLayer, true);
			scene.layers.swap(index, index - 1);
			rebuildLayersTable();
			entityManipulator.sceneDirty();
		}));

		layerDownButton.addListener(new VisChangeListener((event, actor) -> {
			int index = scene.layers.indexOf(scene.activeLayer, true);
			scene.layers.swap(index, index + 1);
			rebuildLayersTable();
			entityManipulator.sceneDirty();
		}));

		layerRemoveButton.addListener(new VisChangeListener((event, actor) ->
				DialogUtils.showOptionDialog(getStage(), "Delete Layer", "Are you sure you want to delete layer '" + scene.activeLayer.name + "'?",
						OptionDialogType.YES_NO, new OptionDialogAdapter() {
							@Override
							public void yes () {
								scene.layers.removeValue(scene.activeLayer, true);
								rebuildLayersTable();
								entityManipulator.sceneDirty();
							}
						})));

		layersTable = new VisTable();

		VisScrollPane layersScrollPane = new VisScrollPane(layersTable);
		layersScrollPane.setScrollingDisabled(true, false);
		layersScrollPane.setFadeScrollBars(false);

		top();
		left();
		defaults().left();
		defaults().padLeft(5).padRight(5);
		add(new VisLabel("Layers")).center().row();
		add(layersScrollPane).expandX().fillX().row();
		addSeparator();
		add(TableBuilder.build(layerAddButton, layerUpButton, layerDownButton, layerRemoveButton)).padBottom(8);

		addListener(new EventStopper());

		rebuildLayersTable();
	}

	private void selectedLayer (Layer layer) {
		entityManipulator.switchLayer(layer);

		layerUpButton.setDisabled(false);
		layerDownButton.setDisabled(false);

		int index = scene.layers.indexOf(layer, true);
		if (index == 0) layerUpButton.setDisabled(true);
		if (index == scene.layers.size - 1) layerDownButton.setDisabled(true);
	}

	private void rebuildLayersTable () {
		layersTable.clearChildren();

		for (Layer layer : scene.layers) {
			LayerItem item = new LayerItem(layer);
			layersTable.add(item).expandX().fillX().row();

			if (layer == scene.activeLayer)
				item.select();
		}

		if (scene.layers.size == 1) {
			layerDownButton.setDisabled(true);
			layerUpButton.setDisabled(true);
			layerRemoveButton.setDisabled(true);
		} else
			layerRemoveButton.setDisabled(false);

	}

	private void deselectAll () {
		for (Actor a : layersTable.getChildren()) {
			if (a instanceof LayerItem) {
				LayerItem item = (LayerItem) a;
				item.deselect();
			}
		}
	}

	private class LayerItem extends VisTable {
		private Layer layer;
		private VisImageButton eyeButton;
		private VisImageButton lockButton;

		public LayerItem (Layer layer) {
			super(true);
			this.layer = layer;

			setTouchable(Touchable.enabled);

			eyeButton = new VisImageButton("default");
			lockButton = new VisImageButton("default");
			updateButtonsImages();

			eyeButton.addListener(new VisChangeListener((event, actor) -> {
				layer.visible = !layer.visible;
				updateButtonsImages();
				entityManipulator.sceneDirty();
			}));

			lockButton.addListener(new VisChangeListener((event, actor) -> {
				layer.locked = !layer.locked;
				updateButtonsImages();

				if (layer.locked)
					entityManipulator.resetSelection();

				entityManipulator.sceneDirty();
			}));

			pad(3);
			add(eyeButton);
			add(lockButton);
			add(new VisLabel(layer.name)).expandX().fillX();

			addListener(new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					select();
					return true;
				}
			});

		}

		private void updateButtonsImages () {
			eyeButton.getStyle().imageUp = layer.visible ? Assets.getIcon(Icons.EYE) : Assets.getIcon(Icons.EYE_DISABLED);
			lockButton.getStyle().imageUp = layer.locked ? Assets.getIcon(Icons.LOCKED) : Assets.getIcon(Icons.UNLOCKED);
		}

		public void select () {
			deselectAll();
			eyeButton.getStyle().over = BUTTON_BLUE_STYLE.over;
			lockButton.getStyle().over = BUTTON_BLUE_STYLE.over;
			eyeButton.getStyle().up = BUTTON_BLUE_STYLE.up;
			lockButton.getStyle().up = BUTTON_BLUE_STYLE.up;
			eyeButton.getStyle().down = BUTTON_BLUE_STYLE.down;
			lockButton.getStyle().down = BUTTON_BLUE_STYLE.down;
			setBackground(SELECTION);

			selectedLayer(layer);
		}

		public void deselect () {
			setBackground((Drawable) null);
			eyeButton.getStyle().over = BUTTON_STYLE.over;
			lockButton.getStyle().over = BUTTON_STYLE.over;
			eyeButton.getStyle().up = BUTTON_STYLE.up;
			lockButton.getStyle().up = BUTTON_STYLE.up;
			eyeButton.getStyle().down = BUTTON_STYLE.down;
			lockButton.getStyle().down = BUTTON_STYLE.down;
		}
	}
}
