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

				out.append("\t").append(style.name).append(": {");
				writeIdentifiers(style.content);
				out.append(" }");

				if (j == styles.size() - 1)
					out.append("\n");
				else
					out.append(",\n");
			}

			if (i == styleBlocks.size() - 1)
				out.append("}\n");
			else
				out.append("},\n");

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
			}

			if (id instanceof GroupIdentifier) {
				GroupIdentifier gid = (GroupIdentifier) id;
				out.append(gid.name).append(": {");
				writeIdentifiers(gid.content);
				out.append("}");
			}

			if (i != content.size() - 1)
				out.append(", ");
		}
	}
}
