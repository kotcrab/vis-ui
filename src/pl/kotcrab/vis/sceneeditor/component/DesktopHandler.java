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

package pl.kotcrab.vis.sceneeditor.component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.kotcrab.vis.sceneeditor.serializer.ObjectsData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

/** This class is not included when compiling to GWT, here all functions not avaiable on GWT must be implemented
 * @author Pawel Pastuszak */
public class DesktopHandler implements DesktopInterface {

	@Override
	public void createBackupFile (String TAG, String filePath, String backupFolderPath) {
		try {

			String fileName = filePath.substring(filePath.lastIndexOf(File.separator));

			String extension = fileName.substring(fileName.lastIndexOf('.') - 1);
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			Date date = new Date();
			fileName += " - " + dateFormat.format(date) + extension;

			Files.copy(new File(filePath).toPath(), new File(backupFolderPath + fileName).toPath(),
				StandardCopyOption.REPLACE_EXISTING);
			Gdx.app.log(TAG, "Backup file created.");
		} catch (IOException e) {
			Gdx.app.error(TAG, "Error while creating backup.");
			e.printStackTrace();
		}
	}

	@Override
	public boolean saveJsonDataToFile (String TAG, String filePath, Json json, ObjectsData data) {
		try {
			json.toJson(data, new FileWriter(new File(filePath)));
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
		System.out.println("Exited before saving! It's you last chance to save! Save changes? (Y/N)");

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
