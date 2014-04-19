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

package pl.kotcrab.vis.sceneeditor.serializer;

import pl.kotcrab.vis.sceneeditor.SceneEditor;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/** Json based file serializer
 * @author Pawel Pastuszak */
public class FileSerializer extends AbstractJsonSerializer {
	private static final String TAG = "VisSceneEditor:FileSerializer";

	private FileHandle file;

	/** Path to backup folder, must be ended with File.separator */
	private String backupFolderPath;

	public FileSerializer (SceneEditor editor, FileHandle file) {
		super(editor);
		this.file = file;
	}

	/** Saves all changes to provied scene file */
	@Override
	public boolean save () {
		createBackup();

		return super.save();
	}

	/** Backup provided scene file */
	private void createBackup () {
		if (file.exists() && backupFolderPath != null) {
			SceneEditorConfig.desktopInterface.createBackupFile(TAG, file, backupFolderPath);
		}
	}

	@Override
	public boolean saveJsonData (Array<ObjectInfo> infos) {
		ObjectsData data = new ObjectsData();
		data.versionCode = SceneEditorConfig.VERSION_CODE;
		data.data = infos;
		return SceneEditorConfig.desktopInterface.saveJsonDataToFile(TAG, file, getJson(), data);
	}

	@Override
	public Array<ObjectInfo> loadJsonData () {
		return getJson().fromJson(ObjectsData.class, file).data;
	}

	@Override
	public boolean isReadyToLoad () {
		return file.exists();
	}

	public String getBackupFolderPath () {
		return backupFolderPath;
	}

	public void setBackupFolderPath (String backupFolderPath) {
		this.backupFolderPath = backupFolderPath;
	}
}
