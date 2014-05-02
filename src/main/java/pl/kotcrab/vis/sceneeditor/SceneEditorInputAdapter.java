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
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class SceneEditorInputAdapter extends InputAdapter implements GestureListener {
	/** Attaches own InputProcessor to all others currently set. This must be called when changed active InputProcessor (called
	 * Gdx.input.setInputProcessor()) */
	public void attachInputProcessor () {
		if (Gdx.input.getInputProcessor() == null) {
			InputMultiplexer mul = new InputMultiplexer();
			mul.addProcessor(this);
			mul.addProcessor(new GestureDetector(this));
			Gdx.input.setInputProcessor(mul);
			return;
		}

		if (Gdx.input.getInputProcessor() instanceof InputMultiplexer) {
			InputMultiplexer mul = (InputMultiplexer)Gdx.input.getInputProcessor();
			mul.addProcessor(0, this);
			mul.addProcessor(1, new GestureDetector(this));
			Gdx.input.setInputProcessor(mul);
		} else {
			InputMultiplexer mul = new InputMultiplexer();
			mul.addProcessor(this);
			mul.addProcessor(new GestureDetector(this));
			mul.addProcessor(Gdx.input.getInputProcessor());
			Gdx.input.setInputProcessor(mul);
		}
	}

	@Override
	public boolean touchDown (float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap (float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress (float x, float y) {
		return false;
	}

	@Override
	public boolean fling (float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan (float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override
	public boolean panStop (float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom (float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch (Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}
}
