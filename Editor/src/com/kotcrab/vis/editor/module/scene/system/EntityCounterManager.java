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

package com.kotcrab.vis.editor.module.scene.system;

import com.artemis.Entity;
import com.artemis.Manager;

/** @author Kotcrab */
public class EntityCounterManager extends Manager {
	private int count;

	@Override
	public void added (Entity e) {
		count++;
	}

	@Override
	public void deleted (Entity e) {
		count--;
	}

	public int getEntityCount () {
		return count;
	}
}
