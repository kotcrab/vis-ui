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

package com.kotcrab.vis.runtime.util.entity.composer;

import com.artemis.ComponentMapper;
import com.kotcrab.vis.runtime.component.Origin;
import com.kotcrab.vis.runtime.component.Tint;
import com.kotcrab.vis.runtime.component.VisSprite;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * Sprite type entity composer.
 * @author Kotcrab
 * @see EntityComposer
 * @since 0.3.3
 */
public class SpriteEntityComposer extends RenderableEntityComposer {
	private ComponentMapper<VisSprite> spriteCm;
	private ComponentMapper<Tint> tintCm;
	private ComponentMapper<Origin> orignCm;

	private VisSprite sprite;
	private Tint tint;
	private Origin origin;

	public SpriteEntityComposer (EntityEngine engine) {
		super(engine);
	}

	@Override
	protected void createComponents () {
		super.createComponents();
		sprite = spriteCm.create(entity);
		tint = tintCm.create(entity);
		origin = orignCm.create(entity);
	}

	public VisSprite getSprite () {
		return sprite;
	}

	public Tint getTint () {
		return tint;
	}

	public Origin getOrigin () {
		return origin;
	}
}
