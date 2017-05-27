/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Used to check whether file system root is readable or writeable.
 * @author Kotcrab
 */
public class DriveCheckerService {
	private static DriveCheckerService instance;

	private final ExecutorService pool;

	private Array<File> readableRoots = new Array<File>();
	private Array<File> writableRoots = new Array<File>();

	private Map<File, ListenerSet> readableListeners = new HashMap<File, ListenerSet>();
	private Map<File, ListenerSet> writableListeners = new HashMap<File, ListenerSet>();

	public static synchronized DriveCheckerService getInstance () {
		if (instance == null) instance = new DriveCheckerService();
		return instance;
	}

	public DriveCheckerService () {
		pool = Executors.newFixedThreadPool(3, new ServiceThreadFactory("DriveStatusChecker"));

		File[] roots = File.listRoots();

		for (File root : roots) {
			processRoot(root);
		}
	}

	private void processRoot (final File root) {
		pool.execute(new Runnable() {
			@Override
			public void run () {
				processResults(root, root.canRead(), root.canWrite());
			}
		});
	}

	private void processResults (final File root, final boolean readable, final boolean writable) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				if (readable) {
					readableRoots.add(root);
					ListenerSet set = readableListeners.get(root);
					if (set != null) {
						set.notifyListeners(root, RootMode.READABLE);
					}
				}

				if (writable) {
					writableRoots.add(root);
					ListenerSet set = writableListeners.get(root);
					if (set != null) {
						set.notifyListeners(root, RootMode.WRITABLE);
					}
				}
			}
		});
	}

	public void addListener (File root, RootMode mode, DriveCheckerListener listener) {
		switch (mode) {
			case READABLE:
				addListener(root, mode, listener, readableRoots, readableListeners);
				break;
			case WRITABLE:
				addListener(root, mode, listener, writableRoots, writableListeners);
				break;
		}
	}

	private void addListener (File root, RootMode mode, DriveCheckerListener listener, Array<File> cachedRoots, Map<File, ListenerSet> listeners) {
		if (cachedRoots.contains(root, false)) {
			listener.rootMode(root, mode);
			return;
		}

		ListenerSet set = listeners.get(root);

		if (set == null) {
			set = new ListenerSet();
			listeners.put(root, set);
		}

		set.add(listener);
		processRoot(root);
	}

	public enum RootMode {
		READABLE, WRITABLE
	}

	public interface DriveCheckerListener {
		void rootMode (File root, RootMode mode);
	}

	public class ListenerSet {
		Array<DriveCheckerListener> list = new Array<DriveCheckerListener>();

		public void add (DriveCheckerListener listener) {
			list.add(listener);
		}

		public void notifyListeners (File root, RootMode mode) {
			Iterator<DriveCheckerListener> it = list.iterator();

			while (it.hasNext()) {
				DriveCheckerListener listener = it.next();
				listener.rootMode(root, mode);
				it.remove();
			}
		}
	}
}
