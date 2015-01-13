/*
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.runtime.scene;

public enum SceneViewport {
	NONE, STRETCH, FIT, FILL, SCREEN, EXTEND;

	public String toListString () {
		switch (this) {
		case NONE:
			return "None";
		case STRETCH:
			return "Stretch Viewport";
		case FIT:
			return "Fit Viewport";
		case FILL:
			return "Fill Viewport";
		case EXTEND:
			return "Extend Viewport";
		case SCREEN:
			return "Screen Viewport";
		default:
			return super.toString();
		}
	}
}
