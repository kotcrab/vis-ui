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

/** Represents style identifier of USL lang */
public class StyleIdentifier extends GroupIdentifier {
	public boolean metaStyle;

	public StyleIdentifier () {
	}

	public StyleIdentifier (StyleIdentifier other) {
		super(other);
		this.metaStyle = other.metaStyle;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		StyleIdentifier that = (StyleIdentifier) o;

		return metaStyle == that.metaStyle;

	}

	@Override
	public int hashCode () {
		int result = super.hashCode();
		result = 31 * result + (metaStyle ? 1 : 0);
		return result;
	}
}
