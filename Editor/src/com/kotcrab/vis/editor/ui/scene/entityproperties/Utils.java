/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.ui.IndeterminateCheckbox;
import com.kotcrab.vis.ui.VisUI;

class Utils {
	static String floatToString (float d) {
		//fk this function
		if (d == (long) d) //if does not have decimal places
			return String.format("%d", (long) d);
		else {
			//round to two decimal places
			d = Math.round(d * 100);
			d = d / 100;
			String s = String.valueOf(d);

			//remove trailing zeros if exists
			return s.contains(".") ? s.replaceAll("0*$", "").replaceAll("\\.$", "") : s;
		}
	}

	static String getEntitiesFieldFloatValue (Array<EditorObject> entities, FloatValue objValue) {
		float value = objValue.getFloat(entities.first());

		for (EditorObject entity : entities)
			if (value != objValue.getFloat(entity)) return "?";

		return floatToString(value);
	}

	static void setCheckBoxState (Array<EditorObject> entities, IndeterminateCheckbox target, BooleanValue value) {
		boolean enabled = value.getBoolean(entities.first());

		for (EditorObject entity : entities) {
			if (enabled != value.getBoolean(entity)) {
				target.setIndeterminate(true);
				return;
			}
		}

		target.setChecked(enabled);
	}

	static String getCommonString (Array<EditorObject> entities, String ifNotCommon, StringValue value) {
		String firstText = value.getString(entities.first());

		for (EditorObject entity : entities) {
			if (value.getString(entity).equals(firstText) == false) return ifNotCommon;
		}

		return firstText;
	}

	static String getEntitiesId (Array<EditorObject> entities) {
		String firstId = entities.first().getId();
		if (firstId == null) firstId = "";

		for (EditorObject entity : entities) {
			String entityId = entity.getId();
			if (entityId == null) entityId = "";

			if (firstId.equals(entityId) == false) {
				return "<?>";
			}
		}

		return firstId;
	}

	static boolean isScaleSupportedForEntities (Array<EditorObject> entities) {
		for (EditorObject entity : entities) {
			if (entity.isScaleSupported() == false) return false;
		}

		return true;
	}

	static boolean isOriginSupportedForEntities (Array<EditorObject> entities) {
		for (EditorObject entity : entities) {
			if (entity.isOriginSupported() == false) return false;
		}

		return true;
	}

	static boolean isRotationSupportedForEntities (Array<EditorObject> entities) {
		for (EditorObject entity : entities) {
			if (entity.isRotationSupported() == false) return false;
		}

		return true;
	}

	static boolean isTintSupportedForEntities (Array<EditorObject> entities) {
		for (EditorObject entity : entities) {
			if (entity.isTintSupported() == false) return false;
		}

		return true;
	}

	static boolean isFlipSupportedForEntities (Array<EditorObject> entities) {
		for (EditorObject entity : entities) {
			if (entity.isFlipSupported() == false) return false;
		}

		return true;
	}

	static class TintImage extends Image {
		private final Drawable alphaBar = Assets.getMisc("alpha-grid-20x20");
		private final Drawable white = VisUI.getSkin().getDrawable("white");
		private final Drawable questionMark = Assets.getIcon(Icons.QUESTION);

		private boolean unknown;

		public TintImage () {
			super();
			setDrawable(white);
		}

		@Override
		public void draw (Batch batch, float parentAlpha) {
			batch.setColor(1, 1, 1, parentAlpha);

			if (unknown)
				questionMark.draw(batch, getX() + getImageX(), getY() + getImageY(), getImageWidth() * getScaleX(), getImageHeight() * getScaleY());
			else {
				alphaBar.draw(batch, getX() + getImageX(), getY() + getImageY(), getImageWidth() * getScaleX(), getImageHeight() * getScaleY());
				super.draw(batch, parentAlpha);
			}
		}

		public void setUnknown (boolean unknown) {
			this.unknown = unknown;
		}

		@Override
		public void setColor (Color color) {
			super.setColor(color);
		}
	}
}
