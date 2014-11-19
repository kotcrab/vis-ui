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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class PopupMenu extends Table {
	private PopupMenuStyle style;
	
	public PopupMenu () {
		style = VisUI.skin.get(PopupMenuStyle.class);
	}

	public void addItem (MenuItem item) {
		add(item).fillX().row();
		pack();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		super.draw(batch, parentAlpha);
		style.border.draw(batch, getX(), getY(), getWidth(), getHeight());
		
	}

	static public class PopupMenuStyle {
		public Drawable border;

		public PopupMenuStyle () {
		}

		public PopupMenuStyle (Drawable border) {
			this.border = border;
		}

		public PopupMenuStyle (PopupMenuStyle style) {
			this.border = style.border;
		}

//
// public VisTextFieldStyle () {
// }
//
// public VisTextFieldStyle (BitmapFont font, Color fontColor, Drawable cursor, Drawable selection, Drawable background) {
// super(font, fontColor, cursor, selection, background);
// }
//
// public VisTextFieldStyle (VisTextFieldStyle style) {
// super(style);
// this.focusBorder = style.focusBorder;
// this.errorBorder = style.errorBorder;
// this.backgroundOver = style.backgroundOver;
// }
	}
}
