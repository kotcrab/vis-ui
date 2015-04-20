package com.kotcrab.vis.plugin.spine;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.module.project.ExportModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.editor.ui.scene.entityproperties.ContentItemProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.SpecificObjectTable;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.gdx.VisDropSource;
import com.kotcrab.vis.plugin.spine.runtime.SpineData;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

@VisPlugin
public class SpineEditorSupport extends ObjectSupport<SpineData, SpineObject> {
	private SpineCacheModule spineCache;
	private FileAccessModule fileAccess;

	private SpineSerializer serializer;

	private SpineObjectTable spineTable;

	@Override
	public void bindModules (ProjectModuleContainer projectMC) {
		SceneIOModule sceneIOModule = projectMC.get(SceneIOModule.class);
		spineCache = projectMC.get(SpineCacheModule.class);
		fileAccess = projectMC.get(FileAccessModule.class);

		serializer = new SpineSerializer(sceneIOModule.getKryo(), spineCache);
		spineTable = new SpineObjectTable();
	}

	@Override
	public SpecificObjectTable getUIPropertyTable () {
		return spineTable;
	}

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
	public ContentItemProperties getContentItemProperties (String relativePath, String ext) {
		if (ext.equals("json"))
			return new ContentItemProperties("Spine Json Skeleton", true);

		if (ext.equals("skel"))
			return new ContentItemProperties("Spine Binary Skeleton", true);

		return null;
	}

	@Override
	public Source createDropSource (DragAndDrop dragAndDrop, FileItem item) {
		return new VisDropSource(dragAndDrop, item).defaultView("New Spine Animation \n (drop on scene to add)").disposeOnNullTarget()
				.setObjectProvider(() -> {
					FileHandle atlasFile = FileUtils.sibling(item.getFile(), "atlas");
					return new SpineObject(fileAccess.relativizeToAssetsFolder(atlasFile.path()), fileAccess.relativizeToAssetsFolder(item.getFile().path()),
							spineCache.get(atlasFile, item.getFile()));
				});
	}

	@Override
	public Serializer<SpineObject> getSerializer () {
		return serializer;
	}

	@Override
	public void export (ExportModule module, Array<EntityData> entities, SpineObject entity) {

	}

}
