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

package com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.provider;

import com.artemis.Component;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.widget.VisTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/** @author Kotcrab */
public abstract class AutoTableFragmentProvider<A extends Annotation> {
	public static final int LABEL_WIDTH = 170;

	protected Class<? extends Component> componentClass;

	protected VisTable uiTable;

	protected ModuleInjector injector;
	protected EntityProperties properties;

	/** Called after injecting modules */
	public void init () {

	}

	public final void setObjects (Class<? extends Component> componentClass, VisTable uiTable, ModuleInjector injector, EntityProperties properties) {
		this.properties = properties;
		this.injector = injector;
		this.uiTable = uiTable;
		this.componentClass = componentClass;
	}

	public abstract Object getUIByField (Class<?> type, Field field);

	public abstract void createUI (A annotation, Field field, Class<?> fieldType) throws ReflectiveOperationException;

	public abstract void updateUIFromEntities (ImmutableArray<EntityProxy> proxies, Field field, Class<?> fieldType) throws ReflectiveOperationException;

	public abstract void setToEntities (Component component, Field field, Class<?> fieldType) throws ReflectiveOperationException;
}
