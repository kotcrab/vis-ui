/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.util.value;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Value;

/**
 * Value that returns given fixed constant value if widget is visible. If actor is invisible then returns 0.
 * @author Kotcrab
 * @since 1.1.0
 */
public class ConstantIfVisibleValue extends Value {
	private Actor actor;
	private float constant;

	public ConstantIfVisibleValue (float constant) {
		this.constant = constant;
	}

	public ConstantIfVisibleValue (Actor actor, float constant) {
		this.actor = actor;
		this.constant = constant;
	}

	@Override
	public float get (Actor context) {
		if (actor != null) context = actor;
		return context.isVisible() ? constant : 0;
	}
}
