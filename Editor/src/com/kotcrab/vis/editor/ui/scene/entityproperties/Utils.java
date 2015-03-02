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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.scene.EditorEntity;
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

	static boolean isScaleSupportedForEntities (Array<EditorEntity> entities) {
		for (EditorEntity entity : entities) {
			if (entity.isScaleSupported() == false) return false;
		}

		return true;
	}

	static boolean isOriginSupportedForEntities (Array<EditorEntity> entities) {
		for (EditorEntity entity : entities) {
			if (entity.isOriginSupported() == false) return false;
		}

		return true;
	}

	static boolean isRotationSupportedForEntities (Array<EditorEntity> entities) {
		for (EditorEntity entity : entities) {
			if (entity.isRotationSupported() == false) return false;
		}

		return true;
	}

	static boolean isTintSupportedForEntities (Array<EditorEntity> entities) {
		for (EditorEntity entity : entities) {
			if (entity.isTintSupported() == false) return false;
		}

		return true;
	}

	static boolean isFlipSupportedForEntities (Array<EditorEntity> entities) {
		for (EditorEntity entity : entities) {
			if (entity.isFlipSupported() == false) return false;
		}

		return true;
	}

	static String getEntitiesFieldValue (Array<EditorEntity> entities, EntityValue objValue) {
		float value = objValue.getValue(entities.first());

		for (EditorEntity entity : entities)
			if (value != objValue.getValue(entity)) return "?";

		return floatToString(value);
	}

	static String getEntitiesId (Array<EditorEntity> entities) {
		String firstId = entities.first().getId();
		if (firstId == null) firstId = "";

		for (EditorEntity entity : entities) {
			String entityId = entity.getId();
			if (entityId == null) entityId = "";

			if (firstId.equals(entityId) == false) {
				return "<?>";
			}
		}

		return firstId;
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
