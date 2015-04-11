package com.kotcrab.vis.editor.api.plugin;

import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.api.scene.EditorObject;
import com.kotcrab.vis.editor.api.ui.SpecificObjectTable;
import com.kotcrab.vis.runtime.api.data.EntityData;
import com.kotcrab.vis.runtime.api.entity.Entity;

public interface ObjectSupport<ED extends EntityData, E extends Entity & EditorObject> {
	Class<E> getObjectClass ();

	ED getData ();

	Serializer<E> getSerializer ();

	SpecificObjectTable getObjectTable();
}
