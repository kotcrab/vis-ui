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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.runtime.util.autotable.ATStringStringMap;

/**
 * Stores user defined variables in key-value store.
 * @author Kotcrab
 */
public class Variables extends Component {
	@ATStringStringMap
	public final ObjectMap<String, String> variables = new ObjectMap<String, String>();

	public Variables () {
	}

	public Variables (Variables vars) {
		this(vars.variables);
	}

	public Variables (ObjectMap<String, String> newVars) {
		variables.putAll(newVars);
	}

	public void setFrom (Variables newVars) {
		setFrom(newVars.variables);
	}

	public void setFrom (ObjectMap<String, String> newVars) {
		variables.clear();
		variables.putAll(newVars);
	}

	public String get (String variableName) {
		return variables.get(variableName);
	}

	public String get (String variableName, String defaultValue) {
		return variables.get(variableName, defaultValue);
	}

	public float getFloat (String variableName) {
		return Float.valueOf(get(variableName));
	}

	public float getFloat (String variableName, float defaultValue) {
		String stringVal = get(variableName);
		if (stringVal == null) return defaultValue;
		return Float.valueOf(get(variableName));
	}

	public int getInt (String variableName) {
		return Integer.valueOf(get(variableName));
	}

	public int getInt (String variableName, int defaultValue) {
		String stringVal = get(variableName);
		if (stringVal == null) return defaultValue;
		return Integer.valueOf(get(variableName));
	}

	public boolean getBoolean (String variableName) {
		return Boolean.valueOf(get(variableName));
	}

	public boolean getBoolean (String variableName, boolean defaultValue) {
		String stringVal = get(variableName);
		if (stringVal == null) return defaultValue;
		return Boolean.valueOf(get(variableName));
	}

	public void put (String variableName, String value) {
		variables.put(variableName, value);
	}

	public void putFloat (String variableName, float value) {
		variables.put(variableName, String.valueOf(value));
	}

	public void putInt (String variableName, int value) {
		variables.put(variableName, String.valueOf(value));
	}

	public void putBoolean (String variableName, boolean value) {
		variables.put(variableName, String.valueOf(value));
	}
}
