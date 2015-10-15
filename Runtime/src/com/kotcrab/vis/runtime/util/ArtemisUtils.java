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
import com.kotcrab.vis.runtime.system.render.*;

/**
 * Various Artemis related utils
 * @author Kotcrab
 */
public class ArtemisUtils {
	public static void createCommonSystems (EntityEngineConfiguration config, Batch batch, ShaderProgram distanceFieldShader, boolean usingFromEditor) {
		RenderBatchingSystem batchingSystem = new RenderBatchingSystem(batch, usingFromEditor);
		config.setSystem(batchingSystem);
		config.setSystem(new VisSpriteRenderSystem(batchingSystem));
		config.setSystem(new SpriteRenderSystem(batchingSystem), true);
		config.setSystem(new TextRenderSystem(batchingSystem, distanceFieldShader), true);
		config.setSystem(new SpriterRenderSystem(batchingSystem), true);
	}
}
