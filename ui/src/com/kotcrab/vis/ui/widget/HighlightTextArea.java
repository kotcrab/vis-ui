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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.kotcrab.vis.ui.util.highlight.Highlight;
import com.kotcrab.vis.ui.util.highlight.Highlighter;

/**
 * @author Kotcrab
 * @since 1.1.2
 */
public class HighlightTextArea extends ScrollableTextArea {
	private Array<Highlight> highlights = new Array<Highlight>();
	private Array<Chunk> renderChunks = new Array<Chunk>();
	private boolean chunkUpdateScheduled = true;
	private Color defaultColor = Color.WHITE;

	private Highlighter highlighter;

	private float maxLineWidth = 0;

	public HighlightTextArea (String text) {
		super(text);
	}

	public void setHighlighter (Highlighter highlighter) {
		this.highlighter = highlighter;
		highlighterChanged();
	}

	public Highlighter getHighlighter () {
		return highlighter;
	}

	@Override
	void updateDisplayText () {
		super.updateDisplayText();
		highlighterChanged();
	}

	@Override
	protected void calculateOffsets () {
		super.calculateOffsets();
		if (chunkUpdateScheduled == false) return;
		chunkUpdateScheduled = false;
		highlights.sort();
		renderChunks.clear();

		String text = getText();

		Pool<GlyphLayout> layoutPool = Pools.get(GlyphLayout.class);
		GlyphLayout layout = layoutPool.obtain();
		boolean carryHighlight = false;
		for (int lineIdx = 0, highlightIdx = 0; lineIdx < linesBreak.size; lineIdx += 2) {
			int lineStart = linesBreak.items[lineIdx];
			int lineEnd = linesBreak.items[lineIdx + 1];
			int lineProgress = lineStart;
			float chunkOffset = 0;

			for (; highlightIdx < highlights.size; ) {
				Highlight highlight = highlights.get(highlightIdx);
				if (highlight.getStart() > lineEnd) {
					break;
				}

				if (highlight.getStart() == lineProgress || carryHighlight) {
					renderChunks.add(new Chunk(text.substring(lineProgress, Math.min(highlight.getEnd(), lineEnd)), highlight.getColor(), chunkOffset, lineIdx));
					lineProgress = Math.min(highlight.getEnd(), lineEnd);

					if (highlight.getEnd() > lineEnd) {
						carryHighlight = true;
					} else {
						carryHighlight = false;
						highlightIdx++;
					}
				} else {
					//protect against overlapping highlights
					while (highlight.getStart() < lineProgress) {
						highlightIdx++;
						highlight = highlights.get(highlightIdx);
					}
					renderChunks.add(new Chunk(text.substring(lineProgress, highlight.getStart()), defaultColor, chunkOffset, lineIdx));
					lineProgress = highlight.getStart();
				}

				Chunk chunk = renderChunks.peek();
				layout.setText(style.font, chunk.text);
				chunkOffset += layout.width;
				//current highlight needs to be applied to next line meaning that there is no other highlights that can be applied to currently parsed line
				if (carryHighlight) break;
			}

			if (lineProgress < lineEnd) {
				renderChunks.add(new Chunk(text.substring(lineProgress, lineEnd), defaultColor, chunkOffset, lineIdx));
			}
		}

		for (String line : text.split("\\n")) {
			layout.setText(style.font, line);
			maxLineWidth = Math.max(maxLineWidth, layout.width + 30);
		}

		layoutPool.free(layout);
	}

	@Override
	protected void drawText (Batch batch, BitmapFont font, float x, float y) {
		float offsetY = 0;
		for (int i = firstLineShowing * 2; i < (firstLineShowing + linesShowing) * 2 && i < linesBreak.size; i += 2) {
			for (Chunk chunk : renderChunks) {
				if (chunk.lineIndex == i) {
					font.setColor(chunk.color);
					font.draw(batch, chunk.text, x + chunk.offsetX, y + offsetY);
				}
			}

			offsetY -= font.getLineHeight();
		}
	}

	public void highlighterChanged () {
		if (highlights == null) return;
		highlights.clear();
		if (highlighter != null) highlighter.process(this, highlights);
		chunkUpdateScheduled = true;
	}

//	@Override
//	public float getWidth () {
//		return maxLineWidth;
//	}

	private static class Chunk {
		String text;
		Color color;
		float offsetX;
		int lineIndex;

		public Chunk (String text, Color color, float offsetX, int lineIndex) {
			this.text = text;
			this.color = color;
			this.offsetX = offsetX;
			this.lineIndex = lineIndex;
		}
	}
}
