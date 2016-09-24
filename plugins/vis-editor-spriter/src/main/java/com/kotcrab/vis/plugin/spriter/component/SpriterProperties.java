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

package com.kotcrab.vis.plugin.spriter.component;

import com.artemis.Component;
import com.kotcrab.vis.editor.serializer.json.EditorJsonTags;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;

/**
 * DO NOT MOVE THIS CLASS - see {@link EditorJsonTags}
 * @author Kotcrab
 */
public class SpriterProperties extends Component {
	@ATProperty(fieldName = "Scale", min = 0.000001f)
	public float scale;

	public int animation = 0;

	@ATProperty(fieldName = "Play animation on start")
	public boolean playOnStart = false;
	@ATProperty(fieldName = "Preview in editor")
	public boolean previewInEditor = false;

	@Deprecated
	public SpriterProperties () {
	}

	public SpriterProperties (float scale) {
		this.scale = scale;
	}
}
