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

package com.kotcrab.vis.editor.webapi;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.editor.JenkinsClientModule.JenkinsJobURLProvider;
import com.kotcrab.vis.runtime.util.PrettyEnum;

public class VersionSet {
	Array<Release> versions;

	public enum EditorReleaseType implements PrettyEnum, JenkinsJobURLProvider {
		STABLE {
			@Override
			public String toPrettyString () {
				return "Stable";
			}

			@Override
			public String getJenkinsJobURL () {
				return "http://kotcrab.com:8080/job/viseditor-stable/lastSuccessfulBuild/api/xml";
			}
		},
		BETA {
			@Override
			public String toPrettyString () {
				return "Beta";
			}

			@Override
			public String getJenkinsJobURL () {
				return "http://kotcrab.com:8080/job/viseditor-beta/lastSuccessfulBuild/api/xml";
			}
		},
		EDGE {
			@Override
			public String toPrettyString () {
				return "Cutting Edge";
			}

			@Override
			public String getJenkinsJobURL () {
				return "http://kotcrab.com:8080/job/viseditor-edge/lastSuccessfulBuild/api/xml";
			}
		}
	}

	public static class Release {
		String title;
		String path;
		int versionCode;
		EditorReleaseType type;
	}
}
