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

package pl.kotcrab.vis.sceneeditor.plugin.impl;

import pl.kotcrab.vis.sceneeditor.AccessorHandler;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.accessor.SceneEditorAccessor;
import pl.kotcrab.vis.sceneeditor.plugin.PluginState;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.IObjectManager;
import pl.kotcrab.vis.sceneeditor.serializer.ObjectInfo;
import pl.kotcrab.vis.sceneeditor.serializer.ObjectsData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
/**
 * Json based SceneSerializer
 * @author Pawel Pastuszak
 */
public class FileSerializerPlugin extends PluginState {
	public static final String TAG = "VisSceneEditor:FileSerializer";

	private FileHandle file;
	private AccessorHandler<SceneEditorAccessor<?>> accessorHandler;
	private IObjectManager objectManager;

	private Json json;

	private String backupFolderPath;

	public FileSerializerPlugin (FileHandle file, AccessorHandler<SceneEditorAccessor<?>> accessorHandler,
		IObjectManager objectManager) {
		this.file = file;

		this.accessorHandler = accessorHandler;
		this.objectManager = objectManager;

		json = new Json();
		json.addClassTag("objectInfo", ObjectInfo.class);
	}

	/** Loads all properties from provied scene file. If file does not exist it will do nothing */
	public void load () {
		if (file.exists() == false) return;

		Array<ObjectInfo> infos;
		infos = json.fromJson(ObjectsData.class, file).data;

		for (ObjectInfo info : infos) {
			SceneEditorAccessor sup = accessorHandler.getAccessorForIdentifier(info.accessorIdentifier);

			Object obj = objectManager.getObjectMap().get(info.identifier);

			sup.setX(obj, info.x);
			sup.setY(obj, info.y);
			sup.setOrigin(obj, info.originX, info.originY);
			sup.setSize(obj, info.width, info.height);
			sup.setScale(obj, info.scaleX, info.scaleY);
			sup.setRotation(obj, info.rotation);
		}
	}

	private boolean saveToFile () {
		createBackup();

		Array<ObjectInfo> infos = new Array<ObjectInfo>();

		for (Entry<String, Object> entry : objectManager.getObjectMap().entries()) {

			Object obj = entry.value;

			SceneEditorAccessor sup = accessorHandler.getAccessorForClass(obj.getClass());

			ObjectInfo info = new ObjectInfo();
			info.accessorIdentifier = sup.getIdentifier();
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

		ObjectsData data = new ObjectsData();
		data.versionCode = SceneEditorConfig.VERSION_CODE;
		data.data = infos;

		if (SceneEditorConfig.getAssetsPath() != null)
			return SceneEditorConfig.desktopInterface.saveJsonDataToFile(TAG, SceneEditorConfig.getAssetsPath() + file.path(), json,
				data);
		else
			return false;
	}

	/** Saves all changes to provied scene file
	 * @return true if save succeed, false otherwise */
	public boolean save () {
		if (saveToFile()) {
			state.dirty = false;
			return true;
		} else
			return false;

	}

	@Override
	public boolean keyDown (int keycode) {
		if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_SPECIAL_ACTIONS)) {
			if (keycode == SceneEditorConfig.KEY_SPECIAL_SAVE_CHANGES) {
				save();
				return true;
			}
		}

		return false;
	}

	/** Backup provided scene file */
	private void createBackup () {
		if (file.exists() && backupFolderPath != null) {
			SceneEditorConfig.desktopInterface.createBackupFile(TAG, SceneEditorConfig.getAssetsPath() + file.path(),
				backupFolderPath);
		}
	}

	public String getBackupFolderPath () {
		return backupFolderPath;
	}

	/** Path to backup folder, must be ended with File.separator */
	public void setBackupFolderPath (String backupFolderPath) {
		this.backupFolderPath = backupFolderPath;
	}
}
