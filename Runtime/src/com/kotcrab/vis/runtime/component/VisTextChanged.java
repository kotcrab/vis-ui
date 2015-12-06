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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;

/**
 * If entity has this component then it means it's {@link VisText} cached values are outdated and must
 * be updated. If only transform has changed adding this component to entity is enough. If tint has changed or actual
 * text content has changed you must set {@link #contentChanged} to true.
 * @author Kotcrab
 */
public class VisTextChanged extends Component {
	public boolean contentChanged = false;
	public boolean persistent = false;

	@Deprecated
	public VisTextChanged () {
	}
}
