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

/** Represents single identifier of USL lang, similar to json: "name: value" */
public class BasicIdentifier extends Identifier {
	public String content;

	public BasicIdentifier () {
	}

	public BasicIdentifier (String name, String content) {
		super(name);
		this.content = content;
	}

	public BasicIdentifier (BasicIdentifier other) {
		super(other);
		this.content = other.content;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		BasicIdentifier that = (BasicIdentifier) o;

		return content.equals(that.content);

	}

	@Override
	public int hashCode () {
		int result = super.hashCode();
		result = 31 * result + content.hashCode();
		return result;
	}
}
