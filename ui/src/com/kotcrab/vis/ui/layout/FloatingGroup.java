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

package com.kotcrab.vis.ui.layout;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.SnapshotArray;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Does not arranges actors in specific layout, instead it uses their position and preferred sizes to place them inside
 * group. Does not check position values and allows widgets to be placed outside group. If combined with
 * {@link VisWindow#setKeepWithinParent(boolean)} allows to create windows that can only moved within this group area.
 * @author Kotcrab
 * @since 1.0.2
 */
public class FloatingGroup extends WidgetGroup {
	private boolean useChildrenPreferredSize = false;
	private float prefWidth = 0f;
	private float prefHeight = 0f;

	/**
	 * Creates floating group without preferred sizes set. Group size should be controlled by parent, note that
	 * most group relies on correctness of preferred width and height so not all groups can be applied.
	 * <p>
	 * This can be useful for adding group to {@link Table}. For example: add(floatingGroup).grow() to fill all available
	 * space.
	 */
	public FloatingGroup () {
		setTouchable(Touchable.childrenOnly);
	}

	/**
	 * Creates floating group with fixed area size.
	 * @param prefHeight preferred height of group. If set to to lower than 0 then {@link #getHeight()} is used as preferred height.
	 * @param prefWidth preferred width of group. If set to to lower than 0 then {@link #getWidth()} is used as preferred width.
	 */
	public FloatingGroup (float prefWidth, float prefHeight) {
		setTouchable(Touchable.childrenOnly);
		setPrefWidth(prefWidth);
		setPrefHeight(prefHeight);
	}

	@Override
	public void layout () {
		if (useChildrenPreferredSize == false) return;
		SnapshotArray<Actor> children = getChildren();

		for (int i = 0; i < children.size; i++) {
			Actor child = children.get(i);
			float width = child.getWidth();
			float height = child.getHeight();
			if (child instanceof Layout) {
				Layout layout = (Layout) child;
				width = layout.getPrefWidth();
				height = layout.getPrefHeight();
			}

			child.setBounds(child.getX(), child.getY(), width, height);
		}
	}

	public boolean isUseChildrenPreferredSize () {
		return useChildrenPreferredSize;
	}

	/**
	 * If true then children preferred size will be used whenever possible when doing group layout. If set to true
	 * changing widget and height using {@link Actor#getWidth()} and {@link Actor#getHeight()} may not have effect
	 * for instances of {@link Layout} (depending on implementation). Default is false.
	 */
	public void setUseChildrenPreferredSize (boolean useChildrenPreferredSize) {
		this.useChildrenPreferredSize = useChildrenPreferredSize;
		invalidate();
	}

	@Override
	public float getPrefWidth () {
		return prefWidth < 0f ? getWidth() : prefWidth;
	}

	@Override
	public float getPrefHeight () {
		return prefHeight < 0f ? getHeight() : prefHeight;
	}

	public void setPrefWidth (float prefWidth) {
		this.prefWidth = prefWidth;
		invalidate();
	}

	public void setPrefHeight (float prefHeight) {
		this.prefHeight = prefHeight;
		invalidate();
	}
}
