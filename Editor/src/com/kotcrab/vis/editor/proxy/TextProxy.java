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

package com.kotcrab.vis.editor.proxy;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.runtime.component.*;

/** @author Kotcrab */
public class TextProxy extends EntityProxy {
	private ComponentMapper<VisTextChanged> changedCm;

	public TextProxy (Entity entity) {
		super(entity);
	}

	@Override
	protected void createAccessors () {
	}

	@Override
	protected void reloadAccessors () {
		Entity entity = getEntity();

		changedCm = entity.getWorld().getMapper(VisTextChanged.class);

		VisText text = entity.getComponent(VisText.class);
		Transform transform = entity.getComponent(Transform.class);
		Origin origin = entity.getComponent(Origin.class);
		Tint tint = entity.getComponent(Tint.class);

		enableBasicProperties(transform, text, text);
		enableOrigin(origin);
		enableScale(transform);
		enableTint(tint);
		enableRotation(transform);
	}

	@Override
	public String getEntityName () {
		return "Text";
	}

	@Override
	public void setX (float x) {
		super.setX(x);
		changedCm.create(getEntity());
	}

	@Override
	public void setY (float y) {
		super.setY(y);
		changedCm.create(getEntity());
	}

	@Override
	public void setPosition (float x, float y) {
		super.setPosition(x, y);
		changedCm.create(getEntity());
	}

	@Override
	public void setOrigin (float x, float y) {
		super.setOrigin(x, y);
		changedCm.create(getEntity());
	}

	@Override
	public void setScale (float x, float y) {
		super.setScale(x, y);
		changedCm.create(getEntity());
	}

	@Override
	public void setColor (Color color) {
		super.setColor(color);
		changedCm.create(getEntity()).contentChanged = true;
	}

	@Override
	public void setRotation (float rotation) {
		super.setRotation(rotation);
		changedCm.create(getEntity());
	}
}
