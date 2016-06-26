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

import com.artemis.*;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.LayersDialog;
import com.kotcrab.vis.runtime.component.Invisible;
import com.kotcrab.vis.runtime.component.Layer;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

/**
 * Provides utils for manipulating layers. This is internal VisEditor API.
 * @author Kotcrab
 */
public class LayerManipulator extends Manager {
	private ComponentMapper<Layer> layerCm;
	private AspectSubscriptionManager subscriptionManager;

	private RenderBatchingSystem renderBatchingSystem;

	private EntitySubscription subscription;

	private EntityTransmuter invisibleTransmuter;
	private EntityTransmuter invisibleRemoverTransmuter;

	@Override
	protected void initialize () {
		subscription = subscriptionManager.get(Aspect.all(Layer.class));

		invisibleTransmuter = new EntityTransmuterFactory(world)
				.add(Invisible.class)
				.build();

		invisibleRemoverTransmuter = new EntityTransmuterFactory(world)
				.remove(Invisible.class)
				.build();
	}

	public ImmutableBag<Entity> getEntitiesWithLayer (int layerId) {
		Bag<Entity> entities = new Bag<Entity>();

		IntBag bag = subscription.getEntities();
		int[] data = bag.getData();

		for (int i = 0; i < bag.size(); i++) {
			int id = data[i];
			Entity entity = world.getEntity(id);
			Layer layer = layerCm.get(entity);

			if (layer.layerId == layerId)
				entities.add(entity);
		}

		return entities;
	}

	public void changeLayerVisibility (int layerId, boolean visible) {
		IntBag bag = subscription.getEntities();
		int[] data = bag.getData();
		EntityTransmuter transmuter = visible == true ? invisibleRemoverTransmuter : invisibleTransmuter;

		for (int i = 0; i < bag.size(); i++) {
			int id = data[i];
			Entity entity = world.getEntity(id);
			if (layerCm.get(entity).layerId == layerId)
				transmuter.transmute(entity);
		}
	}

	/**
	 * Swaps layers id in entities. This SHOULD NOT be used directly because it does not update internal layer structure
	 * in {@link EditorScene}. Layers order is managed by {@link LayersDialog}
	 */
	public void swapLayers (int id1, int id2) {
		IntBag bag = subscription.getEntities();
		int[] data = bag.getData();

		for (int i = 0; i < bag.size(); i++) {
			int id = data[i];
			Entity entity = world.getEntity(id);
			Layer layer = layerCm.get(entity);

			if (layer.layerId == id1) layer.layerId = id2;
			else if (layer.layerId == id2) layer.layerId = id1;
		}

		renderBatchingSystem.markDirty();
	}
}
