/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.ui.widget;

import pl.kotcrab.vis.ui.VisUI;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class VisWindow extends Window {
	public static float FADE_TIME = 0.3f;

	private Stage parent;

	public VisWindow (String title) {
		this(null, title, true);
	}

	public VisWindow (Stage parent, String title) {
		this(parent, title, true);
	}

	public VisWindow (Stage parent, String title, boolean showBorder) {
		super(title, VisUI.skin, showBorder ? "default" : "noborder");
		this.parent = parent;
		setTitleAlignment(Align.left);
	}

	@Override
	public void setPosition (float x, float y) {
		super.setPosition((int)x, (int)y);
	}

	public void setPositionToCenter () {
		if (parent != null) setPosition((parent.getWidth() - getWidth()) / 2, (parent.getHeight() - getHeight()) / 2);
	}

	public void fadeOut (float time) {
		addAction(Actions.sequence(Actions.fadeOut(time, Interpolation.fade), Actions.removeActor()));
	}

	/** @return this window for the purpose of chaining methods eg. stage.addActor(new MyWindow(stage).fadeIn()); */
	public VisWindow fadeIn (float time) {
		setColor(1, 1, 1, 0);
		addAction(Actions.sequence(Actions.fadeIn(time, Interpolation.fade)));
		return this;
	}

	public void fadeOut () {
		fadeOut(FADE_TIME);
	}

	public VisWindow fadeIn () {
		return fadeIn(FADE_TIME);
	}
}
