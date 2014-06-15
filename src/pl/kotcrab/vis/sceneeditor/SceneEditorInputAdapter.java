/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;

public class SceneEditorInputAdapter extends InputAdapter {
	/** Attaches own InputProcessor to all others currently set. This must be called when changed active InputProcessor (called
	 * Gdx.input.setInputProcessor()) */
	public void attachInputProcessor () {
		if (Gdx.input.getInputProcessor() == null) {
			InputMultiplexer mul = new InputMultiplexer();
			mul.addProcessor(this);
			Gdx.input.setInputProcessor(mul);
			return;
		}

		if (Gdx.input.getInputProcessor() instanceof InputMultiplexer) {
			InputMultiplexer mul = (InputMultiplexer)Gdx.input.getInputProcessor();
			mul.addProcessor(0, this);
			Gdx.input.setInputProcessor(mul);
		} else {
			InputMultiplexer mul = new InputMultiplexer();
			mul.addProcessor(this);
			mul.addProcessor(Gdx.input.getInputProcessor());
			Gdx.input.setInputProcessor(mul);
		}
	}
}
