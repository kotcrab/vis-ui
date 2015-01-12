/**
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

package pl.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Object2d extends SceneObject {
	public transient TextureRegion region;

	public String regionRelativePath;

	private float x, y;
	private float originX, originY;
	private float rotation;
	private float scaleX = 1, scaleY = 1;

	public Object2d (String regionRelativePath, TextureRegion region, float x, float y) {
		this.regionRelativePath = regionRelativePath;
		this.region = region;
		this.x = x;
		this.y = y;
	}

	public void draw (Batch batch) {
		batch.draw(region, x, y, originX, originY, region.getRegionWidth(), region.getRegionHeight(), scaleX, scaleY, rotation);
	}
}
