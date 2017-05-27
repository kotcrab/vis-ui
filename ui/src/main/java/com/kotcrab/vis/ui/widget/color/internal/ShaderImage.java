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
import com.kotcrab.vis.ui.widget.VisImage;

/**
 * Allow to render standard {@link VisImage} with shader. Shaders uniforms can be set in {@link #setShaderUniforms(ShaderProgram)}
 * @author Kotcrab
 */
public class ShaderImage extends VisImage {
	private ShaderProgram shader;

	public ShaderImage (ShaderProgram shader, Texture texture) {
		super(texture);
		this.shader = shader;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		ShaderProgram originalShader = batch.getShader();
		batch.setShader(shader);
		setShaderUniforms(shader);

		super.draw(batch, parentAlpha);

		batch.setShader(originalShader);
	}

	protected void setShaderUniforms (ShaderProgram shader) {

	}
}
