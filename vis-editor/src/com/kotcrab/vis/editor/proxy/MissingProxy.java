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

package com.kotcrab.vis.editor.proxy;

import com.artemis.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.runtime.properties.BoundsOwner;
import com.kotcrab.vis.runtime.properties.PositionOwner;
import com.kotcrab.vis.runtime.properties.SizeOwner;

/** @author Kotcrab */
public class MissingProxy extends EntityProxy {
	private Rectangle rectangle = new Rectangle();

	private PositionOwner posOwner;
	private SizeOwner sizeOwner;
	private BoundsOwner boundsOwner;

	public MissingProxy (Entity entity) {
		super(entity);
	}

	@Override
	protected void createAccessors () {
		posOwner = new PositionOwner() {
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
		};

		sizeOwner = new SizeOwner() {
			@Override
			public float getWidth () {
				return 0;
			}

			@Override
			public float getHeight () {
				return 0;
			}
		};

		boundsOwner = () -> rectangle;
	}

	@Override
	protected void reloadAccessors () {
		enableBasicProperties(posOwner, sizeOwner, boundsOwner);
	}

	@Override
	public String getEntityName () {
		return "~Missing Proxy";
	}
}
