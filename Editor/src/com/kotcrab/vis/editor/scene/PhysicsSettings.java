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

package com.kotcrab.vis.editor.scene;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

/** @author Kotcrab */
public class PhysicsSettings {
	@Tag(0) public boolean physicsEnabled = false;
	@Tag(1) public float gravityX = 0;
	@Tag(2) public float gravityY = 0;
	@Tag(3) public boolean allowSleep = true;

	public PhysicsSettings () {
	}

	public PhysicsSettings (PhysicsSettings other) {
		this.physicsEnabled = other.physicsEnabled;
		this.gravityX = other.gravityX;
		this.gravityY = other.gravityY;
		this.allowSleep = other.allowSleep;
	}
}
