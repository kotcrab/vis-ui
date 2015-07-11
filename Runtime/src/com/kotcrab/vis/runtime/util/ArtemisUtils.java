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

package com.kotcrab.vis.runtime.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.kotcrab.vis.runtime.system.RenderBatchingSystem;
import com.kotcrab.vis.runtime.system.SpriteRenderSystem;
import com.kotcrab.vis.runtime.system.TextRenderSystem;

/** @author Kotcrab */
public class ArtemisUtils {
	public static void createCommonSystems (EntityEngine engine, Batch batch, ShaderProgram distanceFieldShader, boolean controlBatchState) {
		RenderBatchingSystem batchingSystem = new RenderBatchingSystem(batch, controlBatchState);
		engine.setSystem(batchingSystem);
		engine.setSystem(new SpriteRenderSystem(batchingSystem), true);
		engine.setSystem(new TextRenderSystem(batchingSystem, distanceFieldShader), true);
	}
}
