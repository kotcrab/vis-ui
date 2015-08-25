/*
 * Copyright 2014-2015 See AUTHORS file.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import sun.awt.shell.ShellFolder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.util.OsUtils;

public class FileChooserWinService {
	private static FileChooserWinService instance;

	private ObjectMap<File, String> nameCache = new ObjectMap<File, String>();

	private Map<File, ListenerSet> listeners = new HashMap<File, ListenerSet>();

	public static synchronized FileChooserWinService getInstance () {
		if (OsUtils.isWindows() == false) return null;

		if (instance == null) instance = new FileChooserWinService();

		return instance;
	}

	protected FileChooserWinService () {
		ExecutorService pool = Executors.newFixedThreadPool(3, new ThreadFactory() {
			final AtomicLong count = new AtomicLong(0);

			@Override
			public Thread newThread (Runnable runnable) {
				Thread thread = Executors.defaultThreadFactory().newThread(runnable);
				thread.setName("SystemDisplayNameGetter-" + count.getAndIncrement());
				thread.setDaemon(true);
				return thread;
			}
		});

		File[] roots = File.listRoots();

		for (final File file : roots) {
			pool.execute(new Runnable() {
				@Override
				public void run () {
					processResult(file, getSystemDisplayName(file));
				}
			});
		}
	}

	private void processResult (final File root, final String name) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				if (name != null)
					nameCache.put(root, name);
				else
					nameCache.put(root, root.toString());

				ListenerSet set = listeners.get(root);
				if (set != null) set.notifyListeners(name);
			}
		});
	}

	public void addListener (File root, RootNameListener listener) {
		String cachedName = nameCache.get(root);
		if (cachedName != null) {
			listener.setRootName(cachedName);
			return;
		}

		ListenerSet set = listeners.get(root);

		if (set == null) {
			set = new ListenerSet();
			listeners.put(root, set);
		}

		set.add(listener);
	}

	private String getSystemDisplayName (File f) {
		String name;

		try {
			name = ShellFolder.getShellFolder(f).getDisplayName();
		} catch (FileNotFoundException e) {
			return null;
		}

		if (name == null || name.length() == 0) {
			name = f.getPath(); // the "/" directory
		}

		return name;
	}

	public interface RootNameListener {
		void setRootName (String newName);
	}

	private static class ListenerSet {
		List<WeakReference<RootNameListener>> list = new ArrayList<WeakReference<RootNameListener>>();

		public void add (RootNameListener listener) {
			list.add(new WeakReference<RootNameListener>(listener));
		}

		public void notifyListeners (String newName) {
			Iterator<WeakReference<RootNameListener>> it = list.iterator();

			while (it.hasNext()) {
				RootNameListener listener = it.next().get();

				if (listener == null) {
					it.remove();
					continue;
				}

				listener.setRootName(newName);
			}
		}
	}
}
