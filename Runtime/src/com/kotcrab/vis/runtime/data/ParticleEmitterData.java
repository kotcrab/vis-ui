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

package com.kotcrab.vis.runtime.data;

import com.kotcrab.vis.runtime.entity.ParticleEmitterEntity;

public class ParticleEmitterData extends EntityData<ParticleEmitterEntity> {
	public String relativePath;
	public float x, y;
	public boolean flipX, flipY;

	@Override
	public void saveFrom (ParticleEmitterEntity entity) {
		relativePath = entity.getRelativeEffectPath();
		x = entity.getX();
		y = entity.getY();
		flipX = entity.isFlipX();
		flipY = entity.isFlipY();
	}

	@Override
	public void loadTo (ParticleEmitterEntity entity) {
		entity.setEffectRelativePath(relativePath);
		entity.setPosition(x, y);
		entity.setFlip(flipX, flipY);
	}
}
