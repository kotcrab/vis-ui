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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.scene.EditorLayer;
import com.kotcrab.vis.editor.ui.WindowResultListener;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/** @author Kotcrab */
public class SelectLayerDialog extends VisWindow {
	private final WindowResultListener<EditorLayer> listener;

	private VisList<String> layerList;
	private final ObjectMap<String, EditorLayer> layersMap;

	public SelectLayerDialog (ImmutableArray<EditorLayer> layers, EditorLayer activeLayer, WindowResultListener<EditorLayer> listener) {
		super("Select Layer");
		this.listener = listener;

		setModal(true);
		addCloseButton();
		closeOnEscape();

		layerList = new VisList<>();

		VisTextButton cancelButton;

		TableUtils.setSpacingDefaults(this);
		defaults().left();

		VisTable buttonsTable = new VisTable(true);
		buttonsTable.add(cancelButton = new VisTextButton("Cancel"));
		VisTextButton okButton;
		buttonsTable.add(okButton = new VisTextButton("OK"));

		add(layerList).expand().fill().row();
		add(buttonsTable).padBottom(2).right();

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				listener.canceled();
				fadeOut();
			}
		});

		okButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				finishSelection();
			}
		});

		layerList.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if (getTapCount() == 2 && event.getButton() == Buttons.LEFT) finishSelection();
			}
		});

		layersMap = new ObjectMap<>();

		for (EditorLayer layer : layers) {
			if (layer.id == activeLayer.id) continue;

			layersMap.put(layer.name, layer);
		}

		layerList.setItems(layersMap.keys().toArray());

		pack();
		centerWindow();
		setWidth(getWidth() + 100);
	}

	private void finishSelection () {
		fadeOut();
		String layerName = layerList.getSelected();
		if (layerName == null) return;
		listener.finished(layersMap.get(layerName));
	}
}
