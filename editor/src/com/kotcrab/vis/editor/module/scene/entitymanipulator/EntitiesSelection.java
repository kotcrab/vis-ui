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

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.scene.system.EntitiesCollector;
import com.kotcrab.vis.editor.module.scene.system.EntitiesCollector.Result;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.runtime.util.ImmutableArray;

import java.util.function.Consumer;

/** @author Kotcrab */
public class EntitiesSelection {
	private final EntitiesCollector collector;
	private final int groupId;
	private final int layerId;

	private final Array<EntityProxy> selectedProxies;
	private final Array<SelectionFragment> selectionFragments;
	private final ImmutableArray<SelectionFragment> immutableSelectionFragments;
	private final ImmutableArray<EntityProxy> immutableSelectedProxies;

	public EntitiesSelection (EntitiesCollector collector, int layerId) {
		this(collector, layerId, -1);
	}

	public EntitiesSelection (EntitiesCollector collector, int layerId, int groupId) {
		this.collector = collector;
		this.groupId = groupId;
		this.layerId = layerId;

		this.selectedProxies = new Array<>();
		this.selectionFragments = new Array<>();
		this.immutableSelectionFragments = new ImmutableArray<>(selectionFragments);
		this.immutableSelectedProxies = new ImmutableArray<>(selectedProxies);
	}

	public ImmutableArray<EntityProxy> getSelection () {
		return immutableSelectedProxies;
	}

	public ImmutableArray<SelectionFragment> getFragmentedSelection () {
		return immutableSelectionFragments;
	}

	public int getGroupId () {
		return groupId;
	}

	public int getLayerId () {
		return layerId;
	}

	public int size () {
		return selectedProxies.size;
	}

	public EntityProxy peek () {
		return selectedProxies.peek();
	}

	public void forEach (Consumer<EntityProxy> consumer) {
		selectedProxies.forEach(consumer);
	}

	public void forEachEntity (Consumer<Entity> consumer) {
		selectedProxies.forEach(proxy -> consumer.accept(proxy.getEntity()));
	}

	public boolean isSelected (EntityProxy proxy) {
		return selectedProxies.contains(proxy, true);
	}

	void clearSelection () {
		selectedProxies.clear();
		selectionFragments.clear();
	}

	void append (EntityProxy proxy) {
		int lastProxyGid = (groupId == -1) ? proxy.getLastGroupId() : proxy.getGroupIdBefore(groupId);
		if (lastProxyGid != -1) {
			if (isGroupIdAlreadySelected(lastProxyGid) == false) {
				Result result = collector.createSelectionFragment(layerId, lastProxyGid);
				selectedProxies.addAll(result.proxies.toArray());
				selectionFragments.add(result.fragment);
			}
		} else {
			SelectionFragment fragment = new SingleSelectionFragment(proxy);
			selectedProxies.add(proxy);
			selectionFragments.add(fragment);
		}
	}

	void deselect (EntityProxy proxy) {
		int lastProxyGid = (groupId == -1) ? proxy.getLastGroupId() : proxy.getGroupIdBefore(groupId);

		if (lastProxyGid != -1) {
			Result result = collector.createSelectionFragment(layerId, lastProxyGid);

			selectedProxies.removeAll(result.proxies, true);
			selectionFragments.removeValue(result.fragment, false); //must use equals compare
		} else {
			SelectionFragment fragment = new SingleSelectionFragment(proxy);
			selectedProxies.removeValue(proxy, true);
			selectionFragments.removeValue(fragment, false); //use equals
		}
	}

	public boolean isGroupIdAlreadySelected (int gid) {
		for (SelectionFragment fragment : selectionFragments) {
			if (fragment instanceof GroupSelectionFragment) {
				GroupSelectionFragment groupFragment = (GroupSelectionFragment) fragment;
				if (groupFragment.getGroupId() == gid) return true;
			}
		}

		return false;
	}

	boolean isEnterIntoGroupValid () {
		if (selectionFragments.size != 1) return false;
		return selectionFragments.first() instanceof GroupSelectionFragment;
	}
}
