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

package com.kotcrab.vis.runtime.util;

import com.kotcrab.vis.runtime.component.ProtoComponent;

/**
 * Component implementing this interface uses {@link ProtoComponent} for serialization. Such components must use
 * InflaterSystem that will transform ProtoComponent into actual component. This is often used for components that cannot
 * be serialized directly.
 * @author Kotcrab
 */
public interface UsesProtoComponent {
	ProtoComponent getProtoComponent ();
}
