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

import com.badlogic.gdx.utils.Json;

/**
 * Funcitons that are not avaiable on GWT, muse be implemented via this interface
 * @author Pawel Pastuszak
 *
 */
public interface DesktopInterface
{
	public void createBackupFile(String TAG, String filePath, String backupFolderPath);
	public boolean saveJsonDataToFile(String TAG, String filePath, Json json, ObjectsData data);
	public boolean lastChanceSave();
}