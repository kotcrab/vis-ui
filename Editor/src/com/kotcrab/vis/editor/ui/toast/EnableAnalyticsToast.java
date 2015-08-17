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

package com.kotcrab.vis.editor.ui.toast;

import com.kotcrab.vis.editor.module.editor.ToastModule.ToastTable;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.ui.widget.LinkLabel;

/**
 * @author Kotcrab
 */
public class EnableAnalyticsToast extends ToastTable {
	private EnableAnalyticsToastListener listener;

	public EnableAnalyticsToast (EnableAnalyticsToastListener listener) {
		this.listener = listener;
		LinkLabel disagree = new LinkLabel("Disagree");
		LinkLabel agree = new LinkLabel("Agree");

		content.add("VisEditor sends anonymous usage statistics").row();
		content.add(TableBuilder.build(12, disagree, agree)).right();

		disagree.setListener(url -> {
			listener.disagreed();
			fadeOut();
		});

		agree.setListener(url -> {
			listener.agreed();
			fadeOut();
		});

		pack();
	}

	@Override
	protected void close () {
		listener.agreed();
		fadeOut();
	}

	public interface EnableAnalyticsToastListener {
		void agreed ();

		void disagreed ();
	}
}
