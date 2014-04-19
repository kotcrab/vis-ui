
package pl.kotcrab.vis.sceneeditor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.kotcrab.vis.sceneeditor.serializer.ObjectInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

/** This class is not included when compiling to GWT, here all functions not avaiable on GWT must be implemented
 * @author Pawel Pastuszak */
public class DesktopHandler implements DesktopInterface {

	@Override
	public void createBackupFile (String TAG, FileHandle file, String backupFolderPath) {
		try {
			String fileName = file.name();
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			Date date = new Date();
			fileName += " - " + dateFormat.format(date) + file.extension();

			Files.copy(new File(new File("").getAbsolutePath() + File.separator + file.path()).toPath(), new File(backupFolderPath
				+ fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
			Gdx.app.log(TAG, "Backup file created.");
		} catch (IOException e) {
			Gdx.app.error(TAG, "Error while creating backup.");
			e.printStackTrace();
		}
	}

	@Override
	public boolean saveJsonDataToFile (String TAG, FileHandle file, Json json, Array<ObjectInfo> infos) {
		try {
			json.toJson(infos, new FileWriter(file.file()));
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

	@Override
	public boolean lastChanceSave () {
		try {
			while (true) {
				char input = '0';
				input = (char)System.in.read();

				if (input == 'Y' || input == 'y') {
					System.out.println("Good choice!");
					return true;
				} else if (input == 'N' || input == 'n') {
					System.out.println("Ok, bye!");
					return false;
				} else {
					System.out.println("Wrong key :( Try again.");
					System.in.skip(Long.MAX_VALUE);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
