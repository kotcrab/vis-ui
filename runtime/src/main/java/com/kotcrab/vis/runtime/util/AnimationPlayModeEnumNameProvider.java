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

package com.kotcrab.vis.runtime.util;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.kotcrab.vis.runtime.util.autotable.EnumNameProvider;

/** @author Kotcrab */
public class AnimationPlayModeEnumNameProvider implements EnumNameProvider<Animation.PlayMode> {
	@Override
	public String getPrettyName (Animation.PlayMode value) {
		switch (value) {
			case NORMAL:
				return "Normal";
			case REVERSED:
				return "Reversed";
			case LOOP:
				return "Loop";
			case LOOP_REVERSED:
				return "Loop Reversed";
			case LOOP_PINGPONG:
				return "Loop Ping-Pong";
			case LOOP_RANDOM:
				return "Loop Random";
		}

		throw new IllegalStateException("Missing enum case for: " + value);
	}
}
