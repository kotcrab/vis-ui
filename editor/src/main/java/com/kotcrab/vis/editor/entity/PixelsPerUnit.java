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

package com.kotcrab.vis.editor.entity;

import com.artemis.Component;

/** @author Kotcrab */
public class PixelsPerUnit extends Component {
	public final float pixelsPerUnits;
	public final float scale;

	public PixelsPerUnit () {
		pixelsPerUnits = 0f;
		scale = 0f;
	}

	public PixelsPerUnit (float pixelsPerUnit) {
		this.pixelsPerUnits = pixelsPerUnit;
		this.scale = 1f / pixelsPerUnit;
	}
}
