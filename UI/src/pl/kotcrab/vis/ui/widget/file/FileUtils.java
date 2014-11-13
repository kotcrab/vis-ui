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

package pl.kotcrab.vis.ui.widget.file;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

import com.badlogic.gdx.utils.Array;

public class FileUtils {
	private static final String[] units = new String[] {"B", "KB", "MB", "GB", "TB", "EB"};

	public static String readableFileSize (long size) {
		if (size <= 0) return "0 B";
		int digitGroups = (int)(Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)).replace(",", ".") + " " + units[digitGroups];
	}

	public static Array<File> sortFiles (File[] files) {
		Array<File> directoriesList = new Array<File>();
		Array<File> filesList = new Array<File>();

		Arrays.sort(files);

		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory())
				directoriesList.add(f);
			else
				filesList.add(f);
		}

		directoriesList.addAll(filesList); // combine lists
		return directoriesList;
	}
}
