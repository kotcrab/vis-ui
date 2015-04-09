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
