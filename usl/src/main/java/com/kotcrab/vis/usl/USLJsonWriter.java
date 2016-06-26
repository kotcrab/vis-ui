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

package com.kotcrab.vis.usl;

import com.kotcrab.vis.usl.lang.*;

import java.util.ArrayList;

/** Converts list of blocks created by {@link Parser} into json string */
public class USLJsonWriter {
	private ArrayList<StyleBlock> styleBlocks;

	private StringBuilder out;

	public USLJsonWriter (ArrayList<StyleBlock> mergedStyleBlocks) {
		out = new StringBuilder();
		styleBlocks = mergedStyleBlocks;
	}

	public String getJson () {
		out.append("{\n");

		for (int i = 0; i < styleBlocks.size(); i++) {
			StyleBlock block = styleBlocks.get(i);
			out.append(block.fullName).append(": {\n");

			ArrayList<StyleIdentifier> styles = block.styles;
			for (int j = 0; j < styles.size(); j++) {
				StyleIdentifier style = styles.get(j);
				if (style.metaStyle) continue;

				out.append("\t").append(style.name).append(": ");
				if (style.content.size() == 1 && style.content.get(0) instanceof AliasIdentifier) {
					writeIdentifiers(style.content);
				} else {
					out.append("{");
					writeIdentifiers(style.content);
					out.append(" }");
				}

				if (j == styles.size() - 1) {
					out.append("\n");
				} else {
					out.append(",\n");
				}
			}

			if (i == styleBlocks.size() - 1) {
				out.append("}\n");
			} else {
				out.append("},\n");
			}

		}

		out.append("\n}");
		return out.toString();
	}

	private void writeIdentifiers (ArrayList<Identifier> content) {
		for (int i = 0; i < content.size(); i++) {
			Identifier id = content.get(i);
			if (id instanceof BasicIdentifier) {
				BasicIdentifier bid = (BasicIdentifier) id;
				if (bid.content.equals("NULL")) continue;
				out.append(bid.name).append(": ").append(bid.content);
			} else if (id instanceof GroupIdentifier) {
				GroupIdentifier gid = (GroupIdentifier) id;
				out.append(gid.name).append(": {");
				writeIdentifiers(gid.content);
				out.append("}");
			} else if (id instanceof AliasIdentifier) {
				out.append(id.name);
			}

			if (i != content.size() - 1) {
				out.append(", ");
			}
		}
	}
}
