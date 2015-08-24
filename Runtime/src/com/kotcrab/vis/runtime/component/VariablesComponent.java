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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.runtime.util.annotation.VisTag;
import com.kotcrab.vis.runtime.util.autotable.ATStringStringMap;

/**
 * Stores user defined variables in key-value store
 * @author Kotcrab
 */
public class VariablesComponent extends Component {
	@VisTag(0) @ATStringStringMap
	public ObjectMap<String, String> variables = new ObjectMap<String, String>();

	public String get (String variableName) {
		return variables.get(variableName);
	}

	public float getFloat (String variableName) {
		return Float.valueOf(get(variableName));
	}

	public int getInt (String variableName) {
		return Integer.valueOf(get(variableName));
	}

	public boolean getBoolean (String variableName) {
		return Boolean.valueOf(get(variableName));
	}
}
