/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.widget.color.internal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** @author Kotcrab */
public class GridSubImage {
	private ShaderProgram gridShader;
	private Texture whiteTexture;
	private float gridSize;

	public GridSubImage (ShaderProgram gridShader, Texture whiteTexture, float gridSize) {
		this.gridShader = gridShader;
		this.whiteTexture = whiteTexture;
		this.gridSize = gridSize;
	}

	public void draw (Batch batch, Image parent) {
		ShaderProgram originalShader = batch.getShader();
		batch.setShader(gridShader);
		gridShader.setUniformf("u_width", parent.getWidth());
		gridShader.setUniformf("u_height", parent.getHeight());
		gridShader.setUniformf("u_gridSize", gridSize);
		batch.draw(whiteTexture, parent.getX() + parent.getImageX(), parent.getY() + parent.getImageY(),
				parent.getImageWidth() * parent.getScaleX(), parent.getImageHeight() * parent.getScaleY());
		batch.setShader(originalShader);
	}
}
