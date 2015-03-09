/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

//https://github.com/syncany/syncany/blob/59cf87c72de4322c737f0073ce8a7ddd992fd898/syncany-lib/src/main/java/org/syncany/operations/watch/RecursiveWatcher.java

/**
 * The recursive file watcher monitors a folder (and its sub-folders).
 * <p/>
 * <p/>
 * The class walks through the file tree and registers to a watch to every sub-folder. For new folders, a new watch is registered,
 * and stale watches are removed.
 * <p/>
 * <p/>
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
		listeners = new Array<WatchListener>();
		running = new AtomicBoolean(false);
		watchService = null;
		watchThread = null;
		watchPathKeyMap = new HashMap<Path, WatchKey>();
		timer = null;

		if (listener != null) listeners.add(listener);
	}

	/**
	 * Starts the watcher service and registers watches in all of the sub-folders of the given root folder.
	 * <p/>
	 * <p/>
	 * <b>Important:</b> This method returns immediately, even though the watches might not be in place yet. For large file trees,
	 * it might take several seconds until all directories are being monitored. For normal cases (1-100 folders), this should not
	 * take longer than a few milliseconds.
	 */
	public void start () {
		try {
			watchService = FileSystems.getDefault().newWatchService();
			watchThread = new Thread(new Runnable() {
				@Override
				public void run () {
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
									Gdx.app.postRunnable(new Runnable() {
										@Override
										public void run () {
											for (WatchListener listener : listeners)
												listener.fileChanged(fileHandle);
										}
									});
								}

								if (ev.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
									Gdx.app.postRunnable(new Runnable() {
										@Override
										public void run () {
											for (WatchListener listener : listeners)
												listener.fileDeleted(fileHandle);
										}
									});
								}

								if (ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
									Gdx.app.postRunnable(new Runnable() {
										@Override
										public void run () {
											for (WatchListener listener : listeners)
												listener.fileCreated(fileHandle);
										}
									});
								}
							}

							watchKey.reset();
							resetWaitSettlementTimer();
						} catch (InterruptedException | ClosedWatchServiceException e) {
							running.set(false);
						}
					}
				}
			}, "Watcher");
			watchThread.start();
		} catch (IOException e) {

		}
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
		Set<Path> paths = new HashSet<Path>(watchPathKeyMap.keySet());
		Set<Path> stalePaths = new HashSet<Path>();
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
		public void fileChanged (FileHandle file);

		public void fileDeleted (FileHandle file);

		public void fileCreated (FileHandle file);
	}
}
