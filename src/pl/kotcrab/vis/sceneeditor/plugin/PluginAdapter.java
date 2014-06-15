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

package pl.kotcrab.vis.sceneeditor.plugin;

import pl.kotcrab.vis.sceneeditor.EditorState;

public class PluginAdapter implements Plugin {

	public static final String TAG = "VisSceneEditor";

	@Override
	public void init (EditorState state) {
	}

	@Override
	public void enable () {
	}

	@Override
	public void disable () {
	}

	@Override
	public void render () {
	}

	@Override
	public void dispose () {
	}

	@Override
	public void resize () {

	}

	@Override
	public boolean keyDown (int keycode) {
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		return false;
	}

	@Override
	public boolean touchDown (int sceneX, int sceneY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp (int sceneX, int sceneY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged (int sceneX, int sceneY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved (int sceneX, int sceneY) {
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		return false;
	}

	@Override
	public boolean touchDown (float x, float y, int pointer, int button) {
		return false;
	}

}
