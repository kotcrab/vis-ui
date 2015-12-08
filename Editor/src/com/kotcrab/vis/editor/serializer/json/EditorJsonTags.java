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

package com.kotcrab.vis.editor.serializer.json;

import com.kotcrab.vis.editor.entity.*;
import com.kotcrab.vis.editor.scene.EditorLayer;
import com.kotcrab.vis.runtime.util.json.JsonTagRegistrar;
import com.kotcrab.vis.runtime.util.json.RuntimeJsonTags;

/** @author Kotcrab */
public class EditorJsonTags {
	public static void registerTags (JsonTagRegistrar registrar) {
		RuntimeJsonTags.registerTags(registrar);

		registrar.register("EditorLayer", EditorLayer.class);
		registrar.register("EntityScheme", EntityScheme.class);

		registrar.register("VisUUID", VisUUID.class);
		registrar.register("PixelsPerUnit", PixelsPerUnit.class);
		registrar.register("ExporterDropsComponent", ExporterDropsComponent.class);
		registrar.register("SpriterProperties", SpriterProperties.class);
	}
}
