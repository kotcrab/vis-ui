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

package com.kotcrab.vis.editor.module.scene.entitymanipulator.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.proxy.GroupEntityProxy;
import com.kotcrab.vis.editor.util.gdx.EventStopper;
import com.kotcrab.vis.editor.util.gdx.VisChangeListener;
import com.kotcrab.vis.editor.util.gdx.VisValue;
import com.kotcrab.vis.editor.util.polygon.Clipper;
import com.kotcrab.vis.runtime.component.PolygonComponent;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/** @author Kotcrab */
public class PolygonTool extends BaseSelectionTool {
	private static final String NO_POLYGON_IN_SELECTION = "No polygon component in selected entity";
	private static final String NOTHING_SELECTED = "Select entity to edit it's polygon";
	private static final String SELECT_ONLY_ONE = "Select only one entity to edit polygon";

	private Color mainColor;
	private Color gray;

	private EntityProxy proxy;
	private PolygonComponent component;

	private VisTable uiTable;
	private VisTable buttonTable;

	private VisLabel statusLabel;
	private VisTextButton makeDefaultButton;
	private VisTextButton traceButton;

	@Override
	public void init () {
		super.init();
		initUI();

		mainColor = VisUI.getSkin().getColor("vis-blue");
		gray = Color.GRAY;
	}

	@Override
	public void render (ShapeRenderer shapeRenderer) {
		shapeRenderer.setProjectionMatrix(Editor.instance.getStage().getCamera().combined);

		Gdx.gl.glLineWidth(3);
		shapeRenderer.setColor(mainColor);
		shapeRenderer.begin(ShapeType.Line);
		if (component != null) {
			for (Vector2 point : component.points) {
				float size = 10.0f;
				Vector3 res = camera.project(new Vector3(point.x, point.y, 0));
				shapeRenderer.rect(res.x - size / 2, res.y - size / 2, size, size);
			}
		}
		shapeRenderer.end();
	}

	private void initUI () {
		uiTable = new VisTable(true) {
			@Override
			public float getPrefHeight () {
				return 110;
			}
		};

		uiTable.setBackground(VisUI.getSkin().getDrawable("window-bg"));
		uiTable.setTouchable(Touchable.enabled);
		uiTable.addListener(new EventStopper());

		uiTable.top().left();
		uiTable.defaults().expandX().fillX().left();

		uiTable.add(new VisLabel("Polygon Tool", Align.center)).expandX().fillX().top();
		uiTable.row();

		statusLabel = new VisLabel();
		statusLabel.setWrap(true);
		statusLabel.setAlignment(Align.center);

		buttonTable = new VisTable(true) {
			@Override
			public float getPrefHeight () {
				if (isVisible())
					return super.getPrefHeight();
				else
					return 0;
			}

			@Override
			public void setVisible (boolean visible) {
				super.setVisible(visible);
				invalidateHierarchy();
			}
		};
		buttonTable.setVisible(false);
		buttonTable.add(makeDefaultButton = new VisTextButton("Set from bounds")).row();
		buttonTable.add(traceButton = new VisTextButton("Auto trace")).row();

		uiTable.add(statusLabel).pad(0, 3, 0, 3).height(new VisValue(context -> context.isVisible() ? 30 : 0)).row();
		uiTable.add(buttonTable).spaceBottom(4);

		makeDefaultButton.addListener(new VisChangeListener((event, actor) -> makeDefaultPolygon()));
	}

	private void makeDefaultPolygon () {
		Rectangle rect = proxy.getBoundingRectangle();
		//TODO action!!!
		component.points.clear();

		component.points.add(new Vector2(rect.x, rect.y));
		component.points.add(new Vector2(rect.x + rect.width, rect.y));
		component.points.add(new Vector2(rect.x, rect.y + rect.height));
		component.points.add(new Vector2(rect.x + rect.width, rect.y + rect.height));

		component.vertices = Clipper.polygonize(App.DEFAULT_POLYGONIZER, component.points.toArray(Vector2.class));
		entityManipulator.selectedEntitiesChanged();
	}

	@Override
	public void activated () {
		super.activated();
		selectedEntitiesChanged();
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		return super.touchDown(event, x, y, pointer, button);
	}

	@Override
	public void selectedEntitiesChanged () {
		super.selectedEntitiesChanged();
		ImmutableArray<EntityProxy> selection = entityManipulator.getSelectedEntities();

		component = null;
		proxy = null;

		statusLabel.setVisible(true);
		buttonTable.setVisible(false);

		if (selection.size() == 0) {
			statusLabel.setText(NOTHING_SELECTED);
			return;
		}

		if (selection.size() > 1 || selection.first() instanceof GroupEntityProxy) {
			statusLabel.setText(SELECT_ONLY_ONE);
			return;
		}

		proxy = selection.first();

		if (proxy.hasComponent(PolygonComponent.class) == false) {
			statusLabel.setText(NO_POLYGON_IN_SELECTION);
			return;
		}

		component = proxy.getEntities().get(0).getComponent(PolygonComponent.class);

		statusLabel.setVisible(false);
		buttonTable.setVisible(true);
		uiTable.invalidateHierarchy();
	}

	@Override
	public VisTable getToolPropertiesUI () {
		return uiTable;
	}

}
