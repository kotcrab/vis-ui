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
import com.artemis.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.runtime.accessor.BasicPropertiesAccessor;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

/** @author Kotcrab */
public class GroupEntityProxy extends EntityProxy implements BasicPropertiesAccessor {
	private int groupId;
	private Array<Entity> entities;
	private Array<EntityProxy> proxies;
	private Accessor accessor;

	public GroupEntityProxy (Array<EntityProxy> proxies, int groupId) {
		super(null);
		this.groupId = groupId;
		this.proxies = new Array<>(proxies);
		accessor.calcBounds();
	}

	@Override
	protected void init () {
		basicAccessor = initAccessors();
	}

	public int getGroupId () {
		return groupId;
	}

	public Array<EntityProxy> getProxies () {
		return proxies;
	}

	@Override
	public Array<Entity> getEntities () {
		if (entities == null) {
			entities = new Array<>(proxies.size);
			proxies.forEach(proxy -> entities.addAll(proxy.getEntities()));
		}
		return entities;
	}

	@Override
	public boolean hasComponent (Class<? extends Component> clazz) {
		if (proxies.size == 0) return false;

		for (EntityProxy proxy : proxies) {
			if (proxy.hasComponent(clazz) == false)
				return false;
		}

		return true;
	}

	@Override
	public void addGroup (int groupId) {
		proxies.forEach(entity -> entity.addGroup(groupId));
	}

	@Override
	public void removeGroup (int groupId) {
		proxies.forEach(entity -> entity.removeGroup(groupId));
	}

	@Override
	public int getLastGroupId () {
		return proxies.get(0).getLastGroupId();
	}

	@Override
	public int getGroupIdBefore (int gid) {
		return proxies.get(0).getGroupIdBefore(gid);
	}

	@Override
	public boolean groupsContains (int gid) {
		return proxies.get(0).groupsContains(gid);
	}

	@Override
	public IntArray getGroupsIds () {
		return proxies.get(0).getGroupsIds();
	}

	@Override
	public void reload () {
		for (EntityProxy proxy : proxies) {
			proxy.reload();
		}
	}

	@Override
	protected BasicPropertiesAccessor initAccessors () {
		accessor = new Accessor();
		return accessor;
	}

	@Override
	public String getEntityName () {
		return "Group";
	}

	@Override
	public boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void updatePolygon (float x, float y) {
		for (EntityProxy proxy : proxies) {
			proxy.updatePolygon(x, y);
		}
	}

	@Override
	public int getLayerID () {
		return proxies.get(0).getLayerID();
	}

	private class Accessor implements BasicPropertiesAccessor {
		private Rectangle bounds = new Rectangle();

		@Override
		public float getWidth () {
			return bounds.getWidth();
		}

		@Override
		public float getHeight () {
			return bounds.getHeight();
		}

		@Override
		public Rectangle getBoundingRectangle () {
			calcBounds();
			return bounds;
		}

		@Override
		public void setPosition (float x, float y) {
			setX(x);
			setY(y);
		}

		@Override
		public float getX () {
			return bounds.x;
		}

		@Override
		public void setX (float x) {
			float delta = x - bounds.x;

			bounds.x = x;

			for (EntityProxy entity : proxies)
				translateX(entity, delta);
		}

		@Override
		public float getY () {
			return bounds.y;
		}

		@Override
		public void setY (float y) {
			float delta = y - bounds.y;

			bounds.y = y;

			for (EntityProxy entity : proxies)
				translateY(entity, delta);
		}

		private void calcBounds () {
			if (proxies.size > 0) {
				bounds.set(proxies.get(0).getBoundingRectangle());

				for (EntityProxy entity : proxies) {
					bounds.merge(entity.getBoundingRectangle());
				}
			} else
				bounds.set(0, 0, 0, 0);
		}

		private void translateX (EntityProxy object, float x) {
			object.setX(object.getX() + x);
		}

		private void translateY (EntityProxy object, float y) {
			object.setY(object.getY() + y);
		}
	}

	@Override
	public EntityScheme getScheme () {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setId (String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId () {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getZIndex () {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setZIndex (int zIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLayerId (int layerId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean compareProxyByID (EntityProxy other) {
		if (other instanceof GroupEntityProxy == false) return false;

		GroupEntityProxy groupProxy = (GroupEntityProxy) other;
		return groupId == groupProxy.groupId;
	}
}
