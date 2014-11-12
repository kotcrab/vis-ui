
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
