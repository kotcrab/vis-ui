package com.kotcrab.vis.editor.plugin;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.module.project.ExportModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.ui.scene.entityproperties.ContentItemProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.SpecificObjectTable;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.entity.Entity;

public abstract class ObjectSupport<ED extends EntityData, E extends Entity & EditorObject> {
	private int id = -1;

	public abstract Class<E> getObjectClass ();

	public abstract ED getEmptyData ();

	public abstract boolean isSupportedDirectory (String extension, String relativePath);

	public abstract ContentItemProperties getContentItemProperties ();

	public abstract Source createDropSource (DragAndDrop dragAndDrop, FileItem item);

	public abstract Serializer<E> getSerializer ();

	public abstract void export (ExportModule module, Array<EntityData> entities, E entity);

	public SpecificObjectTable getUIPropertyTable () {
		return null;
	}

	public boolean canAnalyze (FileHandle file, String relativePath) {
		return false;
	}

	public void assignId (int id) {
		if (this.id != -1) throw new IllegalStateException("Id was already assigned to this support!");
		this.id = id;
	}

	public int getId () {
		if (id == -1) throw new IllegalStateException("Id wasn't assigned yet for this support!");
		return id;
	}

}
