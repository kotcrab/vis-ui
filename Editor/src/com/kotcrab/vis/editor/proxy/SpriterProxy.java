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
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisSpriter;
import com.kotcrab.vis.runtime.component.VisSpriterChanged;

/** @author Kotcrab */
public class SpriterProxy extends EntityProxy {
	private ComponentMapper<VisSpriterChanged> changedCm;

	public SpriterProxy (Entity entity) {
		super(entity);
	}

	@Override
	protected void createAccessors () {
	}

	@Override
	protected void reloadAccessors () {
		Entity entity = getEntity();

		VisSpriter spriter = entity.getComponent(VisSpriter.class);
		Transform transform = entity.getComponent(Transform.class);
		enableBasicProperties(transform, spriter, spriter);
		enableFlip(spriter);
		enableRotation(transform);

		changedCm = entity.getWorld().getMapper(VisSpriterChanged.class);
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
	public void setRotation (float rotation) {
		super.setRotation(rotation);
		changedCm.create(getEntity());
	}

	@Override
	public String getEntityName () {
		return "Spriter Skeleton";
	}
}
