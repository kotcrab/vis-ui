package com.kotcrab.vis.editor.plugin;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.ui.scene.entityproperties.ContentItemProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.SpecificObjectTable;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.entity.Entity;

public interface ObjectSupport<ED extends EntityData, E extends Entity & EditorObject> {
	Class<E> getObjectClass ();

	ED getEmptyData ();

	boolean isSupportedDirecotry(String extension, String relativePath);

	ContentItemProperties getContentItemProperties ();

	Source createSource(DragAndDrop dragAndDrop, FileHandle file);

	Serializer<E> getSerializer ();

	SpecificObjectTable getObjectTable();
}
