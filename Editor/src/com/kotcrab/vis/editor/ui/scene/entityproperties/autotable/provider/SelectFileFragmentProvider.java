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

package com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.provider;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.dialog.SelectFileDialog;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.ATSelectFileHandlerGroup;
import com.kotcrab.vis.editor.util.Holder;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFile;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFileHandler;
import com.kotcrab.vis.ui.util.value.VisWidgetValue;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static com.kotcrab.vis.editor.util.vis.EntityUtils.getCommonString;

/** @author Kotcrab */
public class SelectFileFragmentProvider extends AutoTableFragmentProvider<ATSelectFile> {
	private static final int MAX_FILE_LABEL_WIDTH = 175;

	private FileAccessModule fileAccessModule;
	private AssetsMetadataModule assetsMetadata;
	private Stage stage;

	private ObjectMap<String, ATSelectFileHandlerGroup> handlerGroups = new ObjectMap<>();

	private ObjectMap<Field, SelectFileDialogSet> fileDialogLabels = new ObjectMap<>();

	@Override
	public void createUI (ATSelectFile annotation, Class type, Field field) {
		String fieldName = annotation.fieldName().equals("") ? field.getName() : annotation.fieldName();

		VisLabel fileLabel = new VisLabel();
		fileLabel.setColor(Color.GRAY);
		fileLabel.setEllipsis(true);

		VisImageButton selectFileButton = new VisImageButton(Icons.MORE.drawable());

		VisTable table = new VisTable(true);
		table.add(new VisLabel(fieldName));
		table.add(fileLabel).width(new VisWidgetValue(context -> Math.min(context.getMinWidth(), MAX_FILE_LABEL_WIDTH)));
		table.add(selectFileButton);

		Tooltip tooltip = new Tooltip(fileLabel, "");

		uiTable.add(table).expandX().fillX().row();

		Holder<ATSelectFileHandler> holder = Holder.of(getHandler(annotation));

		fileDialogLabels.put(field, new SelectFileDialogSet(fileLabel, tooltip, holder.value));

		FileHandle folder = fileAccessModule.getAssetsFolder();

		AssetDirectoryDescriptor directoryDescriptor = assetsMetadata.getDirectoryDescriptorForId(holder.value.getAssetDirectoryDescriptorId());
		final SelectFileDialog selectFontDialog = new SelectFileDialog(annotation.extension(), annotation.hideExtension(),
				folder, assetsMetadata, directoryDescriptor, file -> {
			for (EntityProxy proxy : properties.getSelectedEntities()) {
				for (Entity entity : proxy.getEntities()) {
					holder.value.applyChanges(entity, file);
				}
			}

			properties.getParentTab().dirty();
			properties.selectedEntitiesChanged();
			properties.endSnapshot();
		});

		selectFileButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				selectFontDialog.rebuildFileList();
				properties.beginSnapshot();
				stage.addActor(selectFontDialog.fadeIn());
			}
		});
	}

	private ATSelectFileHandler getHandler (ATSelectFile annotation) {
		String groupClassName = annotation.handlerGroupClass();
		String handlerAlias = annotation.handlerAlias();

		try {

			ATSelectFileHandlerGroup group = handlerGroups.get(groupClassName);

			if (group == null) {
				Class clazz = Class.forName(groupClassName);
				Constructor constructor = clazz.getConstructor();
				try {
					group = (ATSelectFileHandlerGroup) constructor.newInstance();
				} catch (ClassCastException castEx) {
					throw new IllegalStateException("ATSelectFile handler group must be instance of ATSelectFileHandlerGroup", castEx);
				}

				group.setInjector(injector);
				handlerGroups.put(groupClassName, group);
			}

			ATSelectFileHandler handler = group.getByAlias(handlerAlias);
			if (handler == null)
				throw new IllegalStateException("Could not find handler for alias: " + handlerAlias + " in group: " + groupClassName);
			return handler;
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("AutoTable failed to create ATSelectFile handler for class: " + groupClassName, e);
		}
	}

	@Override
	public void updateUIFromEntities (ImmutableArray<EntityProxy> proxies, Class type, Field field) throws ReflectiveOperationException {
		SelectFileDialogSet set = fileDialogLabels.get(field);
		String path = getCommonString(proxies, "<?>", set.handler::getLabelValue);
		set.fileLabel.setText(path);
		((VisLabel) set.tooltip.getContent()).setText(path);
		set.tooltip.pack();
	}

	@Override
	public void setToEntities (Class type, Field field, Component component) throws ReflectiveOperationException {

	}

	@Override
	public Object getUiByField (Class type, Field field) {
		return fileDialogLabels.get(field);
	}

	private static class SelectFileDialogSet {
		public VisLabel fileLabel;
		public Tooltip tooltip;
		public ATSelectFileHandler handler;

		public SelectFileDialogSet (VisLabel fileLabel, Tooltip tooltip, ATSelectFileHandler handler) {
			this.fileLabel = fileLabel;
			this.tooltip = tooltip;
			this.handler = handler;
		}
	}
}
