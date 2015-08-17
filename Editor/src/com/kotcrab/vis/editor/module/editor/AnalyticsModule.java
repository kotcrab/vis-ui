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

import com.brsanthu.googleanalytics.AppViewHit;
import com.brsanthu.googleanalytics.ExceptionHit;
import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.ExceptionEvent;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.ui.toast.EnableAnalyticsToast;
import com.kotcrab.vis.editor.ui.toast.EnableAnalyticsToast.EnableAnalyticsToastListener;
import org.apache.commons.codec.binary.Base64;

/** @author Kotcrab */
public class AnalyticsModule extends EditorModule {
	public static final String ID = "VUEtNDMwODg5MjAtNQ==";

	@InjectModule ToastModule toastModule;
	@InjectModule GeneralSettingsModule generalSettings;

	private GoogleAnalytics analytics;

	@Override
	public void postInit () {
		if (generalSettings.getAnalyticsState() == AnalyticsState.SHOW_QUESTION) {
			toastModule.show(new EnableAnalyticsToast(new EnableAnalyticsToastListener() {
				@Override
				public void agreed () {
					generalSettings.setAnalyticsState(AnalyticsState.ENABLED);
					postAppStarted();
				}

				@Override
				public void disagreed () {
					generalSettings.setAnalyticsState(AnalyticsState.DISABLED);
				}
			}));
		} else {
			postAppStarted();
		}

		App.eventBus.register(this);
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
		if (analytics != null) analytics.close();
	}

	private void postAppStarted () {
		if (prepare()) {
			analytics.postAsync(new AppViewHit("VisEditor", App.VERSION, "Startup"));
		}
	}

	@Subscribe
	public void handleExceptionEvent (ExceptionEvent event) {
		if (prepare()) {
			analytics.postAsync(new ExceptionHit(event.throwable.getMessage(), true));
		}
	}

	private boolean prepare () {
		if (isAnalyticsEnabled() == false) return false;

		if (analytics == null) {
			analytics = new GoogleAnalytics(new String(Base64.decodeBase64(ID.getBytes())));
		}

		return true;
	}

	private boolean isAnalyticsEnabled () {
		return generalSettings.getAnalyticsState() == AnalyticsState.ENABLED;
	}

	public enum AnalyticsState {
		SHOW_QUESTION, ENABLED, DISABLED
	}
}
