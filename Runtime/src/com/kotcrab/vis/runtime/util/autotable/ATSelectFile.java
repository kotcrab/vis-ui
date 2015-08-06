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

package com.kotcrab.vis.runtime.util.autotable;

import com.artemis.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used on fields inside {@link Component}s to make them editable inside VisEditor. Target component
 * class must be registered using AutoComponentTable. See VisEditor source for more details.
 * <p>
 * Auto table will automatically create label with current file path and button to chose new file. This annotation
 * requires providing handler class that will take care of updating entities.
 * @author Kotcrab
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ATSelectFile {
	/** @return human friendly field name that will be used in VisEditor UI. */
	String fieldName () default "";

	/** @return relative folder path that will be used for file selector. For example: "gfx/". */
	String relativeFolderPath ();

	/** @return extension that file chooser will display. For example: "png". */
	String extension ();

	/** @return if true file selector will hide file extensions in dialog. */
	boolean hideExtension () default false;

	/** @return class name that will be used as handler. Must implement {@link ATSelectFile}. */
	String handlerClass ();
}
