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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.ObjectMap.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
class Serializer
{
	private static final String TAG = "VisSceneEditor:Serializer";
	
	private SceneEditor editor;
	private ObjectMap<String, Object> objectMap;
	
	private Json json;
	private FileHandle file;
	
	public Serializer(SceneEditor editor, FileHandle file, ObjectMap<String, Object> objectMap)
	{
		this.editor = editor;
		this.file = file;
		this.objectMap = objectMap;
		
		json = new Json();
		json.addClassTag("objectInfo", ObjectInfo.class);
	}
	
	/** Loads all properties from provied scene file. If file does not exist it will do nothing */
	public void load () {
		if (file.exists() == false) return;

		ArrayList<ObjectInfo> infos = new ArrayList<>();
		infos = json.fromJson(infos.getClass(), file);

		for (ObjectInfo info : infos) {
			try {
				Class<?> klass = Class.forName(info.className);
				SceneEditorSupport sup = editor.getSupportForClass(klass);

				Object obj = objectMap.get(info.identifier);

				sup.setX(obj, info.x);
				sup.setY(obj, info.y);
				sup.setOrigin(obj, info.originX, info.originY);
				sup.setSize(obj, info.width, info.height);
				sup.setScale(obj, info.scaleX, info.scaleY);
				sup.setRotation(obj, info.rotation);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	/** Saves all changes to provied scene file */
	public boolean save () {
		createBackup();

		ArrayList<ObjectInfo> infos = new ArrayList<>();

		for (Entry<String, Object> entry : objectMap.entries()) {

			Object obj = entry.value;

			SceneEditorSupport sup = editor.getSupportForClass(obj.getClass());

			ObjectInfo info = new ObjectInfo();
			info.className = obj.getClass().getName();
			info.identifier = entry.key;
			info.x = sup.getX(obj);
			info.y = sup.getY(obj);
			info.scaleX = sup.getScaleX(obj);
			info.scaleY = sup.getScaleY(obj);
			info.originX = sup.getOriginX(obj);
			info.originY = sup.getOriginY(obj);
			info.width = sup.getWidth(obj);
			info.height = sup.getHeight(obj);
			info.rotation = sup.getRotation(obj);

			infos.add(info);
		}

		try {
			if (SceneEditorConfig.assetsFolderPath == null)
				json.toJson(infos, Gdx.files.absolute(new File("").getAbsolutePath() + File.separator + file.path()));
			else
				json.toJson(infos, Gdx.files.absolute(SceneEditorConfig.assetsFolderPath + file.path()));

			Gdx.app.log(TAG, "Saved changes to file.");
			return true;
		} catch (SerializationException e) {
			Gdx.app.log(TAG, "Error while saving file.");
			e.printStackTrace();
			return false;
		}
	}

	/** Backup provided scene file */
	private void createBackup () {
		if (file.exists() && SceneEditorConfig.backupFolderPath != null) {
			try {
				String fileName = file.name();
				fileName = fileName.substring(0, fileName.lastIndexOf('.'));

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
				Date date = new Date();
				fileName += " - " + dateFormat.format(date) + file.extension();

				Files.copy(new File(new File("").getAbsolutePath() + File.separator + file.path()).toPath(), new File(
					SceneEditorConfig.backupFolderPath + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
				Gdx.app.log(TAG, "Backup file created.");
			} catch (IOException e) {
				Gdx.app.log(TAG, "Error while creating backup.");
				e.printStackTrace();
			}
		}
	}

}