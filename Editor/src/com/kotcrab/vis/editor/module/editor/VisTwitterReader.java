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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.serializer.ArraySerializer;
import com.kotcrab.vis.editor.ui.tab.StartPageTab;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.util.URLUtils;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * VisEditor twitter timeline reader. This module provides UI widget wich is displayed in {@link StartPageTab}
 * @author Kotcrab
 */
public class VisTwitterReader extends EditorModule {
	private static final String URL = "https://twitter.com/viseditor";

	private VisTable containerTable;

	private Cell statusesCell;
	private Kryo kryo;
	private TwitterCache twitterCache = null;
	private FileHandle twitterCacheFile;
	private VisTable statusesTable;
	private VisScrollPane scrollPane;

	@Override
	public void init () {
		kryo = new Kryo();
		kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
		kryo.register(Array.class, new ArraySerializer());

		FileHandle apiCache = Gdx.files.absolute(App.CACHE_FOLDER_PATH).child("twitter");
		apiCache.mkdirs();
		twitterCacheFile = apiCache.child("viseditor.data");

		if (twitterCacheFile.exists()) readCache();

		containerTable = new VisTable(false);

		statusesTable = new VisTable();
		statusesTable.left().top();

		scrollPane = new VisScrollPane(statusesTable);
		scrollPane.setFlickScroll(false);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollingDisabled(true, false);

		containerTable.add("@VisEditor");
		containerTable.add().expandX().fillX();
		containerTable.add(new LinkLabel("Open in browser", URL)).row();
		containerTable.addSeparator().colspan(3).spaceBottom(4);
		containerTable.row();
		statusesCell = containerTable.add(new VisLabel("Loading...", Align.center)).colspan(3).expand().fill();

		if (twitterCache == null || twitterCache.isOutdated())
			updateCache();
		else {
			Log.debug("Twitter cache is up to date");
			buildTwitterTable(twitterCache);
		}
	}

	private void updateCache () {
		new Thread(() -> {
			try {
				Log.debug("Reloading Twitter cache");
				Document doc = Jsoup.connect(URL).get();
				Elements elements = doc.select("p.tweet-text");

				TwitterCache cache = new TwitterCache();

				for (Element el : elements)
					cache.tweets.add(el.text().replace("…", ""));

				buildTwitterTable(cache);
			} catch (IOException e) {
				Log.exception(e);
				buildTwitterTable(null);
			}

		}, "VisTwitterReader").start();
	}

	private void buildTwitterTable (TwitterCache cache) {
		Gdx.app.postRunnable(() -> {
			this.twitterCache = cache;

			if (cache == null) {
				statusesCell.setActor(new VisLabel("Error occurred when loading tweets", Align.center));
				return;
			}

			for (String tweet : cache.tweets) {
				VisLabel label = new VisLabel(tweet, Align.left);
				label.setWrap(true);
				statusesTable.add(label).expandX().fillX().spaceBottom(4).padRight(4).row();

				String url = URLUtils.getFirstUrl(tweet);
				if (url != null)
					statusesTable.add(new LinkLabel("Open Link", url)).padRight(4).right().row();

				statusesTable.addSeparator().spaceBottom(4);
			}

			statusesCell.setActor(scrollPane);

			saveCache();
		});
	}

	private void readCache () {
		try {
			Input input = new Input(new FileInputStream(twitterCacheFile.file()));
			twitterCache = kryo.readObject(input, TwitterCache.class);
			input.close();
		} catch (KryoException | IOException ignored) {
			Log.warn("Error while reading Twitter cache, will be rebuild");
		}
	}

	private void saveCache () {
		try {
			Output output = new Output(new FileOutputStream(twitterCacheFile.file()));
			kryo.writeObject(output, twitterCache);
			output.close();
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}
	}

	public VisTable getTable () {
		return containerTable;
	}

	public static class TwitterCache {
		public LocalDateTime date = LocalDateTime.now();
		public Array<String> tweets = new Array<>();

		public boolean isOutdated () {
			return ChronoUnit.HOURS.between(date, LocalDateTime.now()) > 1;
		}
	}
}
