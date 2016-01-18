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

package com.kotcrab.vis.editor.module.scene.entitymanipulator;

import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.runtime.util.ImmutableArray;

/** @author Kotcrab */
public class GroupSelectionFragment implements SelectionFragment {
	private final Rectangle bounds = new Rectangle();

	private final ImmutableArray<EntityProxy> proxies;
	private final int groupId;

	public GroupSelectionFragment (ImmutableArray<EntityProxy> proxies, int groupId) {
		this.proxies = proxies;
		this.groupId = groupId;
	}

	public ImmutableArray<EntityProxy> getProxies () {
		return proxies;
	}

	public int getGroupId () {
		return groupId;
	}

	@Override
	public Rectangle getBoundingRectangle () {
		calcBounds();
		return bounds;
	}

	private void calcBounds () {
		if (proxies.size() > 0) {
			bounds.set(proxies.get(0).getBoundingRectangle());

			for (EntityProxy entity : proxies) {
				bounds.merge(entity.getBoundingRectangle());
			}
		} else
			bounds.set(0, 0, 0, 0);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GroupSelectionFragment that = (GroupSelectionFragment) o;

		if (groupId != that.groupId) return false;
		return proxies.equals(that.proxies);

	}

	@Override
	public int hashCode () {
		int result = proxies.hashCode();
		result = 31 * result + groupId;
		return result;
	}
}
