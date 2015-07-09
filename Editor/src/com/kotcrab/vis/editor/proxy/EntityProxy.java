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

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.entity.accessor.*;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

/** @author Kotcrab */
public abstract class EntityProxy {
	protected Entity entity;

	protected EntityScheme scheme;

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
	}

	protected void init () {
		if (entity.getComponent(RenderableComponent.class) == null || entity.getComponent(LayerComponent.class) == null)
			throw new IllegalArgumentException("Proxy cannot be used for non renderable entities. Entity must contain RenderableComponent and LayerComponent");

		basicAccessor = initAccessors();
	}

	protected abstract BasicPropertiesAccessor initAccessors ();

	/** Reloads this proxy, must be called if there is chance that this entity was removed and then radded by UndoableAction. */
	public void reload () {
		entity = entity.getWorld().getEntity(entity.getId());
		basicAccessor = initAccessors();
	}

	public EntityScheme getScheme () {
		if (scheme == null)
			scheme = new EntityScheme(entity);

		return scheme;
	}

	public void addGroup (int groupId) {
		IntArray groupIds = getGroupComponent().groupIds;
		if (groupIds.contains(groupId) == false) groupIds.add(groupId);
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
		basicAccessor.setX(x);
	}

	public float getY () {
		return basicAccessor.getY();
	}

	public void setY (float y) {
		basicAccessor.setY(y);
	}

	public void setPosition (float x, float y) {
		basicAccessor.setPosition(x, y);
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

	protected void enableTint (ColorPropertiesAccessor colorAccessor) {
		this.colorAccessor = colorAccessor;
	}

	public boolean isTintSupported () {
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

	boolean isRotationSupported () {
		return rotationAccessor != null;
	}

	float getRotation () {
		return rotationAccessor.getRotation();
	}

	void setRotation (float rotation) {
		rotationAccessor.setRotation(rotation);
	}

	//flip properties

	protected void enableFlip (FlipPropertiesAccessor flipAccessor) {
		this.flipAccessor = flipAccessor;
	}

	boolean isFlipSupported () {
		return flipAccessor != null;
	}

	boolean isFlipX () {
		return flipAccessor.isFlipX();
	}

	boolean isFlipY () {
		return flipAccessor.isFlipY();
	}

	void setFlip (boolean x, boolean y) {
		flipAccessor.setFlip(x, y);
	}

	//others

	public String toPrettyString () {
		if (getId() == null)
			return getEntityName() + " X: " + (int) getX() + " Y: " + (int) getY();
		else
			return getEntityName() + " ID: " + getId() + " X: " + (int) getX() + " Y: " + (int) getY();
	}

	protected abstract String getEntityName ();

	VisAssetDescriptor getAssetDescriptor () {
		return entity.getComponent(AssetComponent.class).asset;
	}

	void setAssetDescriptor (VisAssetDescriptor asset) {
		checkAssetDescriptor(asset);

		AssetComponent adc = entity.getComponent(AssetComponent.class);

		if (adc != null)
			adc.asset = asset;
		else
			entity.edit().add(new AssetComponent(asset));
	}

	void checkAssetDescriptor (VisAssetDescriptor assetDescriptor) {
		if (isAssetsDescriptorSupported(assetDescriptor) == false)
			throw new UnsupportedAssetDescriptorException(assetDescriptor);
	}

	abstract boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor);

	public Array<Entity> getEntities () {
		Array<Entity> entities = new Array<>(1);
		entities.add(entity);
		return entities;
	}
}
