/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.util.texturepacker;

import com.badlogic.gdx.utils.Array;

/** @author Nathan Sweet */
@SuppressWarnings("all")
public class GridPacker implements TexturePacker.Packer {
	private TexturePacker.Settings settings;

	public GridPacker (TexturePacker.Settings settings) {
		this.settings = settings;
	}

	public Array<TexturePacker.Page> pack (Array<TexturePacker.Rect> inputRects) {
		// System.out.print("Packing");

		int cellWidth = 0, cellHeight = 0;
		for (int i = 0, nn = inputRects.size; i < nn; i++) {
			TexturePacker.Rect rect = inputRects.get(i);
			cellWidth = Math.max(cellWidth, rect.width);
			cellHeight = Math.max(cellHeight, rect.height);
		}
		cellWidth += settings.paddingX;
		cellHeight += settings.paddingY;

		inputRects.reverse();

		Array<TexturePacker.Page> pages = new Array();
		while (inputRects.size > 0) {
			TexturePacker.Page result = packPage(inputRects, cellWidth, cellHeight);
			pages.add(result);
		}
		return pages;
	}

	private TexturePacker.Page packPage (Array<TexturePacker.Rect> inputRects, int cellWidth, int cellHeight) {
		TexturePacker.Page page = new TexturePacker.Page();
		page.outputRects = new Array();

		int maxWidth = settings.maxWidth, maxHeight = settings.maxHeight;
		if (settings.edgePadding) {
			maxWidth -= settings.paddingX;
			maxHeight -= settings.paddingY;
		}
		int x = 0, y = 0;
		for (int i = inputRects.size - 1; i >= 0; i--) {
			if (x + cellWidth > maxWidth) {
				y += cellHeight;
				if (y > maxHeight - cellHeight) break;
				x = 0;
			}
			TexturePacker.Rect rect = inputRects.removeIndex(i);
			rect.x = x;
			rect.y = y;
			rect.width += settings.paddingX;
			rect.height += settings.paddingY;
			page.outputRects.add(rect);
			x += cellWidth;
			page.width = Math.max(page.width, x);
			page.height = Math.max(page.height, y + cellHeight);
		}

		// Flip so rows start at top.
		for (int i = page.outputRects.size - 1; i >= 0; i--) {
			TexturePacker.Rect rect = page.outputRects.get(i);
			rect.y = page.height - rect.y - rect.height;
		}
		return page;
	}
}
