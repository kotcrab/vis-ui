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
