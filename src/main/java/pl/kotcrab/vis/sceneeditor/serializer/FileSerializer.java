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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pl.kotcrab.vis.sceneeditor.SceneEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SerializationException;

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
			try {
				String fileName = file.name();
				fileName = fileName.substring(0, fileName.lastIndexOf('.'));

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
				Date date = new Date();
				fileName += " - " + dateFormat.format(date) + file.extension();

				Files.copy(new File(new File("").getAbsolutePath() + File.separator + file.path()).toPath(), new File(
					backupFolderPath + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
				Gdx.app.log(TAG, "Backup file created.");
			} catch (IOException e) {
				Gdx.app.error(TAG, "Error while creating backup.");
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean saveJsonData (ArrayList<ObjectInfo> infos) {
		try {
			getJson().toJson(infos, new FileWriter(file.file()));
			Gdx.app.log(TAG, "Saved changes to file.");
			return true;
		} catch (SerializationException e) {
			Gdx.app.error(TAG, "Serialization error while saving file.");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Gdx.app.error(TAG, "IO error while saving file.");
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<ObjectInfo> loadJsonData () {
		return getJson().fromJson(new ArrayList<ObjectInfo>().getClass(), file);
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
