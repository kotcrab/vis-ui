
package pl.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;

import pl.kotcrab.vis.editor.util.DirectoryWatcher;
import pl.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;

public class AssetsWatcherModule extends ProjectModule {
	private DirectoryWatcher watcher;

	@Override
	public void init () {
		FileAccessModule fileAccess = projectContainter.get(FileAccessModule.class);
		FileHandle assetsFolder = fileAccess.getAssetsFolder();

		watcher = new DirectoryWatcher(assetsFolder.file().toPath());
		watcher.start();
	}

	@Override
	public void dispose () {
		watcher.stop();
	}

	public void addListener (WatchListener listener) {
		watcher.addListener(listener);
	}

	public boolean removeListener (WatchListener listener) {
		return watcher.removeListener(listener);
	}
}
