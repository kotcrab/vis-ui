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

package com.kotcrab.vis.editor.serializer.cloner;

import com.artemis.utils.Bag;
import com.rits.cloning.IDeepCloner;

import java.util.Map;

/** @author Kotcrab */
@SuppressWarnings({"rawtypes", "unchecked"})
public class BagCloner extends VisCloner<Bag> {
	@Override
	protected Bag cloneObject (Bag original, IDeepCloner cloner, Map<Object, Object> clones) {
		Bag bag = new Bag(original.size());

		for (Object obj : original) {
			bag.add(cloner.deepClone(obj, clones));
		}

		return bag;
	}
}
