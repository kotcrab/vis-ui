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

package com.kotcrab.vis.runtime.data;

import com.kotcrab.annotation.CallSuper;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.entity.Entity;

/**
 * Base class for all entities data inside scene. Subclasses of this class are directly serialized into
 * JSON file during scene exporting in VisEditor.
 * @author Kotcrab
 */
@Deprecated
public abstract class EntityData<T extends Entity> {
	public String id;
	public VisAssetDescriptor assetDescriptor;

	@CallSuper
	/** Saves all values from this entity to instance of this class */
	public void saveFrom (T entity, VisAssetDescriptor assetDescriptor) {
		this.id = entity.getId();
		this.assetDescriptor = assetDescriptor;
	}

	/**
	 * Loads all possible values from this entity to instance of this class. If value can't be loaded it should
	 * be ignored, in such cases it is typically handled by VisEditor serializer.
	 */
	@CallSuper
	public void loadTo (T entity) {
		entity.setId(id);
	}
}
