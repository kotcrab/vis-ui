/*
 * Copyright 2014-2015 Pawel Pastuszak
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

/** Base class for all entities data inside scene */
public abstract class EntityData<T> {
	public String id;

	/** Saves all values from this entity to instance of this class */
	public abstract void saveFrom (T entity);

	/** Loads all possible values from this entity to instance of this class. If value can't be loaded it should be ignored. */
	public abstract void loadTo (T entity);
}
