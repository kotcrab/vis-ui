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

package com.kotcrab.vis.ui;

/**
 * VisUI padding, spacings and sizes. Loaded from skin, will differ between different skin sizes. If you are using
 * custom skin it must contain "default" definition of Sizes values.
 * @author Kotcrab
 * @since 0.8.1
 */
public class Sizes {
	public float scaleFactor;

	public float spacingTop;
	public float spacingBottom;
	public float spacingRight;
	public float spacingLeft;

	public float buttonBarSpacing;

	public float menuItemIconSize;

	/**
	 * Size of focus border. 1 for standard Vis skin. This is used to avoid showing overlapping borders when two widgets
	 * have borders (for example button can have it's own focus border which without this padding would overlap with menu border)
	 */
	public float borderSize;

	public float spinnerButtonHeight;
	public float spinnerFieldSize;

	public float fileChooserViewModeBigIconsSize;
	public float fileChooserViewModeMediumIconsSize;
	public float fileChooserViewModeSmallIconsSize;
	public float fileChooserViewModeListWidthSize;

	public Sizes () {
	}

	public Sizes (Sizes other) {
		this.scaleFactor = other.scaleFactor;
		this.spacingTop = other.spacingTop;
		this.spacingBottom = other.spacingBottom;
		this.spacingRight = other.spacingRight;
		this.spacingLeft = other.spacingLeft;
		this.buttonBarSpacing = other.buttonBarSpacing;
		this.menuItemIconSize = other.menuItemIconSize;
		this.borderSize = other.borderSize;
		this.spinnerButtonHeight = other.spinnerButtonHeight;
		this.spinnerFieldSize = other.spinnerFieldSize;
		this.fileChooserViewModeBigIconsSize = other.fileChooserViewModeBigIconsSize;
		this.fileChooserViewModeMediumIconsSize = other.fileChooserViewModeMediumIconsSize;
		this.fileChooserViewModeSmallIconsSize = other.fileChooserViewModeSmallIconsSize;
		this.fileChooserViewModeListWidthSize = other.fileChooserViewModeListWidthSize;
	}
}

