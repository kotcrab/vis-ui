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

package com.kotcrab.vis.editor.module.physicseditor.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.module.physicseditor.list.ChangeListener;
import com.kotcrab.vis.editor.module.physicseditor.list.Changeable;
import com.kotcrab.vis.editor.module.physicseditor.models.ShapeModel.Type;
import com.kotcrab.vis.editor.module.physicseditor.util.Clipper;
import com.kotcrab.vis.editor.module.physicseditor.util.Clipper.Polygonizer;
import com.kotcrab.vis.editor.module.physicseditor.util.PolygonUtils;
import com.kotcrab.vis.runtime.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class RigidBodyModel extends Entity implements Changeable {
	public static final String PROP_NAME = "name";
	public static final String PROP_IMAGEPATH = "imagePath";
	public static final String PROP_PHYSICS = "physics";

	private final Vector2 origin = new Vector2(0, 0);
	private final Array<ShapeModel> shapes = new Array<>();
	private final Array<ShapeModel> shapesToRemove = new Array<>();
	private final Array<PolygonModel> polygons = new Array<>();
	private final Array<CircleModel> circles = new Array<>();
	private String name = "unamed";
	public transient TextureRegion region;

	public RigidBodyModel () {
		super(null);
	}

	public Vector2 getOrigin () {
		return origin;
	}

	public Array<ShapeModel> getShapes () {
		return shapes;
	}

	public Array<PolygonModel> getPolygons () {
		return polygons;
	}

	public Array<CircleModel> getCircles () {
		return circles;
	}

	public void setName (String name) {
		assert name != null;
		this.name = name;
		firePropertyChanged(PROP_NAME);
	}

	public String getName () {
		return name;
	}

	public void clear () {
		shapes.clear();
		polygons.clear();
		circles.clear();
		firePropertyChanged(PROP_PHYSICS);
	}

	public void clearPhysics () {
		polygons.clear();
		circles.clear();
		firePropertyChanged(PROP_PHYSICS);
	}

	public void computePhysics (Polygonizer polygonizer) {
		polygons.clear();
		circles.clear();

		for (ShapeModel shape : shapes) {
			if (!shape.isClosed()) continue;

			if (shape.getType() == Type.POLYGON) {
				Vector2[] vertices = shape.getVertices().toArray(Vector2.class);
				Vector2[][] polys = Clipper.polygonize(polygonizer, vertices);
				if (polys != null) for (Vector2[] poly : polys) {

					if (PolygonUtils.isDegenerate(poly)) {
						App.eventBus.post(new StatusBarEvent("Shape polygon is degenerated, removing shape", Color.RED));
						shapesToRemove.add(shape);
						continue;
					}

					polygons.add(new PolygonModel(poly));
				}

			}
			if (shape.getType() == Type.CIRCLE) {
				Vector2 center = shape.getVertices().get(0);
				float radius = Math.abs(shape.getVertices().get(1).cpy().sub(center).len());
				circles.add(new CircleModel(center, radius));
			}
		}

		shapes.removeAll(shapesToRemove, true);
		shapesToRemove.clear();

		firePropertyChanged(PROP_PHYSICS);
	}

	private final List<ChangeListener> changeListeners = new ArrayList<ChangeListener>(3);

	@Override
	public void addChangeListener (ChangeListener l) {
		changeListeners.add(l);
	}

	@Override
	public void removeChangeListener (ChangeListener l) {
		changeListeners.remove(l);
	}

	protected void firePropertyChanged (String propertyName) {
		for (ChangeListener listener : changeListeners)
			listener.propertyChanged(this, propertyName);
	}

	public void setRegion (TextureRegion region, String path) {
		this.region = region;
		setAssetPath(path);
	}
}
