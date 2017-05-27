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

package com.kotcrab.vis.ui.widget.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.FileFilter;

/**
 * FileTypeFilter is used to limit {@link FileChooser} selection only to specified set of extensions. User can use file
 * chooser's select box to select that extension or all types (if it is allowed).
 * <p>
 * This class is not replacement for {@link FileChooser#setFileFilter(FileFilter)}. While the main file chooser
 * filter does general filtering (such as removing hidden or inaccessible files), FileTypeFilter is used to limit extensions
 * of files than user can select.
 * <p>
 * This filter works by adding rules. Each rule has a description (showed in file chooser's filter select box) and a
 * list of extensions that it accepts. During selection user can switch active rule via select box. Additionally each
 * FileTypeFilter can support 'all types allowed' where all files are accepted regardless of their extension.
 * @author Kotcrab
 * @since 1.1.0
 */
public class FileTypeFilter {
	private boolean allTypesAllowed;
	private Array<Rule> rules = new Array<Rule>();

	public FileTypeFilter (FileTypeFilter other) {
		this.allTypesAllowed = other.allTypesAllowed;
		this.rules = new Array<Rule>(other.rules);
	}

	/** @param allTypesAllowed if true then user can choose "All types" in file chooser's filter select box. In that mode all files are shown */
	public FileTypeFilter (boolean allTypesAllowed) {
		this.allTypesAllowed = allTypesAllowed;
	}

	/**
	 * Adds new rule to {@link FileTypeFilter}
	 * @param description rule description used in FileChooser's file type select box
	 * @param extensions list of extensions without leading dot, eg. 'jpg', 'png' etc.
	 */
	public void addRule (String description, String... extensions) {
		rules.add(new Rule(description, extensions));
	}

	public Array<Rule> getRules () {
		return rules;
	}

	/**
	 * Controls whether to allow 'all types allowed' mode, where all file types are shown.
	 * @param allTypesAllowed if true then user can choose "All types" in file chooser's filter select box where all files are shown
	 */
	public void setAllTypesAllowed (boolean allTypesAllowed) {
		this.allTypesAllowed = allTypesAllowed;
	}

	public boolean isAllTypesAllowed () {
		return allTypesAllowed;
	}

	/** Defines single rule for {@link FileTypeFilter}. Rule instances are immutable. */
	public static class Rule {
		private final String description;
		private final Array<String> extensions = new Array<String>();
		private final boolean allowAll;

		public Rule (String description) {
			if (description == null) throw new IllegalArgumentException("description can't be null");
			this.description = description;
			this.allowAll = true;
		}

		public Rule (String description, String... extensionList) {
			if (description == null) throw new IllegalArgumentException("description can't be null");
			if (extensionList == null || extensionList.length == 0)
				throw new IllegalArgumentException("extensionList can't be null nor empty");
			this.description = description;
			this.allowAll = false;
			for (String ext : extensionList) {
				if (ext.startsWith(".")) ext = ext.substring(1);
				extensions.add(ext.toLowerCase());
			}
		}

		public boolean accept (FileHandle file) {
			if (allowAll) return true;
			String ext = file.extension().toLowerCase();
			return extensions.contains(ext, false);
		}

		public String getDescription () {
			return description;
		}

		/** @return copy of extension list. */
		public Array<String> getExtensions () {
			return new Array<String>(extensions);
		}

		@Override
		public String toString () {
			return description;
		}
	}
}
