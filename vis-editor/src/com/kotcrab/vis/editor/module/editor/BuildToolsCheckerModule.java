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

package com.kotcrab.vis.editor.module.editor;

import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.runtime.util.PrettyEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/** @author Kotcrab */
public class BuildToolsCheckerModule extends EditorModule {
	public void isToolsInstalled (Consumer<Set<BuildTool>> missingTools) {
		new Thread(() -> {
			EnumSet<BuildTool> missingToolsSet = EnumSet.noneOf(BuildTool.class);

			if (isToolInstalled("git", "--version", output -> output.startsWith("git version")) == false) {
				missingToolsSet.add(BuildTool.GIT);
			}

			if (isMavenInstalled() == false) {
				missingToolsSet.add(BuildTool.MAVEN);
			}

			missingTools.accept(missingToolsSet);
		}).start();
	}

	private boolean isToolInstalled (String command, String args, Function<String, Boolean> checker) {
		ProcessBuilder builder = new ProcessBuilder(command, args);
		builder.redirectErrorStream(true);
		try {
			Process process = builder.start();
			process.waitFor();

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			return checker.apply(input.readLine());
		} catch (IOException | InterruptedException e) {
			Log.exception(e);
			return false;
		}
	}

	private boolean isMavenInstalled () {
		return System.getenv("M2_HOME") != null;
	}

	public enum BuildTool implements PrettyEnum {
		GIT {
			@Override
			public String toPrettyString () {
				return "Git";
			}
		},
		MAVEN {
			@Override
			public String toPrettyString () {
				return "Maven";
			}
		}
	}
}
