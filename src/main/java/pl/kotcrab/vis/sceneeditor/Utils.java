/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

class Utils {
	public static Rectangle buildRectangeForScaleArea (ObjectRepresentation orep) {
		Rectangle rect = orep.getBoundingRectangle();
		return new Rectangle(rect.x + rect.width - 15, rect.y + rect.height - 15, 15, 15);
	}

	public static Circle buildCirlcleForRotateArea (ObjectRepresentation orep) {
		Rectangle rect = orep.getBoundingRectangle();

		int cWidth = 7;

		return new Circle(rect.x + rect.width / 2 + cWidth, rect.y + rect.height + cWidth, cWidth);
	}
}
