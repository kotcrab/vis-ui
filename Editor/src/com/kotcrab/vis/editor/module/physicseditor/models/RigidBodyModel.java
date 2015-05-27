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
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
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
		setAssetDescriptor(new PathAsset(path));
	}

	@Override
	protected boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor) {
		return assetDescriptor instanceof PathAsset;
	}
}
