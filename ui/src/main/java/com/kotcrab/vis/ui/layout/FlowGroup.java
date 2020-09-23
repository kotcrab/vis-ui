package com.kotcrab.vis.ui.layout;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * Arranges actors to flow in a specified layout direction using up available space and, if sensible, expanding in that direction.
 * <br/>
 * For horizontal layout direction ({@code vertical=false}), attempts to expand to the desired width, creates new rows and expands vertically as necessary.
 * Children automatically overflow to the next row when necessary.
 * <br/>
 * For vertical layout direction ({@code vertical=true}), attempts to expand to the desired height, creates new columns and expands horizontally as necessary.
 * Children automatically overflow to the next column when necessary.
 * <br/>
 * <br/>
 * Can be embedded in a scroll pane. To ensure proper flowing (instead of expanding in the layout direction), scrolling in the layout direction should be disabled, i.e., horizontal layout direction should have scrolling in x direction disabled and vertical layout direction should have scrolling in y direction disabled.
 * <br/>
 * <br/>
 * This is a more versatile implementation of a flow group subsuming
 * {@link com.kotcrab.vis.ui.layout.HorizontalFlowGroup HorizontalFlowGroup} and {@link com.kotcrab.vis.ui.layout.VerticalFlowGroup VerticalFlowGroup}.
 * <br/>
 * <br/>
 * Key differences:
 * <ul>
 *   <li>Can be configured to have a layout direction as either horizontal ({@code vertical=false}) or vertical ({@code vertical=true}).</li>
 *   <li>Layout direction can be changed programmatically during runtime.</li>
 *   <li>If available, uses up all necessary space in the layout direction, i.e., when {@code vertical=false}, attempts to expand horizontally and, when {@code vertical=true}, attempts to expand vertically (instead of using the specified width/height as before).</li>
 *   <li>Adds spacing only between children, but not after the last element.</li>
 *   <li>When even the first child does not fit its row/column, space is no longer placed before it.</li>
 * </ul>
 * @author ccmb2r
 * @since 1.4.7
 */
public class FlowGroup extends WidgetGroup {
	private static final float DEFAULT_SPACING = 0;

	private boolean vertical;
	private float spacing;

	private boolean sizeInvalid = true;

	private float minWidth;
	private float minHeight;

	private float layoutedWidth;
	private float layoutedHeight;

	private float relaxedWidth;
	private float relaxedHeight;

	public FlowGroup (boolean vertical) {
		this(vertical, DEFAULT_SPACING);
	}

	public FlowGroup (boolean vertical, float spacing) {
		this.vertical = vertical;
		this.spacing = spacing;
		setTouchable(Touchable.childrenOnly);
	}

	public boolean isVertical () {
		return vertical;
	}

	public void setVertical (boolean vertical) {
		if (this.vertical == vertical) {
			return;
		}

		this.vertical = vertical;
		invalidate();
	}

	protected void computeSize () {
		if (vertical) {
			computeSizeVertical();
		} else {
			computeSizeHorizontal();
		}
	}

	protected void computeSizeHorizontal () {
		final float targetWidth = getWidth();

		float maxChildWidth = 0;
		float maxChildHeight = 0;

		float x = 0;
		float currentRowHeight = 0;
		float totalWidth = 0;
		float totalHeight = 0;

		SnapshotArray<Actor> children = getChildren();
		boolean wasChildProcessed = false;

		for (Actor child : children) {
			float childWidth;
			float childHeight;

			if (child instanceof Layout) {
				Layout layout = (Layout) child;

				childWidth = layout.getPrefWidth();
				childHeight = layout.getPrefHeight();
			} else {
				childWidth = child.getWidth();
				childHeight = child.getHeight();
			}

			//See if it fits this row but place at least one child in the first row!
			if (x + childWidth <= targetWidth || !wasChildProcessed) {
				//Fits into this row.
				currentRowHeight = Math.max(childHeight, currentRowHeight);
			} else {
				//Start new row.
				totalHeight += currentRowHeight + spacing;
				x = 0;
				currentRowHeight = childHeight;
			}

			float widthIncrement = childWidth + spacing;

			x += widthIncrement;
			totalWidth += widthIncrement;

			maxChildWidth = Math.max(maxChildWidth, childWidth);
			maxChildHeight = Math.max(maxChildHeight, childHeight);

			wasChildProcessed = true;
		}

		//Handle last column (if at least one column exists).
		if (wasChildProcessed) {
			//Remove the last spacing that was added excessively.
			totalWidth -= spacing;
		}

		//Handle last row (no final spacing).
		totalHeight += currentRowHeight;

		//Store results.
		minWidth = maxChildWidth;
		minHeight = maxChildHeight;

		layoutedWidth = targetWidth;
		layoutedHeight = totalHeight;

		relaxedWidth = totalWidth;

		sizeInvalid = false;
	}

	protected void computeSizeVertical () {
		final float targetHeight = getHeight();

		float maxChildWidth = 0;
		float maxChildHeight = 0;

		float y = targetHeight;
		float currentColumnWidth = 0;
		float totalWidth = 0;
		float totalHeight = 0;

		SnapshotArray<Actor> children = getChildren();
		boolean wasChildProcessed = false;

		for (Actor child : children) {
			float childWidth;
			float childHeight;

			if (child instanceof Layout) {
				Layout layout = (Layout) child;

				childWidth = layout.getPrefWidth();
				childHeight = layout.getPrefHeight();
			} else {
				childWidth = child.getWidth();
				childHeight = child.getHeight();
			}

			//See if it fits this column but place at least one child in the first column!
			if (y - childHeight >= 0 || !wasChildProcessed) {
				//Fits into this column.
				currentColumnWidth = Math.max(childWidth, currentColumnWidth);
			} else {
				//Start new column.
				totalWidth += currentColumnWidth + spacing;
				y = targetHeight;
				currentColumnWidth = childWidth;
			}

			float heightIncrement = childHeight + spacing;

			y -= heightIncrement;
			totalHeight += heightIncrement;

			maxChildWidth = Math.max(maxChildWidth, childWidth);
			maxChildHeight = Math.max(maxChildHeight, childHeight);

			wasChildProcessed = true;
		}

		//Handle last row (if at least one row exists).
		if (wasChildProcessed) {
			//Remove the last spacing that was added excessively.
			totalHeight -= spacing;
		}

		//Handle last column (no final spacing).
		totalWidth += currentColumnWidth;

		//Store results.
		minWidth = maxChildWidth;
		minHeight = maxChildHeight;

		layoutedWidth = totalWidth;
		layoutedHeight = targetHeight;

		relaxedHeight = totalHeight;

		sizeInvalid = false;
	}

	@Override
	public void layout () {
		if (vertical) {
			layoutVertical();
		} else {
			layoutHorizontal();
		}
	}

	protected void layoutHorizontal () {
		//NOTE: Should children invalidate/validate as per contract?

		computeSizeIfNeeded();

		final float targetWidth = getWidth();
		final float targetHeight = getHeight();

		float x = 0;
		float y = targetHeight;
		float rowHeight = 0;

		SnapshotArray<Actor> children = getChildren();
		boolean wasChildProcessed = false;

		//Layout the child; first upside down as total height is, as of yet, unknown
		//(due to the dynamic width used) and will be determined during this run.
		for (Actor child : children) {
			float childWidth;
			float childHeight;

			if (child instanceof Layout) {
				Layout layout = (Layout) child;

				childWidth = layout.getPrefWidth();
				childHeight = layout.getPrefHeight();

				//Need to update size.
				child.setSize(childWidth, childHeight);
			} else {
				childWidth = child.getWidth();
				childHeight = child.getHeight();

				//Child already at size.
			}

			//See if it fits this row but place at least one child in the first row!
			if (x + childWidth <= targetWidth || !wasChildProcessed) {
				//Fits into this row.
				rowHeight = Math.max(childHeight, rowHeight);
			} else {
				//Start new row.
				x = 0;
				y -= rowHeight + spacing;
				rowHeight = childHeight;
			}

			child.setPosition(x, y - childHeight);

			x += childWidth + spacing;

			wasChildProcessed = true;
		}

		//Did a best effort to fit into the specified size but it still did not work.
		//Let the ancestors know in hopes that they resize the group.
		if (getHeight() != layoutedHeight) {
			invalidateHierarchy();
		}
	}

	protected void layoutVertical () {
		//NOTE: Should children invalidate/validate as per contract?

		computeSizeIfNeeded();

		final float targetHeight = getHeight();

		float x = 0;
		float y = targetHeight;
		float columnWidth = 0;

		SnapshotArray<Actor> children = getChildren();
		boolean wasChildProcessed = false;

		//Layout the child; first upside down as total height is, as of yet, unknown
		//(due to the dynamic width used) and will be determined during this run.
		for (Actor child : children) {
			float childWidth;
			float childHeight;

			if (child instanceof Layout) {
				Layout layout = (Layout) child;

				childWidth = layout.getPrefWidth();
				childHeight = layout.getPrefHeight();

				//Need to update size.
				child.setSize(childWidth, childHeight);
			} else {
				childWidth = child.getWidth();
				childHeight = child.getHeight();

				//Child already at size.
			}

			//See if it fits this column but place at least one child in the first column!
			if (y - childHeight >= 0 || !wasChildProcessed) {
				//Fits into this column.
				columnWidth = Math.max(childWidth, columnWidth);
			} else {
				//Start new column.
				x += columnWidth + spacing;
				y = targetHeight;
				columnWidth = childWidth;
			}

			child.setPosition(x, y - childHeight);
			y -= childHeight + spacing;

			wasChildProcessed = true;
		}

		//Did a best effort to fit into the specified size but it still did not work.
		//Let the ancestors know in hopes that they resize the group.
		if (getWidth() != layoutedWidth) {
			invalidateHierarchy();
		}
	}

	public float getSpacing () {
		return spacing;
	}

	public void setSpacing (float spacing) {
		this.spacing = spacing;
		invalidateHierarchy();
	}

	@Override
	public void invalidate () {
		sizeInvalid = true;
		super.invalidate();
	}

	@Override
	public float getMinWidth () {
		computeSizeIfNeeded();
		return minWidth;
	}

	@Override
	public float getMinHeight () {
		computeSizeIfNeeded();
		return minHeight;
	}

	@Override
	public float getPrefWidth () {
		computeSizeIfNeeded();
		return vertical ? layoutedWidth : relaxedWidth;
	}

	@Override
	public float getPrefHeight () {
		computeSizeIfNeeded();
		return vertical ? relaxedHeight : layoutedHeight;
	}

	protected void computeSizeIfNeeded () {
		if (sizeInvalid) {
			computeSize();
		}
	}
}
