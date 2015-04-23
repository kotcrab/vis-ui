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

package com.kotcrab.vis.runtime.plugin;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.entity.Entity;

public interface EntitySupport<ED extends EntityData, E extends Entity> {
	void setLoaders (AssetManager manager);

	Class<E> getEntityClass ();

	void resolveDependencies (Array<AssetDescriptor> deps, ED entityData);

	E getInstanceFromData (AssetManager manager, ED data);
}
