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

import com.artemis.Entity;
import com.kotcrab.vis.runtime.accessor.BasicPropertiesAccessor;
import com.kotcrab.vis.runtime.assets.SpriterAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.SpriterComponent;

/** @author Kotcrab */
public class SpriterProxy extends EntityProxy {

	public SpriterProxy (Entity entity) {
		super(entity);
	}

	@Override
	protected BasicPropertiesAccessor initAccessors () {
		SpriterComponent c = entity.getComponent(SpriterComponent.class);
		enableFlip(c);
		enableRotation(c);
		return c;
	}

	@Override
	public String getEntityName () {
		return "SpriterEntity";
	}

	@Override
	public boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor) {
		return assetDescriptor instanceof SpriterAsset;
	}
}
