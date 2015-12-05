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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.kotcrab.vis.runtime.component.proto.ProtoVisParticle;
import com.kotcrab.vis.runtime.component.proto.ProtoComponent;
import com.kotcrab.vis.runtime.properties.PositionOwner;
import com.kotcrab.vis.runtime.properties.UsesProtoComponent;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;

/**
 * Stores particle effect
 * @author Kotcrab
 */
public class VisParticle extends Component implements UsesProtoComponent, PositionOwner {
	public transient ParticleEffect effect;
	@ATProperty(fieldName = "Active on start", tooltip = "Controls whether to automatically start this effect on runtime.\nIn editor, particle effect are always active.")
	public boolean active = true;

	public VisParticle () {
	}

	public VisParticle (ParticleEffect effect) {
		this.effect = effect;
	}

	public VisParticle (VisParticle original, ParticleEffect effect) {
		this.effect = effect;
		setPosition(original.getX(), original.getY());
		this.active = original.active;
	}

	@Override
	public ProtoComponent toProtoComponent () {
		return new ProtoVisParticle(this);
	}

	@Override
	public float getX () {
		return effect.getEmitters().get(0).getX();
	}

	@Override
	public void setX (float x) {
		effect.setPosition(x, getY());
		reset();
	}

	@Override
	public float getY () {
		return effect.getEmitters().get(0).getY();
	}

	@Override
	public void setY (float y) {
		effect.setPosition(getX(), y);
		reset();
	}

	@Override
	public void setPosition (float x, float y) {
		effect.setPosition(x, y);
		reset();
	}

	public void reset () {
		effect.reset();
	}
}
