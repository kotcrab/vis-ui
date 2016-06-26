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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.util.OsUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Used to get system drive name. Only used on Windows.
 * @author Kotcrab
 */
public class FileChooserWinService {
	private static FileChooserWinService instance;

	private ObjectMap<File, String> nameCache = new ObjectMap<File, String>();

	private Map<File, ListenerSet> listeners = new HashMap<File, ListenerSet>();
	private final ExecutorService pool;

	private boolean shellFolderSupported = false;
	private Method getShellFolderMethod;
	private Method getShellFolderDisplayNameMethod;

	public static synchronized FileChooserWinService getInstance () {
		if (OsUtils.isWindows() == false) return null;
		if (instance == null) instance = new FileChooserWinService();
		return instance;
	}

	@SuppressWarnings("unchecked")
	protected FileChooserWinService () {
		pool = Executors.newFixedThreadPool(3, new ServiceThreadFactory("SystemDisplayNameGetter"));

		try {
			Class shellFolderClass = Class.forName("sun.awt.shell.ShellFolder");
			getShellFolderMethod = shellFolderClass.getMethod("getShellFolder", File.class);
			getShellFolderDisplayNameMethod = shellFolderClass.getMethod("getDisplayName");
			shellFolderSupported = true;
		} catch (ClassNotFoundException ignored) { //ShellFolder not supported on current JVM, ignoring
		} catch (NoSuchMethodException ignored) {
		}

		File[] roots = File.listRoots();

		for (File root : roots) {
			processRoot(root);
		}
	}

	private void processRoot (final File root) {
		pool.execute(new Runnable() {
			@Override
			public void run () {
				processResult(root, getSystemDisplayName(root));
			}
		});
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
		processRoot(root);
	}

	private String getSystemDisplayName (File f) {
		if (shellFolderSupported == false) return null;
		String name;

		try {
			//name = ShellFolder.getShellFolder(f).getDisplayName();
			Object shellFolder = getShellFolderMethod.invoke(null, f);
			name = (String) getShellFolderDisplayNameMethod.invoke(shellFolder);
		} catch (InvocationTargetException e) {
			return null;
		} catch (IllegalAccessException e) {
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
		Array<WeakReference<RootNameListener>> list = new Array<WeakReference<RootNameListener>>();

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
