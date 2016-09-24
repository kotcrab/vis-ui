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
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.IntArray;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.properties.StoresAssetDescriptor;

/**
 * This system should be passive.
 * @author Kotcrab
 */
public class AssetsUsageAnalyzer extends EntityProcessingSystem {
	private IntArray ids;
	private VisAssetDescriptor searchFor;

	private Bag<Component> fillBag = new Bag<>();

	public AssetsUsageAnalyzer () {
		super(Aspect.all());
	}

	@Override
	protected void process (Entity e) {
		fillBag.clear();
		e.getComponents(fillBag);

		for (Component component : fillBag) {
			if (component instanceof StoresAssetDescriptor) {
				StoresAssetDescriptor storage = (StoresAssetDescriptor) component;
				VisAssetDescriptor asset = storage.getAsset();

				if (asset != null && asset.compare(searchFor)) {
					ids.add(e.getId());
					return;
				}
			}
		}
	}

	public void collectUsages (IntArray ids, VisAssetDescriptor searchFor) {
		this.ids = ids;
		this.searchFor = searchFor;
		process();
	}
}
