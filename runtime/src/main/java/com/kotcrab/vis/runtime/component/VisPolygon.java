/*
 * Copyright 2014-2016 See AUTHORS file.
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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.util.autotable.ATVector2Array;

/**
 * Stores polygon data for creating Box2d Body. Keep in mind that after physics simulation is running, values of this
 * component won't be updated. Changing values after body creation won't have any effect. Values are stored in world
 * positions.
 * @author Kotcrab
 */
public class VisPolygon extends Component {
	@ATVector2Array(fieldName = "Vertices")
	public Array<Vector2> vertices = new Array<Vector2>();
	public Vector2[][] faces;
}
