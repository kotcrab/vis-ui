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

package com.kotcrab.vis.editor.module.physicseditor.util;

import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;
import com.kotcrab.vis.editor.module.physicseditor.models.ShapeModel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ShapeUtils {
	public static ShapeModel getShape (RigidBodyModel model, Vector2 v) {
		for (ShapeModel shape : model.getShapes()) {
			if (shape.getVertices().contains(v)) return shape;
		}

		return null;
	}
}
