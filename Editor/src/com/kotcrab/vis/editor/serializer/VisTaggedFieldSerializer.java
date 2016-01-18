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

package com.kotcrab.vis.editor.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.runtime.util.annotation.VisTag;

import java.lang.reflect.Field;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

/**
 * Serializes objects using direct field assignment for fields that have been {@link VisTag tagged}. Fields without the {@link VisTag}
 * annotation are not serialized. New tagged fields can be added without invalidating previously serialized bytes. If any tagged
 * field is removed, previously serialized bytes are invalidated. Instead of removing fields, apply the {@link Deprecated}
 * annotation and they will still be deserialized but won't be serialized. If fields are public, bytecode generation will be used
 * instead of reflection.
 * <p>
 * For compatibility reason fields can be also tagged with {@link Tag} but for new classes should always use {@link VisTag}
 * @author Nathan Sweet <misc@n4te.com>
 * @author Kotcrab
 */
public class VisTaggedFieldSerializer<T> extends FieldSerializer<T> {
	private int[] tags;
	private int writeFieldCount;
	private boolean[] deprecated;

	private boolean ignoreMissingTags = false;
	private boolean logMissingTags = false;

	public VisTaggedFieldSerializer (Kryo kryo, Class type) {
		super(kryo, type);
	}

	@Override
	protected void initializeCachedFields () {
		CachedField[] fields = getFields();
		// Remove untagged fields.
		for (int i = 0, n = fields.length; i < n; i++) {
			Field field = fields[i].getField();
			if (field.getAnnotation(VisTag.class) == null && field.getAnnotation(Tag.class) == null) {
				if (TRACE) trace("kryo", "Ignoring field without tag: " + fields[i]);
				super.removeField(fields[i]);
			}
		}
		// Cache tag values.
		fields = getFields();
		tags = new int[fields.length];
		deprecated = new boolean[fields.length];
		writeFieldCount = fields.length;
		for (int i = 0, n = fields.length; i < n; i++) {
			Field field = fields[i].getField();

			if (field.getAnnotation(VisTag.class) != null)
				tags[i] = field.getAnnotation(VisTag.class).value();
			else
				tags[i] = field.getAnnotation(Tag.class).value();

			if (field.getAnnotation(Deprecated.class) != null) {
				deprecated[i] = true;
				writeFieldCount--;
			}
		}

		this.removedFields.clear();
	}

	@Override
	public void removeField (String fieldName) {
		super.removeField(fieldName);
		initializeCachedFields();
	}

	@Override
	public void removeField (CachedField field) {
		super.removeField(field);
		initializeCachedFields();
	}

	@Override
	public void write (Kryo kryo, Output output, T object) {
		CachedField[] fields = getFields();
		output.writeVarInt(writeFieldCount, true); // Can be used for null.
		for (int i = 0, n = fields.length; i < n; i++) {
			if (deprecated[i]) continue;
			output.writeVarInt(tags[i], true);
			fields[i].write(output, object);
		}
	}

	/** If true this serializer won't fail when there are tags that exists in deserialized data but not on object target */
	public void setIgnoreMissingTags (boolean ignoreMissingTags) {
		this.ignoreMissingTags = ignoreMissingTags;
	}

	public void setLogMissingTags (boolean logMissingTags) {
		this.logMissingTags = logMissingTags;
	}

	@Override
	public T read (Kryo kryo, Input input, Class<T> type) {
		T object = create(kryo, input, type);
		kryo.reference(object);
		int fieldCount = input.readVarInt(true);
		int[] tags = this.tags;
		CachedField[] fields = getFields();
		for (int i = 0, n = fieldCount; i < n; i++) {
			int tag = input.readVarInt(true);

			CachedField cachedField = null;
			for (int ii = 0, nn = tags.length; ii < nn; ii++) {
				if (tags[ii] == tag) {
					cachedField = fields[ii];
					break;
				}
			}
			if (cachedField == null) {
				if (ignoreMissingTags) {
					if (logMissingTags) {
						Log.warn("Ignoring missing field tag: " + tag + " (" + getType().getName() + ")");
					}

					continue;
				}
				throw new KryoException("Unknown field tag: " + tag + " (" + getType().getName() + ")");
			}
			cachedField.read(input, object);
		}
		return object;
	}

}
