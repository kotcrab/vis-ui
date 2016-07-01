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

package com.kotcrab.vis.plugin.spriter.proxy;

import com.artemis.Entity;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.plugin.spriter.runtime.component.VisSpriter;
import com.kotcrab.vis.runtime.component.Transform;

/** @author Kotcrab */
public class SpriterProxy extends EntityProxy {
	public SpriterProxy (Entity entity) {
		super(entity);
	}

	@Override
	protected void createAccessors () {
	}

	@Override
	protected void reloadAccessors () {
		VisSpriter spriter = getComponent(VisSpriter.class);
		Transform transform = getComponent(Transform.class);
		enableBasicProperties(transform, spriter, spriter);
		enableFlip(spriter);
		enableRotation(transform);
	}

	@Override
	public String getEntityName () {
		return "Spriter Skeleton";
	}
}
