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

package com.kotcrab.vis.editor.module.scene.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.GroupSelectionFragment;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.SelectionFragment;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.SingleSelectionFragment;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.runtime.component.Layer;
import com.kotcrab.vis.runtime.component.VisGroup;
import com.kotcrab.vis.runtime.util.ImmutableArray;

/** @author Kotcrab */
public class EntitiesCollector extends EntityProcessingSystem {
	private EntityProxyCache proxyCache;

	private ComponentMapper<VisGroup> groupCm;
	private ComponentMapper<Layer> layerCm;

	private Array<EntityProxy> result;
	private int findGroupId;
	private int findLayerId;

	public EntitiesCollector () {
		super(Aspect.all(Layer.class));
	}

	@Override
	protected void initialize () {
		setEnabled(false);
	}

	public Array<EntityProxy> collect (int layerId, int groupId) {
		findGroupId = groupId;
		findLayerId = layerId;

		result = new Array<>();

		process();

		return result;
	}

	public Result createSelectionFragment (int layerId, int groupId) {
		findGroupId = groupId;
		findLayerId = layerId;

		result = new Array<>();

		process();

		if (result.size == 0) {
			throw new IllegalStateException("EntitiesCollector didn't find any matching entity " +
					"(was searching for group id: " + groupId + " on layer id: " + layerId + ")");
		}

		if (result.size == 1) {
			return new Result(result, new SingleSelectionFragment(result.first()));
		} else {
			return new Result(result, new GroupSelectionFragment(new ImmutableArray<>(result), groupId));
		}
	}

	@Override
	protected void process (Entity e) {
		Layer layer = layerCm.get(e);
		VisGroup group = groupCm.get(e);

		if (layer.layerId != findLayerId) return;
		if (findGroupId != -1) {
			if (group == null || group.groupIds.contains(findGroupId) == false) return;
		}

		result.add(proxyCache.get(e));
	}

	public static class Result {
		public final Array<EntityProxy> proxies;
		public final SelectionFragment fragment;

		public Result (Array<EntityProxy> proxies, SelectionFragment fragment) {
			this.proxies = proxies;
			this.fragment = fragment;
		}
	}
}
