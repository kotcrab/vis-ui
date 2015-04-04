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

package com.kotcrab.vis.editor.module.physicseditor;

import com.kotcrab.vis.editor.module.physicseditor.util.Clipper.Polygonizer;

/**
 * Contains all settings for physics editor
 * @author Kotcrab
 */
public class PhysicsEditorSettings {
	public boolean isImageDrawn = true;
	public boolean isShapeDrawn = true;
	public boolean isPolygonDrawn = true;
	public boolean isPhysicsDebugEnabled = false;
	public boolean isSnapToGridEnabled = false;
	public boolean isGridShown = false;
	public float gridGap = 0.10f;

	public Polygonizer polygonizer = Polygonizer.EWJORDAN;
	public float autoTraceHullTolerance = 2.5f;
	public int autoTraceAlphaTolerance = 128;
	public boolean autoTraceMultiPartDetection = false;
	public boolean autoTraceHoleDetection = false;
}
