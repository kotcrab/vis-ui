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

package com.kotcrab.vis.editor.module.scene.entitymanipulator.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.module.editor.DisableableDialogsModule;
import com.kotcrab.vis.editor.module.editor.DisableableDialogsModule.DefaultDialogOption;
import com.kotcrab.vis.editor.module.editor.DisableableDialogsModule.DisableableOptionDialog;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.scene.action.ChangePolygonAction;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.dialog.PolygonAutoTraceDialog;
import com.kotcrab.vis.editor.util.polygon.Clipper;
import com.kotcrab.vis.editor.util.polygon.Clipper.Polygonizer;
import com.kotcrab.vis.editor.util.polygon.PolygonUtils;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.runtime.assets.TextureAssetDescriptor;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisPolygon;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.value.VisValue;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.Optional;

/** @author Kotcrab */
public class PolygonTool extends BaseSelectionTool {
	public static final String TAG = "PolygonTool";
	public static final String TOOL_ID = App.PACKAGE + ".tools.PolygonTool";

	public static final Polygonizer DEFAULT_POLYGONIZER = Polygonizer.EWJORDAN;

	private static final String NO_POLYGON_IN_SELECTION = "No polygon component in selected entity";
	private static final String NOTHING_SELECTED = "Select entity to edit it's polygon";
	private static final String SELECT_ONLY_ONE = "Select only one entity to edit polygon";
	private static final float POLYGON_RECT_SIZE = 16f;

	private StatusBarModule statusBar;
	private DisableableDialogsModule disableableDialogs;
	private Stage stage;

	private Color lineOverColor = new Color(0, 88 / 255f, 131 / 255f, 1);
	private Color mainColor = VisUI.getSkin().getColor("vis-blue");

	private TextureRegion white = VisUI.getSkin().getRegion("white");
	private TextureRegion polygon = Assets.getMiscRegion("polygon");
	private TextureRegion polygonOver = Assets.getMiscRegion("polygon-over");
	private TextureRegion polygonDown = Assets.getMiscRegion("polygon-down");

	private EntityProxy proxy;
	private VisPolygon component;
	private Vector2 selectedVertex;
	private Vector2 overVertex;
	private Vector2 lineOverStartVertex;

	private Array<Edge> drawnFacesLines = new Array<>();

	private ChangePolygonAction changePolygonAction;

	private Vector3 tmpVector = new Vector3();
	private Vector2 tmpVector2 = new Vector2(); //this is not second temp vector, this is 2d vector
	private Rectangle tmpRect = new Rectangle();

	//UI
	private VisTable uiTable;
	private VisTable buttonTable;

	private VisLabel statusLabel;
	private VisTextButton traceButton;
	private VisCheckBox dynamicUpdateCheck;

	@Override
	public void init () {
		super.init();
		initUI();
	}

	@Override
	public void render (Batch batch) {
		lineOverStartVertex = null;

		if (component != null) {
			batch.setProjectionMatrix(stage.getCamera().combined);

			camera.unproject(tmpVector.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			float worldMouseX = tmpVector.x;
			float worldMouseY = tmpVector.y;

			drawnFacesLines.clear();
			if ((dynamicUpdateCheck.isChecked() == false && Gdx.input.isButtonPressed(Buttons.LEFT)) == false) {
				batch.setColor(Color.GRAY);
				if (component.faces != null) {
					for (int i = 0; i < component.faces.length; i++) {
						Vector2[] faces = component.faces[i];

						for (int j = 1; j < faces.length; j++) {
							drawFaceLine(batch, faces[j], faces[j - 1]);
						}

						drawFaceLine(batch, faces[0], faces[faces.length - 1]);
					}
				}
			}

			Array<Vector2> vertices = component.vertices;
			batch.setColor(mainColor);
			for (int i = 1; i < vertices.size; i++) {
				drawPolygonBorderLine(batch, vertices.get(i), vertices.get(i - 1), worldMouseX, worldMouseY);
			}

			if (vertices.size > 1) {
				drawPolygonBorderLine(batch, vertices.get(0), vertices.get(vertices.size - 1), worldMouseX, worldMouseY);
			}

			batch.setColor(Color.WHITE);
			for (Vector2 vertex : vertices) {
				tmpVector.set(vertex.x, vertex.y, 0);
				camera.project(tmpVector);

				TextureRegion region = polygon;

				if (vertex == selectedVertex)
					region = polygonDown;
				else if (vertex == overVertex)
					region = polygonOver;

				batch.draw(region, tmpVector.x - POLYGON_RECT_SIZE / 2, tmpVector.y - POLYGON_RECT_SIZE / 2);
			}
		}
	}

	private void drawFaceLine (Batch batch, Vector2 vert1, Vector2 vert2) {
		if (wasLineDrawn(vert1, vert2, 1f / scene.pixelsPerUnit) == false) {
			drawnFacesLines.add(new Edge(vert1, vert2));
			drawLine(batch, vert1, vert2, 2);
		}
	}

	private boolean wasLineDrawn (Vector2 start, Vector2 end, float epsilon) {
		for (Edge edge : drawnFacesLines) {
			if (edge.epsilonEquals(start, end, epsilon))
				return true;
		}

		return false;
	}

	private void drawPolygonBorderLine (Batch batch, Vector2 start, Vector2 end, float mouseX, float mouseY) {
		if (overVertex == null && isPointOnLine(start, end, mouseX, mouseY, 0.3f / scene.pixelsPerUnit)) {
			batch.setColor(lineOverColor);
			lineOverStartVertex = start;
		} else {
			batch.setColor(mainColor);
		}

		drawLine(batch, start, end, 3);
	}

	private void drawLine (Batch batch, Vector2 vert1, Vector2 vert2, int thickness) {
		tmpVector.set(vert1.x, vert1.y, 0);
		camera.project(tmpVector);

		float x1 = tmpVector.x;
		float y1 = tmpVector.y;

		tmpVector.set(vert2.x, vert2.y, 0);
		camera.project(tmpVector);

		drawLine(batch, x1, y1, tmpVector.x, tmpVector.y, thickness);
	}

	private void drawLine (Batch batch, float x1, float y1, float x2, float y2, int thickness) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		float rad = (float) Math.atan2(dy, dx);

		batch.draw(white, x1, y1, 0, 0, dist, thickness, 1, 1, rad * MathUtils.radiansToDegrees);
	}

	private boolean isPointOnLine (Vector2 start, Vector2 end, float x, float y, float epsilon) {
		tmpVector2.set(x, y);
		float d1 = start.dst(tmpVector2) + end.dst(tmpVector2);
		float d2 = start.dst(end);
		return (Math.abs(d1 - d2) <= epsilon);
	}

	private boolean isInsidePoint (Vector2 point, float x, float y, float epsilon) {
		camera.project(tmpVector.set(point, 0));

		return tmpRect.set(tmpVector.x - epsilon / 2, tmpVector.y - epsilon / 2, epsilon, epsilon).contains(x, y);
	}

	private boolean polygonLineIntersection (Vector2 a, Vector2 b, Vector2 c, Vector2 d) {
		//ignore coincident lines
		if (a.x == c.x || a.y == c.y || b.x == d.x || b.y == d.y) return false;
		if (a.x == d.x || a.y == d.y || b.x == c.x || b.y == c.y) return false;

		float denominator = ((b.x - a.x) * (d.y - c.y)) - ((b.y - a.y) * (d.x - c.x));
		float numerator1 = ((a.y - c.y) * (d.x - c.x)) - ((a.x - c.x) * (d.y - c.y));
		float numerator2 = ((a.y - c.y) * (b.x - a.x)) - ((a.x - c.x) * (b.y - a.y));

		if (denominator == 0) return numerator1 == 0 && numerator2 == 0;

		float r = numerator1 / denominator;
		float s = numerator2 / denominator;

		return (r >= 0 && r <= 1) && (s >= 0 && s <= 1);
	}

	private void makeDefaultPolygon () {
		Rectangle rect = proxy.getBoundingRectangle();

		ChangePolygonAction action = new ChangePolygonAction(entityManipulator, proxy);

		component.vertices.clear();
		component.vertices.add(new Vector2(rect.x, rect.y));
		component.vertices.add(new Vector2(rect.x + rect.width, rect.y));
		component.vertices.add(new Vector2(rect.x + rect.width, rect.y + rect.height));
		component.vertices.add(new Vector2(rect.x, rect.y + rect.height));

		updateComponentFaces();

		action.takeSnapshot();
		undoModule.add(action);
		entityManipulator.selectedEntitiesChanged();
	}

	@Override
	public void activated () {
		super.activated();
		selectedEntitiesChanged();
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		camera.unproject(tmpVector.set(x, y, 0));

		float worldX = tmpVector.x;
		float worldY = tmpVector.y;

		camera.project(tmpVector);

		x = tmpVector.x;
		y = tmpVector.y;

		if (button == Buttons.LEFT && component != null) {
			if (lineOverStartVertex != null) {
				int vertexIndex = component.vertices.indexOf(lineOverStartVertex, true);
				ChangePolygonAction action = new ChangePolygonAction(entityManipulator, proxy);

				Vector2 newVertex = new Vector2(worldX, worldY);
				component.vertices.insert(vertexIndex, newVertex);
				overVertex = newVertex;

				updateComponentFaces();
				action.takeSnapshot();
				undoModule.add(action);
				entityManipulator.selectedEntitiesChanged();

				changePolygonAction = new ChangePolygonAction(entityManipulator, proxy);

				return true;
			}

			if (overVertex != null) {
				changePolygonAction = new ChangePolygonAction(entityManipulator, proxy);
			}

			for (Vector2 v : component.vertices) {
				if (isInsidePoint(v, x, y, POLYGON_RECT_SIZE)) {
					return true;
				}
			}
		}

		return super.touchDown(event, x, y, pointer, button);
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		super.touchDragged(event, x, y, pointer);
		camera.unproject(tmpVector.set(x, y, 0));

		x = tmpVector.x;
		y = tmpVector.y;

		if (overVertex != null) {
			overVertex.set(x, y);
			if (dynamicUpdateCheck.isChecked()) updateComponentFaces();
			dragged = true;
			entityManipulator.selectedEntitiesValuesChanged();
		}
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		camera.unproject(tmpVector.set(x, y, 0));
		camera.project(tmpVector);

		x = tmpVector.x;
		y = tmpVector.y;

		if (changePolygonAction != null) {
			if (PolygonUtils.isDegenerate(component.faces)) {
				changePolygonAction.takeSnapshot();
				changePolygonAction.undo();
				statusBar.setText("Polygon is degenerate", Color.RED, 3);
			} else if (checkCurrentVertexIntersection()) {
				changePolygonAction.takeSnapshot();
				changePolygonAction.undo();
				statusBar.setText("Invalid intersecting polygon", Color.RED, 3);
			} else {
				updateComponentFaces();
				changePolygonAction.takeSnapshot();
				undoModule.add(changePolygonAction);
				entityManipulator.selectedEntitiesChanged();
			}
			changePolygonAction = null;
		}

		if (button == Buttons.LEFT) {
			selectedVertex = null;
			if (component != null) {
				for (Vector2 v : component.vertices) {
					if (isInsidePoint(v, x, y, POLYGON_RECT_SIZE)) {
						selectedVertex = v;
						resetAfterTouchUp();
						return;
					}
				}
			}
		}

		super.touchUp(event, x, y, pointer, button);
	}

	private boolean checkCurrentVertexIntersection () {
		Array<Vector2> vertices = component.vertices;
		int index = vertices.indexOf(overVertex, true);
		if (index == -1) return true;

		Edge firstEdge;
		Edge secondEdge;

		if (index == 0)
			firstEdge = new Edge(vertices.peek(), overVertex);
		else
			firstEdge = new Edge(vertices.get(index - 1), overVertex);

		if (index == vertices.size - 1)
			secondEdge = new Edge(vertices.first(), overVertex);
		else
			secondEdge = new Edge(vertices.get(index + 1), overVertex);

		Array<Edge> polygonEdges = new Array<>();

		for (int i = 0; i < vertices.size - 1; i++) {
			polygonEdges.add(new Edge(vertices.get(i), vertices.get(i + 1)));
		}

		polygonEdges.add(new Edge(vertices.peek(), vertices.first()));

		for (Edge edge : polygonEdges) {
			if (edge.epsilonEquals(firstEdge, 0) || edge.epsilonEquals(secondEdge, 0))
				continue;

			if (polygonLineIntersection(firstEdge.start, firstEdge.end, edge.start, edge.end))
				return true;

			if (polygonLineIntersection(secondEdge.start, secondEdge.end, edge.start, edge.end))
				return true;
		}

		return false;
	}

	private void updateComponentFaces () {
		component.faces = Clipper.polygonize(DEFAULT_POLYGONIZER, component.vertices.toArray(Vector2.class));
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		super.mouseMoved(event, x, y);

		camera.unproject(tmpVector.set(x, y, 0));
		camera.project(tmpVector);

		x = tmpVector.x;
		y = tmpVector.y;

		overVertex = null;
		if (component != null) {
			for (Vector2 v : component.vertices) {
				if (isInsidePoint(v, x, y, POLYGON_RECT_SIZE)) {
					overVertex = v;
					return false;
				}
			}
		}

		return false;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		if (keycode == Keys.FORWARD_DEL && selectedVertex != null) {
			if (component.vertices.size < 3) {
				return false;
			}

			ChangePolygonAction action = new ChangePolygonAction(entityManipulator, proxy);

			component.vertices.removeValue(selectedVertex, true);
			selectedVertex = null;

			updateComponentFaces();
			action.takeSnapshot();
			undoModule.add(action);
			entityManipulator.selectedEntitiesValuesChanged();
			return true;
		}

		return super.keyDown(event, keycode);
	}

	private void initUI () {
		uiTable = new VisTable(true) {
			@Override
			public float getPrefHeight () {
				return 120;
			}
		};

		uiTable.setBackground(VisUI.getSkin().getDrawable("window-bg"));
		uiTable.setTouchable(Touchable.enabled);

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
		VisTextButton makeDefaultButton;

		buttonTable.setVisible(false);
		buttonTable.add(makeDefaultButton = new VisTextButton("Set From Bounds")).row();
		buttonTable.add(traceButton = new VisTextButton("Auto Trace")).row();

		dynamicUpdateCheck = new VisCheckBox("Dynamic faces update", true);

		uiTable.add(statusLabel).pad(0, 3, 0, 3).height(new VisValue(context -> statusLabel.isVisible() ? statusLabel.getPrefHeight() : 0)).spaceBottom(0).row();
		uiTable.add(buttonTable).height(new VisValue(context -> buttonTable.isVisible() ? buttonTable.getPrefHeight() : 0)).spaceBottom(0).row();
		uiTable.add().expand().fill().row();
		uiTable.add(dynamicUpdateCheck).expand(false, false).fill(false, false).center().padBottom(3);

		makeDefaultButton.addListener(new VisChangeListener((event, actor) -> makeDefaultPolygon()));

		traceButton.addListener(new VisChangeListener((event, actor) -> {
			EntityProxy entity = entityManipulator.getSelectedEntities().first();
			VisAssetDescriptor assetDescriptor = entity.getComponent(AssetReference.class).getAsset();
			if (assetDescriptor instanceof TextureAssetDescriptor == false) {
				Dialogs.showOKDialog(stage, "Message", "Auto Trace can only be used with sprite entities");
				return;
			}

			if (entity.getRotation() != 0) {
				Optional<DisableableOptionDialog> dialog = disableableDialogs.showOptionDialog(DisableableDialogsModule.POLYGON_TOOL_ROTATED_UNSUPPORTED, DefaultDialogOption.YES, stage, "Warning",
						"Auto tracer does not support rotated entities",
						OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
							@Override
							public void yes () {
								showAutoTracerDialog(entity, assetDescriptor);
							}
						});

				dialog.ifPresent(optDialog -> optDialog.setYesButtonText("Continue Anyway"));
			} else {
				showAutoTracerDialog(entity, assetDescriptor);
			}
		}));
	}

	private void showAutoTracerDialog (EntityProxy entity, VisAssetDescriptor assetDescriptor) {
		stage.addActor(new PolygonAutoTraceDialog(sceneMC, assetDescriptor, vertices -> {
			ChangePolygonAction action = new ChangePolygonAction(entityManipulator, proxy);

			//convert to world cords
			Rectangle bounds = entity.getBoundingRectangle();
			for (Vector2 vertex : vertices) {
				vertex.set(bounds.x + bounds.getWidth() * vertex.x, bounds.y + bounds.getHeight() * vertex.y);
			}

			component.vertices.clear();
			component.vertices.addAll(vertices);
			selectedVertex = null;

			updateComponentFaces();
			action.takeSnapshot();
			undoModule.add(action);
			entityManipulator.selectedEntitiesValuesChanged();
		}).fadeIn());
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
			overVertex = null;
			selectedVertex = null;
			return;
		}

		if (selection.size() > 1) {
			statusLabel.setText(SELECT_ONLY_ONE);
			overVertex = null;
			selectedVertex = null;
			return;
		}

		proxy = selection.first();

		if (proxy.hasComponent(VisPolygon.class) == false) {
			statusLabel.setText(NO_POLYGON_IN_SELECTION);
			overVertex = null;
			selectedVertex = null;
			return;
		}

		component = proxy.getComponent(VisPolygon.class);

		statusLabel.setVisible(false);
		buttonTable.setVisible(true);
		uiTable.invalidateHierarchy();
	}

	@Override
	public VisTable getToolPropertiesUI () {
		return uiTable;
	}

	@Override
	public String getToolId () {
		return TOOL_ID;
	}

	private static class Edge {
		public Vector2 start;
		public Vector2 end;

		public Edge (Vector2 start, Vector2 end) {
			this.start = start;
			this.end = end;
		}

		public boolean epsilonEquals (Edge edge, float epsilon) {
			return epsilonEquals(edge.start, edge.end, epsilon);
		}

		public boolean epsilonEquals (Vector2 start, Vector2 end, float epsilon) {
			if (this.start.epsilonEquals(start, epsilon) && this.end.epsilonEquals(end, epsilon))
				return true;

			if (this.start.epsilonEquals(end, epsilon) && this.end.epsilonEquals(start, epsilon))
				return true;

			return false;
		}
	}
}
