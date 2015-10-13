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
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.assets.AssetDescriptorProvider;
import com.kotcrab.vis.editor.entity.ExporterDropsComponent;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.ContentItemProperties;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable.SpecificUITable;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.scene2d.VisDragAndDrop;
import com.kotcrab.vis.editor.util.scene2d.VisDropSource;
import com.kotcrab.vis.plugin.spine.runtime.SpineAssetDescriptor;
import com.kotcrab.vis.plugin.spine.runtime.SpineComponent;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.LayerComponent;
import com.kotcrab.vis.runtime.component.RenderableComponent;
import com.kotcrab.vis.runtime.plugin.VisPlugin;
import com.kotcrab.vis.runtime.system.RenderBatchingSystem;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.kotcrab.vis.runtime.util.EntityEngineConfiguration;

@VisPlugin
public class SpineEditorSupport extends EditorEntitySupport {
	private SpineCacheModule spineCache;
	private FileAccessModule fileAccess;

	private Array<Serializer> serializers = new Array<>();
	private Array<Class<?>> serializedTypes = new Array<>();
	private Array<AssetDescriptorProvider> assetProviders = new Array<>();

	private float pixelsPerUnit;

	@Override
	public void bindModules (ProjectModuleContainer projectMC) {
		SceneIOModule sceneIOModule = projectMC.get(SceneIOModule.class);
		spineCache = projectMC.get(SpineCacheModule.class);
		fileAccess = projectMC.get(FileAccessModule.class);

		serializers.add(new SpineComponentSerializer(sceneIOModule.getKryo(), spineCache));
		assetProviders.add(new SpineAssetDescriptorProvider());

		serializedTypes.add(SpinePreviewComponent.class);
		serializedTypes.add(SpineScaleComponent.class);
		serializedTypes.add(SpineBoundsComponent.class);
		serializedTypes.add(SpineAssetDescriptor.class);
	}

	@Override
	public void registerSystems (SceneModuleContainer sceneMC, EntityEngineConfiguration config) {
		RenderBatchingSystem renderBatchingSystem = config.getSystem(RenderBatchingSystem.class);
		config.setSystem(new SpineEditorRenderSystem(renderBatchingSystem), true);

		config.setSystem(new SpinePreviewUpdaterSystem());
		config.setSystem(new SpineScaleUpdaterSystem());

		pixelsPerUnit = sceneMC.getScene().pixelsPerUnit;
	}

	@Override
	public Array<SpecificUITable> getUIPropertyTables () {
		Array<SpecificUITable> array = new Array<>(1);
		array.add(new SpineUITable());
		return array;
	}

	@Override
	public boolean isSupportedDirectory (String relativePath, String extension) {
		return relativePath.startsWith("spine/");
	}

	@Override
	public Array<AssetDescriptorProvider> getAssetDescriptorProviders () {
		return assetProviders;
	}

	@Override
	public ContentItemProperties getContentItemProperties (String relativePath, String extension) {
		if (extension.equals("json"))
			return new ContentItemProperties("Spine Json Skeleton", true);

		if (extension.equals("skel"))
			return new ContentItemProperties("Spine Binary Skeleton", true);

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
					.with(new SpineComponent(spineCache.get(asset)), new SpinePreviewComponent(), new SpineScaleComponent(1f / pixelsPerUnit), new SpineBoundsComponent(),
							new AssetComponent(asset),
							new RenderableComponent(0), new LayerComponent(scene.getActiveLayerId()),
							new ExporterDropsComponent(SpinePreviewComponent.class, SpineScaleComponent.class, SpineBoundsComponent.class))
					.build();
		}

		return null;
	}

	@Override
	public EntityProxy resolveProxy (Entity entity) {
		if (entity.getComponent(SpineComponent.class) != null) return new SpineProxy(entity);

		return null;
	}

	@Override
	public Array<Serializer> getSerializers () {
		return serializers;
	}

	@Override
	public Array<Class<?>> getSerializedTypes () {
		return serializedTypes;
	}
}
