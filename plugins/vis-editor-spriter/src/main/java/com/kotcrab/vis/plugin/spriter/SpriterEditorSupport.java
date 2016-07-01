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

package com.kotcrab.vis.plugin.spriter;

import com.artemis.Entity;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.kotcrab.vis.editor.entity.ExporterDropsComponent;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.ContentItemProperties;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.plugin.api.EditorEntitySupport;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.scene2d.VisDragAndDrop;
import com.kotcrab.vis.editor.util.scene2d.VisDropSource;
import com.kotcrab.vis.editor.util.vis.SortedEntityEngineConfiguration;
import com.kotcrab.vis.plugin.spriter.assets.SpriterAssetType;
import com.kotcrab.vis.plugin.spriter.component.SpriterProperties;
import com.kotcrab.vis.plugin.spriter.module.SpriterCacheModule;
import com.kotcrab.vis.plugin.spriter.module.SpriterDataIOModule;
import com.kotcrab.vis.plugin.spriter.proxy.SpriterProxy;
import com.kotcrab.vis.plugin.spriter.runtime.assets.SpriterAsset;
import com.kotcrab.vis.plugin.spriter.runtime.component.VisSpriter;
import com.kotcrab.vis.plugin.spriter.runtime.system.SpriterRenderSystem;
import com.kotcrab.vis.plugin.spriter.system.EditorSpriterInflater;
import com.kotcrab.vis.plugin.spriter.system.SpriterReloaderManager;
import com.kotcrab.vis.plugin.spriter.util.SpriterAssetData;
import com.kotcrab.vis.plugin.spriter.util.SpriterProjectPathUtils;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.Layer;
import com.kotcrab.vis.runtime.component.Renderable;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;
import com.kotcrab.vis.runtime.util.EntityEngine;

import static com.kotcrab.vis.runtime.scene.SceneConfig.Priority.VIS_INFLATER;
import static com.kotcrab.vis.runtime.scene.SceneConfig.Priority.VIS_RENDERER;

/** @author Kotcrab */
public class SpriterEditorSupport implements EditorEntitySupport {
	private AssetsMetadataModule assetsMetadata;

	private SpriterCacheModule spriterCache;
	private SpriterDataIOModule spriterDataIO;
	private FileAccessModule fileAccess;

	@Override
	public void registerSystems (SortedEntityEngineConfiguration config) {
		RenderBatchingSystem renderBatchingSystem = config.getSystem(RenderBatchingSystem.class);

		config.setSystem(new SpriterRenderSystem(renderBatchingSystem), VIS_RENDERER);
		config.setSystem(new EditorSpriterInflater(), VIS_INFLATER);
		config.setSystem(new SpriterReloaderManager(), VIS_RENDERER);
	}

	@Override
	public boolean isSupportedDirectory (FileHandle file, String relativePath) {
		return SpriterProjectPathUtils.isImportedSpriterAnimationDir(assetsMetadata, file);
	}

	@Override
	public ContentItemProperties getContentItemProperties (FileHandle file, String relativePath, String extension) {
		if (SpriterProjectPathUtils.isImportedSpriterAnimationDir(assetsMetadata, file)) {
			return new ContentItemProperties(SpriterAssetType.SPRITER_SCML, "Spriter Animation", true);
		}

		return null;
	}

	@Override
	public Source createDropSource (VisDragAndDrop dragAndDrop, FileItem item) {
		String relativePath = fileAccess.relativizeToAssetsFolder(item.getFile());
		FileHandle dataFile = item.getFile().parent().child(".vis").child("data.json");
		if (dataFile.exists() == false) return null;
		SpriterAssetData data = spriterDataIO.loadData(dataFile);
		return new VisDropSource(dragAndDrop, item).defaultView("New Spriter Animation \n (drop on scene to add)").setPayload(new SpriterAsset(relativePath, data.imageScale));
	}

	@Override
	public Entity processDropPayload (EntityEngine entityEngine, EditorScene scene, Object payload) {

		if (payload instanceof SpriterAsset) {
			SpriterAsset asset = (SpriterAsset) payload;

			float scale = 1f / scene.pixelsPerUnit;

			return new EntityBuilder(entityEngine)
					.with(spriterCache.createComponent(asset, scale), new SpriterProperties(scale), new Transform(),
							new AssetReference(asset),
							new Renderable(0), new Layer(scene.getActiveLayerId()),
							new ExporterDropsComponent(SpriterProperties.class))
					.build();

		}

		return null;
	}

	@Override
	public EntityProxy resolveProxy (Entity entity) {
		if (entity.getComponent(VisSpriter.class) != null) return new SpriterProxy(entity);
		return null;
	}
}
