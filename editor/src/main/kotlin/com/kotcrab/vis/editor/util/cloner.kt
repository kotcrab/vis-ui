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

@file:Suppress("UNCHECKED_CAST")

package com.kotcrab.vis.editor.util

import com.rits.cloning.IFastCloner

fun <T> createFastClonerProvider(clazz: Class<T>, cloner: (T) -> T): BiHolder<Class<*>, IFastCloner> {
    return BiHolder.of(clazz, IFastCloner { obj, iDeepCloner, mutableMap -> cloner(obj as T) })
}

fun <T> createFastCloner(cloner: (T) -> T): IFastCloner {
    return IFastCloner { obj, iDeepCloner, mutableMap -> cloner(obj as T) }
}
