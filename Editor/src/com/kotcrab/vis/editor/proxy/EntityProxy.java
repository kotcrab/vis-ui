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

package com.kotcrab.vis.editor.proxy;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.entity.UUIDComponent;
import com.kotcrab.vis.editor.module.scene.VisUUIDManager;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.PolygonTool;
import com.kotcrab.vis.editor.util.polygon.Clipper;
import com.kotcrab.vis.runtime.accessor.*;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

import java.util.UUID;

/** @author Kotcrab */
public abstract class EntityProxy {
	private ComponentMapper<PolygonComponent> polygonCm;

	protected Entity entity;
	protected VisUUIDManager uuidManager;
	protected UUID uuid;

	protected BasicPropertiesAccessor basicAccessor;

	protected SizePropertiesAccessor sizeAccessor;
	protected OriginPropertiesAccessor originAccessor;
	protected ScalePropertiesAccessor scaleAccessor;
	protected ColorPropertiesAccessor colorAccessor;
	protected RotationPropertiesAccessor rotationAccessor;
	protected FlipPropertiesAccessor flipAccessor;

	public EntityProxy (Entity entity) {
		this.entity = entity;
		init();

		if (entity != null) {
			uuidManager = entity.getWorld().getManager(VisUUIDManager.class);
			uuid = entity.getComponent(UUIDComponent.class).getUUID();

			polygonCm = entity.getWorld().getMapper(PolygonComponent.class);
		}
	}

	protected void init () {
		if (entity.getComponent(RenderableComponent.class) == null || entity.getComponent(LayerComponent.class) == null)
			throw new IllegalArgumentException("Proxy cannot be used for non renderable entities. Entity must contain RenderableComponent and LayerComponent");

		basicAccessor = initAccessors();
	}

	protected abstract BasicPropertiesAccessor initAccessors ();

	/** Reloads this proxy, must be called if there is chance that this entity was removed and then radded by UndoableAction. */
	public void reload () {
		entity = uuidManager.get(uuid);
		basicAccessor = initAccessors();
	}

	public EntityScheme getScheme () {
		return new EntityScheme(entity);
	}

	public void addGroup (int groupId) {
		addGroup(groupId, -1);
	}

	public void addGroup (int groupId, int parentGroupId) {
		IntArray groupIds = getGroupComponent().groupIds;
		if (groupIds.contains(groupId) == false) {
			if (parentGroupId != -1)
				groupIds.insert(groupIds.indexOf(parentGroupId), groupId);
			else
				groupIds.add(groupId);
		}
	}

	public void removeGroup (int groupId) {
		IntArray groupIds = getGroupComponent().groupIds;
		if (groupIds.contains(groupId)) groupIds.removeValue(groupId);
	}

	public int getLastGroupId () {
		GroupComponent gdc = entity.getComponent(GroupComponent.class);

		if (gdc == null || gdc.groupIds.size == 0) {
			return -1;
		}

		return gdc.groupIds.peek();
	}

	public int getGroupIdBefore (int gid) {
		IntArray groupIds = getGroupComponent().groupIds;
		int index = groupIds.indexOf(gid) - 1;

		if (index < 0)
			return -1;
		else
			return groupIds.get(index);
	}

	public boolean groupsContains (int gid) {
		return getGroupComponent().groupIds.contains(gid);
	}

	public IntArray getGroupsIds () {
		return new IntArray(getGroupComponent().groupIds);
	}

	private GroupComponent getGroupComponent () {
		GroupComponent gdc = entity.getComponent(GroupComponent.class);

		if (gdc == null) {
			gdc = new GroupComponent();
			entity.edit().add(gdc);
		}

		return gdc;
	}

	public String getId () {
		IDComponent idc = entity.getComponent(IDComponent.class);
		if (idc != null)
			return idc.id;
		else
			return null;
	}

	public void setId (String id) {
		IDComponent idc = entity.getComponent(IDComponent.class);

		if (idc != null)
			idc.id = id;
		else
			entity.edit().add(new IDComponent(id));
	}

	public boolean hasComponent (Class<? extends Component> clazz) {
		return entity.getComponent(clazz) != null;
	}

	public int getZIndex () {
		return entity.getComponent(RenderableComponent.class).zIndex;
	}

	public void setZIndex (int zIndex) {
		entity.getComponent(RenderableComponent.class).zIndex = zIndex;
	}

	public int getLayerID () {
		return entity.getComponent(LayerComponent.class).layerId;
	}

	public void setLayerId (int layerId) {
		entity.getComponent(LayerComponent.class).layerId = layerId;
	}

	//basic properties

	public float getX () {
		return basicAccessor.getX();
	}

	public void setX (float x) {
		updatePolygon(x, getY());
		basicAccessor.setX(x);
	}

	public float getY () {
		return basicAccessor.getY();
	}

	public void setY (float y) {
		updatePolygon(getX(), y);
		basicAccessor.setY(y);
	}

	public void setPosition (float x, float y) {
		updatePolygon(x, y);
		basicAccessor.setPosition(x, y);
	}

	protected void updatePolygon (float x, float y) {
		PolygonComponent polygon = polygonCm.getSafe(entity);
		float dx = getX() - x;
		float dy = getY() - y;
		if (polygon != null) {
			for (Vector2 vertex : polygon.vertices) {
				vertex.sub(dx, dy);
			}

			polygon.faces = Clipper.polygonize(PolygonTool.DEFAULT_POLYGONIZER, polygon.vertices.toArray(Vector2.class));
		}
	}

	public float getWidth () {
		return basicAccessor.getWidth();
	}

	public float getHeight () {
		return basicAccessor.getHeight();
	}

	public Rectangle getBoundingRectangle () {
		return basicAccessor.getBoundingRectangle();
	}

	//resize properties

	protected void enableResize (SizePropertiesAccessor sizeAccessor) {
		this.sizeAccessor = sizeAccessor;
	}

	public boolean isResizeSupported () {
		return sizeAccessor != null;
	}

	public void setSize (float width, float height) {
		sizeAccessor.setSize(width, height);
	}

	//origin properties

	protected void enableOrigin (OriginPropertiesAccessor originAccessor) {
		this.originAccessor = originAccessor;
	}

	public boolean isOriginSupported () {
		return originAccessor != null;
	}

	public float getOriginX () {
		return originAccessor.getOriginX();
	}

	public float getOriginY () {
		return originAccessor.getOriginY();
	}

	public void setOrigin (float x, float y) {
		originAccessor.setOrigin(x, y);
	}

	//scale properties

	protected void enableScale (ScalePropertiesAccessor scaleAccessor) {
		this.scaleAccessor = scaleAccessor;
	}

	public boolean isScaleSupported () {
		return scaleAccessor != null;
	}

	public float getScaleX () {
		return scaleAccessor.getScaleX();
	}

	public float getScaleY () {
		return scaleAccessor.getScaleY();
	}

	public void setScale (float x, float y) {
		scaleAccessor.setScale(x, y);
	}

	//color properties

	protected void enableColor (ColorPropertiesAccessor colorAccessor) {
		this.colorAccessor = colorAccessor;
	}

	public boolean isColorSupported () {
		return colorAccessor != null;
	}

	public Color getColor () {
		return colorAccessor.getColor();
	}

	public void setColor (Color color) {
		colorAccessor.setColor(color);
	}

	//rotation properties

	protected void enableRotation (RotationPropertiesAccessor rotationAccessor) {
		this.rotationAccessor = rotationAccessor;
	}

	public boolean isRotationSupported () {
		return rotationAccessor != null;
	}

	public float getRotation () {
		return rotationAccessor.getRotation();
	}

	public void setRotation (float rotation) {
		rotationAccessor.setRotation(rotation);
	}

	//flip properties

	protected void enableFlip (FlipPropertiesAccessor flipAccessor) {
		this.flipAccessor = flipAccessor;
	}

	public boolean isFlipSupported () {
		return flipAccessor != null;
	}

	public boolean isFlipX () {
		return flipAccessor.isFlipX();
	}

	public boolean isFlipY () {
		return flipAccessor.isFlipY();
	}

	public void setFlip (boolean x, boolean y) {
		flipAccessor.setFlip(x, y);
	}

	//others

	public abstract String getEntityName ();

	VisAssetDescriptor getAssetDescriptor () {
		return entity.getComponent(AssetComponent.class).asset;
	}

	public void setAssetDescriptor (VisAssetDescriptor asset) {
		checkAssetDescriptor(asset);

		AssetComponent adc = entity.getComponent(AssetComponent.class);

		if (adc != null)
			adc.asset = asset;
		else
			entity.edit().add(new AssetComponent(asset));
	}

	public void checkAssetDescriptor (VisAssetDescriptor assetDescriptor) {
		if (isAssetsDescriptorSupported(assetDescriptor) == false)
			throw new UnsupportedAssetDescriptorException(assetDescriptor);
	}

	protected abstract boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor);

	public Array<Entity> getEntities () {
		Array<Entity> entities = new Array<>(1);
		entities.add(entity);
		return entities;
	}

	public Entity getEntity () {
		return entity;
	}

	public boolean compareProxyByID (EntityProxy other) {
		return uuid.equals(other.uuid);
	}
}
