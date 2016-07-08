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

package com.kotcrab.vis.runtime.system.inflater;

import com.artemis.Aspect.Builder;
import com.artemis.BaseEntitySystem;

/**
 * Base class for all inflater systems. This is just a utility class because inflaters can extend any type of
 * entity system class.
 * @author Kotcrab
 */
public class InflaterSystem extends BaseEntitySystem {
	public InflaterSystem (Builder aspect) {
		super(aspect);
	}

	@Override
	protected void processSystem () {

	}
}
