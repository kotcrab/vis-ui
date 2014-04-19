
package pl.kotcrab.vis.sceneeditor.serializer;

import pl.kotcrab.vis.sceneeditor.SceneEditor;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.accessor.SceneEditorAccessor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
/**
 * Base class for Json based SceneSerializer, this object does not perfrom serlization and saving data, child class must do it
 * @author Pawel Pastuszak
 */
public class FileSerializer implements SceneSerializer {
	private static final String TAG = "VisSceneEditor:FileSerializer";
	
	private SceneEditor editor;
	private FileHandle file;
	private ObjectMap<String, Object> objectMap;
	protected String assetsPath;

	private Json json;
	
	/** Path to backup folder, must be ended with File.separator */
	private String backupFolderPath;

	public FileSerializer (SceneEditor editor, FileHandle file) {
		this.editor = editor;
		this.file = file;

		json = new Json();
		json.addClassTag("objectInfo", ObjectInfo.class);
	}

	@Override
	public void init (String assetsPath, ObjectMap<String, Object> objectMap) {
		this.objectMap = objectMap;
		this.assetsPath = assetsPath;
	}

	/** Loads all properties from provied scene file. If file does not exist it will do nothing */
	public void load () {
		if (file.exists() == false) return;

		Array<ObjectInfo> infos = new Array<ObjectInfo>();
		infos = 	json.fromJson(ObjectsData.class, file).data;


		for (ObjectInfo info : infos) {
			SceneEditorAccessor sup = editor.getAccessorForIdentifier(info.accessorIdentifier);

			Object obj = objectMap.get(info.identifier);

			sup.setX(obj, info.x);
			sup.setY(obj, info.y);
			sup.setOrigin(obj, info.originX, info.originY);
			sup.setSize(obj, info.width, info.height);
			sup.setScale(obj, info.scaleX, info.scaleY);
			sup.setRotation(obj, info.rotation);
		}
	}

	/** Saves all changes to provied scene file */
	public boolean save () {
		createBackup();
		
		Array<ObjectInfo> infos = new Array<ObjectInfo>();

		for (Entry<String, Object> entry : objectMap.entries()) {

			Object obj = entry.value;

			SceneEditorAccessor sup = editor.getAccessorForClass(obj.getClass());

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
		return SceneEditorConfig.desktopInterface.saveJsonDataToFile(TAG, assetsPath + file.path(), json, data);
	}

	/** Backup provided scene file */
	private void createBackup () {
		if (file.exists() && backupFolderPath != null) {
			SceneEditorConfig.desktopInterface.createBackupFile(TAG, assetsPath + file.path(), backupFolderPath);
		}
	}
	

	public String getBackupFolderPath () {
		return backupFolderPath;
	}

	public void setBackupFolderPath (String backupFolderPath) {
		this.backupFolderPath = backupFolderPath;
	}
}
