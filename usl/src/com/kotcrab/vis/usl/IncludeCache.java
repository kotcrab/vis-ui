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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/** @author Kotcrab */
public class IncludeCache {
	private File cacheFolder = new File(USL.CACHE_FOLDER_PATH);

	public IncludeCache () {
		cacheFolder.mkdirs();
	}

	public File loadInclude (String filePath) {
		try {
			String fileName = filePath.substring(filePath.lastIndexOf('/'));
			File cacheFile = new File(cacheFolder, fileName);
			if (cacheFile.exists()) return cacheFile;

			URL url = new URL(filePath);
			System.out.println("Download include file " + filePath + "...");
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream(cacheFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			return cacheFile;
		} catch (IOException e) {
			throw new IllegalStateException("Error during include file downloading", e);
		}
	}
}
