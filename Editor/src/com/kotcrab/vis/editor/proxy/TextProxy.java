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
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.TextComponent;

/** @author Kotcrab */
public class TextProxy extends EntityProxy {

	public TextProxy (Entity entity) {
		super(entity);
	}

	@Override
	protected BasicPropertiesAccessor initAccessors () {
		TextComponent c = entity.getComponent(TextComponent.class);
		enableOrigin(c);
		enableScale(c);
		enableTint(c);
		enableRotation(c);
		return c;
	}

	@Override
	protected String getEntityName () {
		return "TextEntity";
	}

	@Override
	boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor) {
		return assetDescriptor instanceof BmpFontAsset || assetDescriptor instanceof TtfFontAsset;
	}
}
