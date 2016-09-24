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

package com.kotcrab.vis.runtime.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.kotcrab.vis.runtime.component.*;

/**
 * System responsible for cleaning dirty properties on entity components.
 * @author Kotcrab
 */
public class DirtyCleanerSystem extends IteratingSystem {
	private ComponentMapper<Transform> transformCm;
	private ComponentMapper<Origin> originCm;
	private ComponentMapper<Tint> tintCm;
	private ComponentMapper<VisSpriteAnimation> spriteAnimCm;

	public DirtyCleanerSystem () {
		super(Aspect.one(Transform.class, Origin.class, Tint.class, VisSpriteAnimation.class).exclude(Invisible.class));
	}

	@Override
	protected void process (int entityId) {
		Transform transform = transformCm.get(entityId);
		Origin origin = originCm.get(entityId);
		Tint tint = tintCm.get(entityId);
		VisSpriteAnimation anim = spriteAnimCm.get(entityId);

		if (transform != null) transform.setDirty(false);
		if (origin != null) origin.setDirty(false);
		if (tint != null) tint.setDirty(false);
		if (anim != null) anim.setDirty(false);
	}
}
