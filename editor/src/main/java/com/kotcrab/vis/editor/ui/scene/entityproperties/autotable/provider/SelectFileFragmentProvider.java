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

package com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.provider;

import com.artemis.Component;
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
import com.kotcrab.vis.editor.ui.scene.entityproperties.components.handler.ATExtSelectFileHandler;
import com.kotcrab.vis.editor.util.Holder;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFile;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFileHandler;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
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
	public void createUI (ATSelectFile annotation, Field field, Class<?> fieldType) {
		String fieldName = annotation.fieldName().equals("") ? field.getName() : annotation.fieldName();

		VisLabel fileLabel = new VisLabel();
		fileLabel.setColor(Color.GRAY);
		fileLabel.setEllipsis(true);

		VisImageButton selectFileButton = new VisImageButton(Icons.MORE.drawable());

		VisTable table = new VisTable(true);
		table.left();
		table.add(new VisLabel(fieldName));
		table.add(fileLabel).width(new VisWidgetValue(context -> Math.min(context.getMinWidth(), MAX_FILE_LABEL_WIDTH)));
		table.add(selectFileButton);

		Tooltip tooltip = new Tooltip.Builder("").target(fileLabel).build();

		uiTable.add(table).expandX().fillX().row();

		ATSelectFileHandlerGroup group = getHandlerGroup(annotation);
		ATSelectFileHandler handler = group.getByAlias(annotation.handlerAlias());
		ATExtSelectFileHandler extHandler = group.getExtByAlias(annotation.extHandlerAlias());
		if (handler == null)
			throw new IllegalStateException("Could not find handler for alias: " + annotation.handlerAlias() + " in group: " + annotation.handlerGroupClass());

		Holder<ATSelectFileHandler> handlerHolder = Holder.of(handler);
		FileHandle folder = fileAccessModule.getAssetsFolder();

		AssetDirectoryDescriptor directoryDescriptor = assetsMetadata.getDirectoryDescriptorForId(handlerHolder.value.getAssetDirectoryDescriptorId());
		final SelectFileDialog selectFileDialog = new SelectFileDialog(annotation.extension(), annotation.hideExtension(), folder, assetsMetadata, directoryDescriptor,
				file -> {
					for (EntityProxy proxy : properties.getSelectedEntities()) {
						handlerHolder.value.applyChanges(proxy.getEntity(), file);
					}

					properties.getParentTab().dirty();
					properties.selectedEntitiesValuesChanged();
					properties.endSnapshot();
				});

		selectFileButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (extHandler != null) {
					String extension = extHandler.resolveExtension(properties.getSelectedEntities());
					if (extension == null) {
						Dialogs.showOKDialog(stage, "Message", "Select file dialog can't be showed for current selection because there is no common extension" +
								"for selected entities.");
						return;
					}
					selectFileDialog.setExtensions(extension);
				}

				selectFileDialog.rebuildFileList();
				properties.beginSnapshot();
				stage.addActor(selectFileDialog.fadeIn());
			}
		});

		fileDialogLabels.put(field, new SelectFileDialogSet(fileLabel, tooltip, selectFileDialog, handler, extHandler));
	}

	private ATSelectFileHandlerGroup getHandlerGroup (ATSelectFile annotation) {
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

			return group;
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("AutoTable failed to create ATSelectFile handler for class: " + groupClassName, e);
		}
	}

	@Override
	public void updateUIFromEntities (ImmutableArray<EntityProxy> proxies, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		SelectFileDialogSet set = fileDialogLabels.get(field);
		String path = getCommonString(proxies, "<?>", set.handler::getLabelValue);
		set.fileLabel.setText(path);
		((VisLabel) set.tooltip.getContent()).setText(path);
		set.tooltip.pack();
	}

	@Override
	public void setToEntities (Component component, Field field, Class<?> fieldType) throws ReflectiveOperationException {

	}

	@Override
	public Actor getUIByField (Class type, Field field) {
		SelectFileDialogSet set = fileDialogLabels.get(field);
		if (set == null) return null;
		return set.fileLabel;
	}

	private static class SelectFileDialogSet {
		public VisLabel fileLabel;
		public Tooltip tooltip;
		public SelectFileDialog selectFileDialog;
		public ATSelectFileHandler handler;
		public ATExtSelectFileHandler extHandler;

		public SelectFileDialogSet (VisLabel fileLabel, Tooltip tooltip, SelectFileDialog selectFileDialog,
									ATSelectFileHandler handler, ATExtSelectFileHandler extHandler) {
			this.fileLabel = fileLabel;
			this.tooltip = tooltip;
			this.selectFileDialog = selectFileDialog;
			this.handler = handler;
			this.extHandler = extHandler;
		}
	}
}
