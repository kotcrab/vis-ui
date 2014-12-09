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

package pl.kotcrab.vis.ui.test;

import pl.kotcrab.vis.ui.VisUI;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TestLauncher {

	public static void main (String[] args) {
		LwjglApplicationConfiguration c = new LwjglApplicationConfiguration();
		c.width = 1000;
		c.height = 680;
		new LwjglApplication(new TestApplication(), c);
	}

}

class TestApplication extends ApplicationAdapter {
	private static final boolean USE_VIS_COMPONENTS = true;

	private Stage stage;

	@Override
	public void create () {
		VisUI.load();

		stage = new Stage(new ScreenViewport());

		Gdx.input.setInputProcessor(stage);

		stage.addActor(new TestWindow(USE_VIS_COMPONENTS));
		stage.addActor(new TestTree(USE_VIS_COMPONENTS));
		stage.addActor(new TestTextAreaAndScroll(USE_VIS_COMPONENTS));
		stage.addActor(new TestSplitPane(USE_VIS_COMPONENTS));
		stage.addActor(new TestVertical(USE_VIS_COMPONENTS));
		stage.addActor(new TestValidator(USE_VIS_COMPONENTS));
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void dispose () {
		VisUI.dispose();

		stage.dispose();
	}

}
