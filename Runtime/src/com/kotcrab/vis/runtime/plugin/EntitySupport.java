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

/**
 * Interface for plugins that provide additional runtime entities support
 * @param <ED> custom entity data type (see {@link EntityData})
 * @param <E> custom entity type (see {@link Entity})
 * @author Kotcrab
 */
@Deprecated
public interface EntitySupport<ED extends EntityData<E>, E extends Entity> {
	/** Called when entity support should add it's loader into AssetsManager */
	void setLoaders (AssetManager manager);

	/** @return custom entity data class */
	Class<ED> getEntityDataClass ();

	/** Called when EntitySupport should resolve required dependencies for Entity and add them into dependency list */
	void resolveDependencies (Array<AssetDescriptor> deps, ED entityData);

	/**
	 * Called when EntitySupport should construct new entity instance from data and dependencies from asset manager
	 * (dependencies was loaded in {@link #resolveDependencies(Array, EntityData)})
	 */
	E getInstanceFromData (AssetManager manager, ED data);
}
