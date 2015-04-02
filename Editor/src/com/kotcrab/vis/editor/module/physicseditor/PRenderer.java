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

package com.kotcrab.vis.editor.module.physicseditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.editor.module.physicseditor.models.PolygonModel;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;
import com.kotcrab.vis.editor.module.physicseditor.models.ShapeModel;

import java.util.List;

public class PRenderer extends PhysicsEditorModule {

	private static final Color SHAPE_COLOR = new Color(0.0f, 0.0f, 0.8f, 1);
	private static final Color SHAPE_LASTLINE_COLOR = new Color(0.5f, 0.5f, 0.5f, 1);
	private static final Color POLYGON_COLOR = new Color(0.0f, 0.7f, 0.0f, 1);
	private static final Color ORIGIN_COLOR = new Color(0.7f, 0.0f, 0.0f, 1);
	private static final Color MOUSESELECTION_FILL_COLOR = new Color(0.2f, 0.2f, 0.8f, 0.2f);
	private static final Color MOUSESELECTION_STROKE_COLOR = new Color(0.2f, 0.2f, 0.8f, 0.6f);
	private static final Color GRID_COLOR = new Color(0.5f, 0.5f, 0.5f, 1);
	private static final Color AXIS_COLOR = new Color(0.5f, 0.5f, 0.5f, 1);

	private static final Color BALLTHROWPATH_COLOR = new Color(0.2f, 0.2f, 0.2f, 1);

	private final ShapeRenderer drawer = new ShapeRenderer();
	private PCameraModule camera;
	private PSettings settings;

	@Override
	public void init () {
		camera = physicsContainer.get(PCameraModule.class);
		settings = physicsContainer.get(PSettings.class);
	}

	public void drawBallThrowPath (Vector2 p1, Vector2 p2) {
		if (p1 == null || p2 == null) return;

		Gdx.gl.glLineWidth(3);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		float w = 0.03f * camera.getZoom();

		drawer.setProjectionMatrix(camera.getCombinedMatrix());
		drawer.begin(ShapeRenderer.ShapeType.Line);
		drawer.setColor(BALLTHROWPATH_COLOR);
		drawer.line(p1.x, p1.y, p2.x, p2.y);
		drawer.end();

		drawer.setProjectionMatrix(camera.getCombinedMatrix());
		drawer.begin(ShapeType.Filled);
		drawer.setColor(BALLTHROWPATH_COLOR);
		drawer.rect(p2.cpy().sub(w / 2, w / 2).x, p2.cpy().sub(w / 2, w / 2).y, w, w);
		drawer.end();
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void drawModel (RigidBodyModel model, List<Vector2> selectedPoints, Vector2 nextPoint, Vector2 nearestPoint) {
		if (model == null) return;

		drawer.setProjectionMatrix(camera.getCombinedMatrix());
		drawer.setTransformMatrix(new Matrix4());

		if (settings.isPolygonDrawn) {
			drawPolygons(model.getPolygons());
		}

		if (settings.isShapeDrawn) {
			drawShapes(model.getShapes(), nextPoint);
			drawPoints(model.getShapes(), selectedPoints, nearestPoint, nextPoint);
			drawOrigin(model.getOrigin(), nearestPoint);
		}
	}

	public void drawBoundingBox (Sprite sp) {
		if (sp == null) return;
		drawer.setProjectionMatrix(camera.getCombinedMatrix());
		drawer.setTransformMatrix(new Matrix4());
		drawBoundingBox(sp.getWidth(), sp.getHeight());
	}

	public void drawGrid () {
		drawer.setProjectionMatrix(camera.getCombinedMatrix());
		drawer.setTransformMatrix(new Matrix4());
		if (settings.isGridShown) drawGrid(settings.gridGap);
	}

	public void drawMouseSelection (Vector2 p1, Vector2 p2) {
		if (p1 == null || p2 == null) return;
		drawer.setProjectionMatrix(camera.getCombinedMatrix());
		drawer.setTransformMatrix(new Matrix4());
		drawMouseSelection(p1.x, p1.y, p2.x, p2.y);
	}

	// -------------------------------------------------------------------------
	// Internals
	// -------------------------------------------------------------------------

	private void drawBoundingBox (float w, float h) {
		Gdx.gl.glLineWidth(1);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		drawer.begin(ShapeType.Line);
		drawer.setColor(AXIS_COLOR);
		drawer.rect(0, 0, w, h);
		drawer.end();
	}

	private void drawGrid (float gap) {
		Gdx.gl.glLineWidth(1);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		if (gap <= 0) gap = 0.001f;
		float x = camera.getX();
		float y = camera.getY();
		float w = camera.getCamera().viewportWidth;
		float h = camera.getCamera().viewportHeight;
		float z = camera.getZoom();

		drawer.begin(ShapeRenderer.ShapeType.Line);
		drawer.setColor(GRID_COLOR);
		for (float d = 0; d < x + w / 2 * z; d += gap) drawer.line(d, y - h / 2 * z, d, y + h / 2 * z);
		for (float d = -gap; d > x - w / 2 * z; d -= gap) drawer.line(d, y - h / 2 * z, d, y + h / 2 * z);
		for (float d = 0; d < y + h / 2 * z; d += gap) drawer.line(x - w / 2 * z, d, x + w / 2 * z, d);
		for (float d = -gap; d > y - h / 2 * z; d -= gap) drawer.line(x - w / 2 * z, d, x + w / 2 * z, d);
		drawer.end();
	}

	private void drawShapes (List<ShapeModel> shapes, Vector2 nextPoint) {
		Gdx.gl.glLineWidth(2);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		for (ShapeModel shape : shapes) {
			List<Vector2> vs = shape.getVertices();
			if (vs.isEmpty()) continue;

			switch (shape.getType()) {
				case POLYGON:
					drawer.begin(ShapeRenderer.ShapeType.Line);
					drawer.setColor(SHAPE_COLOR);

					for (int i = 1; i < vs.size(); i++)
						drawer.line(vs.get(i).x, vs.get(i).y, vs.get(i - 1).x, vs.get(i - 1).y);

					if (shape.isClosed()) {
						drawer.setColor(SHAPE_COLOR);
						drawer.line(vs.get(0).x, vs.get(0).y, vs.get(vs.size() - 1).x, vs.get(vs.size() - 1).y);
					} else {
						drawer.setColor(SHAPE_LASTLINE_COLOR);
						drawer.line(vs.get(vs.size() - 1).x, vs.get(vs.size() - 1).y, nextPoint.x, nextPoint.y);
					}

					drawer.end();
					break;

				case CIRCLE:
					if (shape.isClosed()) {
						Vector2 center = shape.getVertices().get(0);
						float radius = shape.getVertices().get(1).cpy().sub(center).len();
						if (radius > 0.0001f) {
							drawer.begin(ShapeType.Line);
							drawer.setColor(SHAPE_COLOR);
							drawer.circle(center.x, center.y, radius, 20);
							drawer.end();
						}
					} else {
						Vector2 center = shape.getVertices().get(0);
						float radius = nextPoint.cpy().sub(center).len();
						if (radius > 0.0001f) {
							drawer.begin(ShapeType.Line);
							drawer.setColor(SHAPE_LASTLINE_COLOR);
							drawer.circle(center.x, center.y, radius, 20);
							drawer.end();
						}
					}
					break;
			}
		}
	}

	private void drawPoints (List<ShapeModel> shapes, List<Vector2> selectedPoints, Vector2 nearestPoint, Vector2 nextPoint) {
		Gdx.gl.glLineWidth(2);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		float w = 0.025f * camera.getZoom();

		for (ShapeModel shape : shapes) {
			for (Vector2 p : shape.getVertices()) {
				if (p == nearestPoint || (selectedPoints != null && selectedPoints.contains(p))) {
					drawer.begin(ShapeType.Filled);
					drawer.setColor(SHAPE_COLOR);
					drawer.rect(p.cpy().sub(w / 2, w / 2).x, p.cpy().sub(w / 2, w / 2).y, w, w);
					drawer.end();
				} else {
					drawer.begin(ShapeType.Line);
					drawer.setColor(SHAPE_COLOR);
					drawer.rect(p.cpy().sub(w / 2, w / 2).x, p.cpy().sub(w / 2, w / 2).y, w, w);
					drawer.end();
				}
			}
		}

		if (nextPoint != null) {
			drawer.begin(ShapeType.Line);
			drawer.setColor(SHAPE_LASTLINE_COLOR);
			drawer.rect(nextPoint.cpy().sub(w / 2, w / 2).x, nextPoint.cpy().sub(w / 2, w / 2).y, w, w);
			drawer.end();
		}
	}

	private void drawPolygons (List<PolygonModel> polygons) {
		Gdx.gl.glLineWidth(2);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		drawer.begin(ShapeRenderer.ShapeType.Line);
		drawer.setColor(POLYGON_COLOR);

		for (PolygonModel polygon : polygons) {
			List<Vector2> vs = polygon.vertices;
			for (int i = 1, n = vs.size(); i < n; i++)
				drawer.line(vs.get(i).x, vs.get(i).y, vs.get(i - 1).x, vs.get(i - 1).y);
			if (vs.size() > 1) drawer.line(vs.get(0).x, vs.get(0).y, vs.get(vs.size() - 1).x, vs.get(vs.size() - 1).y);
		}

		drawer.end();
	}

	private void drawOrigin (Vector2 o, Vector2 nearestPoint) {
		Gdx.gl.glLineWidth(2);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		float len = 0.03f * camera.getZoom();
		float radius = 0.02f * camera.getZoom();

		drawer.begin(ShapeRenderer.ShapeType.Line);
		drawer.setColor(ORIGIN_COLOR);
		drawer.line(o.x - len, o.y, o.x + len, o.y);
		drawer.line(o.x, o.y - len, o.x, o.y + len);
		drawer.end();

		if (nearestPoint != o) {
			drawer.begin(ShapeType.Line);
			drawer.setColor(ORIGIN_COLOR);
			drawer.circle(o.x, o.y, radius, 20);
			drawer.end();
		} else {
			drawer.begin(ShapeType.Filled);
			drawer.setColor(ORIGIN_COLOR);
			drawer.circle(o.x, o.y, radius, 20);
			drawer.end();
		}
	}

	private void drawMouseSelection (float x1, float y1, float x2, float y2) {
		Gdx.gl.glLineWidth(3);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		Rectangle rect = new Rectangle(
				Math.min(x1, x2), Math.min(y1, y2),
				Math.abs(x2 - x1), Math.abs(y2 - y1)
		);

		drawer.begin(ShapeType.Filled);
		drawer.setColor(MOUSESELECTION_FILL_COLOR);
		drawer.rect(rect.x, rect.y, rect.width, rect.height);
		drawer.end();

		drawer.begin(ShapeType.Line);
		drawer.setColor(MOUSESELECTION_STROKE_COLOR);
		drawer.rect(rect.x, rect.y, rect.width, rect.height);
		drawer.end();
	}
}
