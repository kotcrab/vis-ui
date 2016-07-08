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

package com.kotcrab.vis.runtime.data;

/**
 * Stores scene physics settings exported from VisEditor.
 * @author Kotcrab
 */
public class PhysicsSettings {
	public boolean physicsEnabled;
	public float gravityX;
	public float gravityY;
	public boolean allowSleep;

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
