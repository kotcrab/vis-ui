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

package com.kotcrab.vis.editor.ui.scene.entityproperties.components.handler;

import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.ATSelectFileHandlerGroup;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFileHandler;

/** @author Kotcrab */
public class VisATSelectFileHandlerGroup implements ATSelectFileHandlerGroup {
	//if this class is ever to be moved don't forget to update ATSelectFile and ATExtendedSelectFile default handler

	private ObjectMap<String, ATSelectFileHandler> handlers = new ObjectMap<>();
	private ObjectMap<String, ATExtSelectFileHandler> extHandlers = new ObjectMap<>();

	@Override
	public void setInjector (ModuleInjector injector) {
		handlers.put("shader", new ShaderATSelectFileHandler());
		handlers.put("music", new MusicATSelectFileHandler());
		handlers.put("sound", new SoundATSelectFileHandler());
		handlers.put("font", new FontATSelectFileHandler());

		extHandlers.put("font", new FontATExtSelectFileHandler());

		for (ATSelectFileHandler handler : handlers.values()) {
			injector.injectModules(handler);
		}

		for (ATExtSelectFileHandler handler : extHandlers.values()) {
			injector.injectModules(handler);
		}
	}

	@Override
	public ATSelectFileHandler getByAlias (String alias) {
		return handlers.get(alias);
	}

	@Override
	public ATExtSelectFileHandler getExtByAlias (String alias) {
		return extHandlers.get(alias);
	}
}
