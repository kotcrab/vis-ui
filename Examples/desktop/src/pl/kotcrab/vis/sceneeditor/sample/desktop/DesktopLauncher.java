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

package pl.kotcrab.vis.sceneeditor.sample.desktop;

import pl.kotcrab.vis.sceneeditor.example.Examples;
import pl.kotcrab.vis.sceneeditor.example.scene2d.ExampleScene2d;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher
{
	
	@SuppressWarnings("unused")
	public static void main(String[] arg)
	{
		
		ExampleSelector selector = new ExampleSelector(new ExampleSelected()
		{
			@Override
			public void exampleSelected(int exampleId)
			{
				LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
				config.width = 800;
				config.height = 480;
				
				switch (exampleId)
				{
				case 0:
					new LwjglApplication(new Examples(false), config);
					break;
				case 1:
					new LwjglApplication(new Examples(true), config);
					break;
				case 2:
					new LwjglApplication(new ExampleScene2d(), config);
					break;
				default:
					break;
				}
				
			}
		});
		
	}
}
