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
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.runtime.accessor.BasicPropertiesAccessor;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

/** @author Kotcrab */
public class MissingProxy extends EntityProxy {
	private Rectangle rectangle = new Rectangle();

	public MissingProxy (Entity entity) {
		super(entity);
	}

	@Override
	protected BasicPropertiesAccessor initAccessors () {
		return new BasicPropertiesAccessor() {
			@Override
			public float getX () {
				return 0;
			}

			@Override
			public void setX (float x) {

			}

			@Override
			public float getY () {
				return 0;
			}

			@Override
			public void setY (float y) {

			}

			@Override
			public void setPosition (float x, float y) {

			}

			@Override
			public float getWidth () {
				return 0;
			}

			@Override
			public float getHeight () {
				return 0;
			}

			@Override
			public Rectangle getBoundingRectangle () {
				return rectangle;
			}
		};
	}

	@Override
	public String getEntityName () {
		return "~Missing Proxy";
	}

	@Override
	protected boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor) {
		return false;
	}
}
