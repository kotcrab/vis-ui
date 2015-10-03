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

package com.kotcrab.vis.runtime.data;

import com.kotcrab.vis.runtime.util.annotation.VisTag;

/** @author Kotcrab */
public class PhysicsSettings {
	@VisTag(0) public boolean physicsEnabled;
	@VisTag(1) public float gravityX;
	@VisTag(2) public float gravityY;
	@VisTag(3) public boolean allowSleep;

	public PhysicsSettings () {
	}

	public PhysicsSettings (boolean physicsEnabled, float gravityX, float gravityY, boolean allowSleep) {
		this.physicsEnabled = physicsEnabled;
		this.gravityX = gravityX;
		this.gravityY = gravityY;
		this.allowSleep = allowSleep;
	}

	public PhysicsSettings (PhysicsSettings other) {
		this.physicsEnabled = other.physicsEnabled;
		this.gravityX = other.gravityX;
		this.gravityY = other.gravityY;
		this.allowSleep = other.allowSleep;
	}
}
