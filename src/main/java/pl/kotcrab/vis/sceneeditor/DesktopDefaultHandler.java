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

import pl.kotcrab.vis.sceneeditor.serializer.ObjectsData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class DesktopDefaultHandler implements DesktopInterface {

	@Override
	public void createBackupFile (String TAG, String filePath, String backupFolderPath) {
	}

	@Override
	public boolean saveJsonDataToFile (String TAG, String filePath, Json json, ObjectsData data) {
		Gdx.app.error(TAG, "SceneEditorConfig.desktopInterface not set, saving is disabled! "
			+ "Add 'SceneEditorConfig.desktopInterface = new DesktopHandler();' in your Libgdx desktop project!");
		return false;
	}

	@Override
	public boolean lastChanceSave () {
		return false;
	}

}
