package com.kotcrab.vis.launcher.ui;

import com.kotcrab.vis.launcher.api.APIClient.SetCallback;
import com.kotcrab.vis.launcher.api.NewsSet;
import com.kotcrab.vis.launcher.api.NewsSet.News;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class NewsSection extends VisTable implements SetCallback<NewsSet> {
	private VisTable newsTable;
	private boolean newsLoaded;

	public NewsSection () {
		super(true);

		newsTable = new VisTable(true);
		newsTable.add(new VisLabel("Loading..."));
		newsTable.top().left();
		newsTable.defaults().left();

		add("News").left().row();
		add(newsTable).expand().fill();
	}

	@Override
	public void failed (Throwable cause) {
		if (newsLoaded == false) {
			newsTable.clear();
			newsTable.add("Error occurred while loading news!");
		}
	}

	@Override
	public void reload (NewsSet set) {
		newsLoaded = true;

		newsTable.clear();
		for (News news : set.news) {
			VisLabel label = new VisLabel(news.title + "\n" + news.text);
			label.setWrap(true);
			newsTable.add(label).expandX().fillX().spaceBottom(3).row();
			newsTable.add(new LinkLabel("Read More", news.more)).right().row();
		}
	}
}
