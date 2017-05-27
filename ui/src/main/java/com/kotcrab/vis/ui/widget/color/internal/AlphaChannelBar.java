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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Channel bar intended for alpha channel, renders alpha grid below channel texture.
 * @author Kotcrab
 */
public class AlphaChannelBar extends ChannelBar {
	private GridSubImage gridImage;

	public AlphaChannelBar (PickerCommons commons, int mode, int maxValue, ChangeListener changeListener) {
		super(commons, mode, maxValue, changeListener);
		gridImage = new GridSubImage(commons.gridShader, commons.whiteTexture, 6 * commons.sizes.scaleFactor);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		gridImage.draw(batch, this);
		super.draw(batch, parentAlpha);
	}
}
