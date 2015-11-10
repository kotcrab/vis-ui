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

package com.kotcrab.vis.editor.serializer;

import com.artemis.Component;
import com.artemis.utils.Bag;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.annotation.CallSuper;

/**
 * Base class used to make component serializers. Provides entity for serialization context. Entity must be set before performing serialization.
 * @author Kotcrab
 */
public abstract class EntityComponentSerializer<T> extends CompatibleFieldSerializer<T> {
	protected Bag<Component> components;

	public EntityComponentSerializer (Kryo kryo, Class type) {
		super(kryo, type);
	}

	public void setComponents (Bag<Component> components) {
		this.components = components;
	}

	protected <C extends Component> C getComponent (Class<C> clazz) {
		for (int i = 0; i < components.size(); i++) {
			Component c = components.get(i);
			if (c.getClass().equals(clazz))
				return (C) c;
		}

		return null;
	}

	@Override
	@CallSuper
	public T read (Kryo kryo, Input input, Class<T> type) {
		if (components != null) throw new IllegalStateException("Entity deserializing cannot depend on entity context");
		return null;
	}

	@Override
	@CallSuper
	public void write (Kryo kryo, Output output, T object) {
		if (components == null) throw new IllegalStateException("Entity context not set for serializer!");
	}

	protected T parentRead (Kryo kryo, Input input, Class<T> type) {
		return super.read(kryo, input, type);
	}

	protected void parentWrite (Kryo kryo, Output output, T object) {
		super.write(kryo, output, object);
	}

	@Override
	@CallSuper
	public T copy (Kryo kryo, T original) {
		if (components == null) throw new IllegalStateException("Entity context not set for serializer!");
		return null;
	}
}
