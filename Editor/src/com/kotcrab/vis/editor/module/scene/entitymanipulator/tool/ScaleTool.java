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

package com.kotcrab.vis.editor.module.scene.entitymanipulator.tool;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;

/** @author Kotcrab */
public class ScaleTool extends BaseGizmoTool {
	public static final String TOOL_ID = App.PACKAGE + ".tools.ScaleTool";

	private Stage stage;

	@Override
	public void activated () {
		super.activated();
		DialogUtils.showOKDialog(stage, "Warning", "Scale tool is not ready yet in this snapshot");
	}

	@Override
	public void render (ShapeRenderer shapeRenderer) {
		super.render(shapeRenderer);

		if (totalSelectionBounds != null) {
			float centerX = totalSelectionBounds.x + totalSelectionBounds.width / 2;
			float centerY = totalSelectionBounds.y + totalSelectionBounds.height / 2;

//			shapeRenderer.begin(ShapeType.Filled);
//			shapeRenderer.rect(centerX, centerY, 0.1f, 0.1f);
//			shapeRenderer.end();
		}
	}

	@Override
	public String getToolId () {
		return TOOL_ID;
	}
}
