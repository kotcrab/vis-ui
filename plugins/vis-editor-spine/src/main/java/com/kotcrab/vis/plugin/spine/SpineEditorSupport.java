/*
 * Spine Runtimes Software License
 * Version 2.3
 *
 * Copyright (c) 2013-2015, Esoteric Software
 * All rights reserved.
 *
 * You are granted a perpetual, non-exclusive, non-sublicensable and
 * non-transferable license to use, install, execute and perform the Spine
 * Runtimes Software (the "Software") and derivative works solely for personal
 * or internal use. Without the written permission of Esoteric Software (see
 * Section 2 of the Spine Software License Agreement), you may not (a) modify,
 * translate, adapt or otherwise create derivative works, improvements of the
 * Software or develop new applications using the Software or (b) remove,
 * delete, alter or obscure any trademarks or any copyright, trademark, patent
 * or other intellectual property or proprietary rights notices on or in the
 * Software, including any copy thereof. Redistributions in binary or source
 * form must include this license and terms.
 *
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.kotcrab.vis.plugin.spine;

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
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.scene2d.VisDragAndDrop;
import com.kotcrab.vis.editor.util.scene2d.VisDropSource;
import com.kotcrab.vis.editor.util.vis.SortedEntityEngineConfiguration;
import com.kotcrab.vis.plugin.spine.components.SpineBounds;
import com.kotcrab.vis.plugin.spine.components.SpinePreview;
import com.kotcrab.vis.plugin.spine.components.SpineScale;
import com.kotcrab.vis.plugin.spine.runtime.SpineAssetDescriptor;
import com.kotcrab.vis.plugin.spine.runtime.VisSpine;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.scene.SceneConfig.Priority;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;
import com.kotcrab.vis.runtime.util.EntityEngine;

/** @author Kotcrab */
public class SpineEditorSupport implements EditorEntitySupport {
	private AssetsMetadataModule assetsMetadata;

	private SpineCacheModule spineCache;
	private FileAccessModule fileAccess;

	@Override
	public void registerSystems (SortedEntityEngineConfiguration config) {
		RenderBatchingSystem renderBatchingSystem = config.getSystem(RenderBatchingSystem.class);
		config.setSystem(new SpineEditorRenderSystem(renderBatchingSystem), Priority.VIS_RENDERER);

		config.setSystem(new EditorSpineInflaterSystem(), Priority.VIS_INFLATER);

		config.setSystem(new SpinePreviewUpdaterSystem(), Priority.NORMAL);
		config.setSystem(new SpineScaleUpdaterSystem(), Priority.NORMAL);
	}

	@Override
	public boolean isSupportedDirectory (FileHandle file, String relativePath) {
		return assetsMetadata.isDirectoryMarkedAs(file, SpineAssetType.DIRECTORY_SPINE);
	}

	@Override
	public ContentItemProperties getContentItemProperties (FileHandle file, String relativePath, String extension) {
		if (extension.equals("json"))
			return new ContentItemProperties(SpineAssetType.JSON_SKELETON, "Spine Json Skeleton", true);

		if (extension.equals("skel"))
			return new ContentItemProperties(SpineAssetType.BINARY_SKELETON, "Spine Binary Skeleton", true);

		return null;
	}

	@Override
	public Source createDropSource (VisDragAndDrop dragAndDrop, FileItem item) {
		FileHandle atlasFile = FileUtils.sibling(item.getFile(), "atlas");
		FileHandle skeletonFile = item.getFile();

		String atlasPath = fileAccess.relativizeToAssetsFolder(atlasFile);
		String skeletonPath = fileAccess.relativizeToAssetsFolder(skeletonFile);

		SpineAssetDescriptor asset = new SpineAssetDescriptor(atlasPath, skeletonPath, 1);

		return new VisDropSource(dragAndDrop, item).defaultView("New Spine Animation \n (drop on scene to add)").setPayload(asset);
	}

	@Override
	public Entity processDropPayload (EntityEngine engine, EditorScene scene, Object payload) {

		if (payload instanceof SpineAssetDescriptor) {
			SpineAssetDescriptor asset = (SpineAssetDescriptor) payload;

			return new EntityBuilder(engine)
					.with(new VisSpine(spineCache.get(asset)), new SpinePreview(), new SpineScale(1f / scene.pixelsPerUnit), new SpineBounds(),
							new AssetReference(asset), new Transform(), new Tint(),
							new Renderable(0), new Layer(scene.getActiveLayerId()),
							new ExporterDropsComponent(SpinePreview.class, SpineScale.class, SpineBounds.class))
					.build();
		}

		return null;
	}

	@Override
	public EntityProxy resolveProxy (Entity entity) {
		if (entity.getComponent(VisSpine.class) != null) return new SpineProxy(entity);

		return null;
	}
}
