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

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardWatchEventKinds.*;

//https://github.com/syncany/syncany/blob/59cf87c72de4322c737f0073ce8a7ddd992fd898/syncany-lib/src/main/java/org/syncany/operations/watch/RecursiveWatcher.java

/**
 * The recursive file watcher monitors a folder (and its sub-folders).
 * <p>
 * <p>
 * The class walks through the file tree and registers to a watch to every sub-folder. For new folders, a new watch is registered,
 * and stale watches are removed.
 * <p>
 * <p>
 * When a file event occurs, a timer is started to wait for the file operations to settle. It is reset whenever a new event
 * occurs. When the timer times out, an event is thrown through the {@link WatchListener}.
 * @author Philipp C. Heckel <philipp.heckel@gmail.com>
 * @author Pawel Pastuszak
 */
@SuppressWarnings("unchecked")
public class DirectoryWatcher {
	private Path root;
	private int settleDelay;
	private Array<WatchListener> listeners;
	private AtomicBoolean running;
	private WatchService watchService;
	private Thread watchThread;
	private Map<Path, WatchKey> watchPathKeyMap;
	private Timer timer;

	public DirectoryWatcher (Path root) {
		this(root, null);
	}

	public DirectoryWatcher (Path root, WatchListener listener) {
		this.root = root;

		settleDelay = 1;
		listeners = new Array<>();
		running = new AtomicBoolean(false);
		watchService = null;
		watchThread = null;
		watchPathKeyMap = new HashMap<>();
		timer = null;

		if (listener != null) listeners.add(listener);
	}

	/**
	 * Starts the watcher service and registers watches in all of the sub-folders of the given root folder.
	 * <p>
	 * <p>
	 * <b>Important:</b> This method returns immediately, even though the watches might not be in place yet. For large file trees,
	 * it might take several seconds until all directories are being monitored. For normal cases (1-100 folders), this should not
	 * take longer than a few milliseconds.
	 */
	public void start () throws IOException {
		watchService = FileSystems.getDefault().newWatchService();
		watchThread = new Thread(() -> {
			running.set(true);
			walkTreeAndSetWatches();
			while (running.get()) {
				try {
					WatchKey watchKey = watchService.take();

					for (WatchEvent<?> event : watchKey.pollEvents()) {
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path dir = (Path) watchKey.watchable();
						Path fullPath = dir.resolve(ev.context());
						final FileHandle fileHandle = Gdx.files.absolute(fullPath.toFile().toString());

						if (ev.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
							Gdx.app.postRunnable(() -> {
								for (WatchListener listener : listeners)
									listener.fileChanged(fileHandle);
							});
						}

						if (ev.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
							Gdx.app.postRunnable(() -> {
								for (WatchListener listener : listeners)
									listener.fileDeleted(fileHandle);
							});
						}

						if (ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
							Gdx.app.postRunnable(() -> {
								for (WatchListener listener : listeners)
									listener.fileCreated(fileHandle);
							});
						}
					}

					watchKey.reset();
					resetWaitSettlementTimer();
				} catch (InterruptedException | ClosedWatchServiceException e) {
					running.set(false);
				}
			}
		}, "Watcher");
		watchThread.start();
	}

	public synchronized void stop () {
		if (watchThread != null) {
			try {
				watchService.close();
				running.set(false);
				watchThread.interrupt();
			} catch (IOException e) {
				// Don't care
			}
		}
	}

	private synchronized void resetWaitSettlementTimer () {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timer = new Timer("WatchTimer");
		timer.schedule(new TimerTask() {
			@Override
			public void run () {
				walkTreeAndSetWatches();
				unregisterStaleWatches();
			}
		}, settleDelay);
	}

	private synchronized void walkTreeAndSetWatches () {
		try {
			Files.walkFileTree(root, new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory (Path dir, BasicFileAttributes attrs) throws IOException {
					registerWatch(dir);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed (Path file, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory (Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			// Don't care
		}
	}

	private synchronized void unregisterStaleWatches () {
		Set<Path> paths = new HashSet<>(watchPathKeyMap.keySet());
		Set<Path> stalePaths = new HashSet<>();
		for (Path path : paths) {
			if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
				stalePaths.add(path);
			}
		}
		if (stalePaths.size() > 0) {
			for (Path stalePath : stalePaths) {
				unregisterWatch(stalePath);
			}
		}
	}

	private synchronized void registerWatch (Path dir) {
		if (!watchPathKeyMap.containsKey(dir)) {
			try {
				WatchKey watchKey = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
				watchPathKeyMap.put(dir, watchKey);
			} catch (IOException e) {
				// Don't care!
			}
		}
	}

	private synchronized void unregisterWatch (Path dir) {
		WatchKey watchKey = watchPathKeyMap.get(dir);
		if (watchKey != null) {
			watchKey.cancel();
			watchPathKeyMap.remove(dir);
		}
	}

	public void addListener (WatchListener listener) {
		listeners.add(listener);
	}

	public boolean removeListener (WatchListener listener) {
		return listeners.removeValue(listener, true);
	}

	public interface WatchListener {
		default void fileChanged (FileHandle file) {
		}

		default void fileDeleted (FileHandle file) {
		}

		default void fileCreated (FileHandle file) {
		}
	}
}
