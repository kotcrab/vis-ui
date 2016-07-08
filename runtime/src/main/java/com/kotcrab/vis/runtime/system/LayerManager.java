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

package com.kotcrab.vis.runtime.system;

import com.artemis.Manager;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.data.LayerData;

/**
 * Allows to get layer data exported from VisEditor scene.
 * @author Kotcrab
 */
public class LayerManager extends Manager {
	private LayerData layers[];

	public LayerManager (Array<LayerData> layerData) {
		int maxId = 0;

		for (LayerData data : layerData) {
			if (data.id > maxId)
				maxId = data.id;
		}

		layers = new LayerData[maxId + 1];

		for (LayerData data : layerData) {
			layers[data.id] = data;
		}
	}

	public LayerData getData (int layerId) {
		return layers[layerId];
	}
}
