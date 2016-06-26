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

/** Represents identifier of USL lang, see subclasses. */
public abstract class Identifier {
	public String name;

	public Identifier () {
	}

	public Identifier (String name) {
		this.name = name;
	}

	public Identifier (Identifier other) {
		this.name = other.name;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Identifier that = (Identifier) o;

		return name.equals(that.name);

	}

	@Override
	public int hashCode () {
		return name.hashCode();
	}
}
