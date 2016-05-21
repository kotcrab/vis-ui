/*
 * Copyright 2014-2016 See AUTHORS file.
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
 */

package com.kotcrab.vis.usl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Scanner;

/** @author Kotcrab */
public class IncludeLoader {
	private File cacheFolder = new File(USL.CACHE_FOLDER_PATH);
	private File tmpFolder = new File(USL.TMP_FOLDER_PATH);

	private ArrayList<String> includeSources = new ArrayList<>();

	public IncludeLoader () {
		cacheFolder.mkdirs();
		tmpFolder.mkdirs();

		String additionalIncludeDir = System.getProperty("usl.include.path");
		if (additionalIncludeDir != null) includeSources.add(additionalIncludeDir);
		includeSources.add("http://apps.kotcrab.com/vis/usl/");
		includeSources.add("https://raw.githubusercontent.com/kotcrab/vis-editor/master/usl/styles/");
	}

	public String loadInclude (String includeName) {
		return fileToString(loadIncludeFile(includeName));
	}

	private File loadIncludeFile (String includeName) {
		try {
			includeName += ".usl";
			File cacheFile = new File(cacheFolder, includeName);
			File tmpFile = new File(tmpFolder, includeName);
			if (cacheFile.exists()) return cacheFile;

			boolean snapshot = includeName.endsWith("-SNAPSHOT.usl");

			for (String includeSource : includeSources) {
				if (includeSource.startsWith("https://") || includeSource.startsWith("http://")) {
					URL url = new URL(includeSource + includeName);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					final int responseCode = connection.getResponseCode();
					if (responseCode != 200) {
						continue;
					}

					System.out.println("Download include file " + includeSource + includeName + "...");
					ReadableByteChannel rbc = Channels.newChannel(url.openStream());
					FileOutputStream fos = new FileOutputStream(snapshot ? tmpFile : cacheFile);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					return snapshot ? tmpFile : cacheFile;

				} else {
					File localFile = new File(includeSource, includeName);
					if (localFile.exists()) return localFile;
				}
			}

			StringBuilder exceptionMsg = new StringBuilder();
			exceptionMsg.append("Could not find '").append(includeName).append("' include. Searched in the following locations:\n");
			for (String includeSource : includeSources) {
				exceptionMsg.append("\t").append(includeSource + includeName).append("\n");
			}
			throw new IllegalStateException(exceptionMsg.toString());
		} catch (IOException e) {
			throw new IllegalStateException("Error during include file downloading", e);
		}
	}

	private String fileToString (File file) {
		try {
			Scanner s = new Scanner(file).useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Error reading file", e);
		}
	}

	public void addIncludeSource (String path) {
		includeSources.add(0, path);
	}
}
