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

package com.kotcrab.vis.usl.lang;

import com.kotcrab.vis.usl.CollectionUtils;

import java.util.ArrayList;

/** Represents group identifier of USL lang */
public class GroupIdentifier extends Identifier {
	public ArrayList<String> inherits = new ArrayList<String>();
	public ArrayList<Identifier> content = new ArrayList<Identifier>();

	public GroupIdentifier () {
	}

	public GroupIdentifier (GroupIdentifier other) {
		super(other);
		this.inherits = new ArrayList<String>(other.inherits);

		for (Identifier id : other.content) {
			if (id instanceof BasicIdentifier)
				this.content.add(new BasicIdentifier((BasicIdentifier) id));

			if (id instanceof GroupIdentifier)
				this.content.add(new GroupIdentifier((GroupIdentifier) id));
		}
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		GroupIdentifier that = (GroupIdentifier) o;

		if (!CollectionUtils.isEqualCollection(inherits, that.inherits)) return false;
		return CollectionUtils.isEqualCollection(content, that.content);

	}

	@Override
	public int hashCode () {
		int result = super.hashCode();
		result = 31 * result + inherits.hashCode();
		result = 31 * result + content.hashCode();
		return result;
	}
}
