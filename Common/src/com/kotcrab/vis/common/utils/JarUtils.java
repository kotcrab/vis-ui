package com.kotcrab.vis.common.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class JarUtils {
	public static String getJarPath (Class<?> caller) {
		try {
			URL url = caller.getProtectionDomain().getCodeSource().getLocation();
			String path = URLDecoder.decode(url.getFile(), "UTF-8");
			path = path.substring(1, path.lastIndexOf('/')); // remove jar name from path

			if (path.endsWith("target/classes")) //launched from ide, remove classes from path
				path = path.substring(0, path.length() - "/classes".length());

			path = path.replace("/", File.separator);
			return path + File.separator;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Failed to get jar path due to unsupported encoding!", e);
		}
	}
}
