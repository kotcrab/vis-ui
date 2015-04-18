package com.kotcrab.vis.plugin.spine;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.module.project.ExportModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.runtime.plugin.VisPlugin;
import com.kotcrab.vis.editor.ui.scene.entityproperties.ContentItemProperties;
import com.kotcrab.vis.editor.util.gdx.VisDropSource;
import com.kotcrab.vis.plugin.spine.runtime.SpineData;
import com.kotcrab.vis.runtime.data.EntityData;

@VisPlugin
public class EditorSpineSupport extends ObjectSupport<SpineData, SpineObject> {
	@Override
	public Class<SpineObject> getObjectClass () {
		return SpineObject.class;
	}

	@Override
	public SpineData getEmptyData () {
		return new SpineData();
	}

	@Override
	public boolean isSupportedDirectory (String extension, String relativePath) {
		return relativePath.startsWith("spine/");
	}

	@Override
	public ContentItemProperties getContentItemProperties () {
		return new ContentItemProperties("Spine");
	}

	@Override
	public Source createDropSource (DragAndDrop dragAndDrop, FileItem item) {
		return new VisDropSource(dragAndDrop, item).defaultView("New Sound \n (drop on scene to add)").disposeOnNullTarget();
		//.setObjectProvider(() -> new SoundObject(fileAccess.relativizeToAssetsFolder(item.getFile()), Gdx.audio.newSound(item.getFile())));
	}

	@Override
	public Serializer<SpineObject> getSerializer () {
		return new SpineSerializer();
	}

	@Override
	public void export (ExportModule module, Array<EntityData> entities, SpineObject entity) {

	}

	@Override
	public boolean canAnalyze (FileHandle file, String relativePath) {
		return false;
	}
}
