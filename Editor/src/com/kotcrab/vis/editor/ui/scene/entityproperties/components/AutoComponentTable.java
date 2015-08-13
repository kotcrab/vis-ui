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

package com.kotcrab.vis.editor.ui.scene.entityproperties.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.proxy.GroupEntityProxy;
import com.kotcrab.vis.editor.ui.AutoTableEnumSelectBox;
import com.kotcrab.vis.editor.ui.Vector2ArrayView;
import com.kotcrab.vis.editor.ui.dialog.SelectFileDialog;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.ui.scene.entityproperties.NumberInputField;
import com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable.SpecificComponentTable;
import com.kotcrab.vis.editor.util.gdx.FieldUtils;
import com.kotcrab.vis.editor.util.gdx.IntDigitsOnlyFilter;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.component.PolygonComponent;
import com.kotcrab.vis.runtime.util.autotable.*;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static com.kotcrab.vis.editor.util.vis.EntityUtils.getCommonString;

/**
 * Uses magic of annotations and reflection to automatically build and update specific table for component.
 * Only some primitives fields are supported. Component fields must be marked with one of auto table annotations
 * (see util.autotable package in runtime).
 * @author Kotcrab
 */
public class AutoComponentTable<T extends Component> extends SpecificComponentTable<T> {
	public static final int LABEL_WIDTH = 170;

	private Class<T> componentClass;

	private ModuleInjector injector;
	private boolean removable;

	@InjectModule private FileAccessModule fileAccessModule;

	private ObjectMap<Field, NumberInputField> numberFields = new ObjectMap<>();
	private ObjectMap<Field, IndeterminateCheckbox> checkboxFields = new ObjectMap<>();
	private ObjectMap<Field, SelectFileDialogSet> fileDialogLabels = new ObjectMap<>();
	private ObjectMap<Field, Vector2ArrayView> vector2Views = new ObjectMap<>();
	private ObjectMap<Field, EnumSelectBoxSet> enumSelectBoxes = new ObjectMap<>();

	public AutoComponentTable (ModuleInjector injector, Class<T> componentClass, boolean removable) {
		super(true);
		this.injector = injector;
		this.removable = removable;
		this.componentClass = componentClass;
		injector.injectModules(this);
	}

	@Override
	public Class<T> getComponentClass () {
		return componentClass;
	}

	@Override
	public boolean isRemovable () {
		return removable;
	}

	@Override
	protected void init () {
		defaults().left();
		left();

		for (Field field : componentClass.getDeclaredFields()) {
			Class type = field.getType();

			for (Annotation annotation : field.getAnnotations()) {
				if (annotation instanceof ATEntityProperty)
					createEntityPropertyField(field, type, (ATEntityProperty) annotation);
				if (annotation instanceof ATSelectFile) createFileChooserField(field, (ATSelectFile) annotation);
				if (annotation instanceof ATVector2Array) createVector2ArrayView(field, (ATVector2Array) annotation);
				if (annotation instanceof ATEnumProperty)
					createEnumSelectBox(field, type, (ATEnumProperty) annotation);
			}
		}
	}

	@Override
	public void updateUIValues () {
		try {

			Array<EntityProxy> proxies = properties.getProxies();

			for (Field field : componentClass.getDeclaredFields()) {
				Class type = field.getType();
				for (Annotation annotation : field.getAnnotations()) {
					if (annotation instanceof ATEntityProperty) updateEntityPropertyField(field, type, proxies);
					if (annotation instanceof ATSelectFile) updateFileChooser(field, proxies);
					if (annotation instanceof ATVector2Array) updateVector2View(field, proxies);
					if (annotation instanceof ATEnumProperty) updateEnumSelectBox(field, proxies);
				}
			}
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void setValuesToEntities () {
		try {

			for (EntityProxy proxy : properties.getProxies()) {
				for (Entity entity : proxy.getEntities()) {

					T component = entity.getComponent(componentClass);

					for (Field field : componentClass.getDeclaredFields()) {
						Class type = field.getType();
						for (Annotation annotation : field.getAnnotations()) {
							if (annotation instanceof ATEntityProperty)
								setFromEntityPropertyField(field, type, component);
							if (annotation instanceof ATEnumProperty) setFromEnumSelectBoxField(field, type, component);
						}
					}
				}
			}

		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	// ============ NumberFields ============

	private void createEntityPropertyField (Field field, Class type, ATEntityProperty propertyUI) {
		if (type.equals(Integer.TYPE) == false && type.equals(Float.TYPE) == false && type.equals(Boolean.TYPE) == false) {
			throw new UnsupportedOperationException("Field of this type is not supported by EntityPropertyUI: " + type);
		}

		String fieldName = propertyUI.fieldName().equals("") ? field.getName() : propertyUI.fieldName();
		if (type.equals(Boolean.TYPE)) {
			IndeterminateCheckbox checkbox = new IndeterminateCheckbox(fieldName);
			checkbox.addListener(properties.getSharedCheckBoxChangeListener());

			VisTable table = new VisTable(true);
			table.add(checkbox).left();
			add(table).left().expandX().row();
			checkboxFields.put(field, checkbox);
		} else {
			NumberInputField numberInputField = new NumberInputField(properties.getSharedFocusListener(), properties.getSharedChangeListener());

			if (type.equals(Integer.TYPE)) numberInputField.setTextFieldFilter(new IntDigitsOnlyFilter());

			VisTable table = new VisTable(true);

			table.add(new VisLabel(fieldName)).width(LABEL_WIDTH);
			table.add(numberInputField).width(EntityProperties.FIELD_WIDTH);
			add(table).expandX().fillX().row();
			numberFields.put(field, numberInputField);
		}
	}

	private void updateEntityPropertyField (Field field, Class type, Array<EntityProxy> proxies) {
		if (type.equals(Boolean.TYPE)) {
			IndeterminateCheckbox checkbox = checkboxFields.get(field);

			EntityUtils.setCommonCheckBoxState(proxies, checkbox, (Entity entity) -> {
				try {
					return (boolean) field.get(entity.getComponent(componentClass));
				} catch (IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
			});

		} else {
			NumberInputField inputField = numberFields.get(field);

			if (type.equals(Integer.TYPE)) {
				inputField.setText(EntityUtils.getEntitiesCommonIntegerValue(proxies,
						(Entity entity) -> {
							try {
								return (int) field.get(entity.getComponent(componentClass));
							} catch (IllegalAccessException e) {
								throw new IllegalStateException(e);
							}
						}));
			} else {
				inputField.setText(EntityUtils.getEntitiesCommonFloatValue(proxies,
						(Entity entity) -> {
							try {
								return (float) field.get(entity.getComponent(componentClass));
							} catch (IllegalAccessException e) {
								throw new IllegalStateException(e);
							}
						}));
			}
		}
	}

	private void setFromEntityPropertyField (Field field, Class type, T component) throws IllegalAccessException {
		if (type.equals(Boolean.TYPE)) {
			IndeterminateCheckbox checkbox = checkboxFields.get(field);
			if (checkbox.isIndeterminate() == false) field.set(component, checkbox.isChecked());
		}

		if (type.equals(Integer.TYPE)) {
			try {
				int value = FieldUtils.getInt(numberFields.get(field), (int) field.get(component));
				field.set(component, value);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		if (type.equals(Float.TYPE)) {
			try {
				float value = FieldUtils.getFloat(numberFields.get(field), (float) field.get(component));
				field.set(component, value);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	// ============ FileChooser ============

	private void createFileChooserField (Field field, ATSelectFile propertyUI) {
		String fieldName = propertyUI.fieldName().equals("") ? field.getName() : propertyUI.fieldName();

		VisLabel fileLabel = new VisLabel();
		fileLabel.setColor(Color.GRAY);
		fileLabel.setEllipsis(true);

		VisImageButton selectFileButton = new VisImageButton(Assets.getIcon(Icons.MORE));

		VisTable table = new VisTable(true);
		table.add(new VisLabel(fieldName));
		table.add(fileLabel).expandX().fillX();
		table.add(selectFileButton);

		Tooltip tooltip = new Tooltip(fileLabel, "");

		add(table).expandX().fillX().row();

		ATSelectFileHandler handler = null;

		try {
			Class clazz = Class.forName(propertyUI.handlerClass());
			Constructor constructor = clazz.getConstructor();
			Object object = constructor.newInstance();

			if (object instanceof ATSelectFileHandler == false) {
				throw new IllegalStateException("SelectFilePropertyUI handler must be instance of SelectFilePropertyHandler");
			}

			handler = (ATSelectFileHandler) object;
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("AutoTable failed, failed to create handler with class: " + propertyUI.handlerClass(), e);
		}

		injector.injectModules(handler);

		fileDialogLabels.put(field, new SelectFileDialogSet(fileLabel, tooltip, handler));

		FileHandle folder = fileAccessModule.getAssetsFolder().child(propertyUI.relativeFolderPath());

		final ATSelectFileHandler finalHandler = handler;
		final SelectFileDialog selectFontDialog = new SelectFileDialog(propertyUI.extension(), propertyUI.hideExtension(), folder, file -> {
			for (EntityProxy proxy : properties.getProxies()) {
				for (Entity entity : proxy.getEntities()) {
					finalHandler.applyChanges(entity, file);
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
				getStage().addActor(selectFontDialog.fadeIn());
			}
		});
	}

	private void updateFileChooser (Field field, Array<EntityProxy> proxies) {
		SelectFileDialogSet set = fileDialogLabels.get(field);
		String path = getCommonString(proxies, "<?>", set.handler::getLabelValue);
		set.fileLabel.setText(path);
		((VisLabel) set.tooltip.getContent()).setText(path);
		set.tooltip.pack();
	}

	// ============ Vector2View ============

	private void createVector2ArrayView (Field field, ATVector2Array annotation) {
		Vector2ArrayView view = new Vector2ArrayView();
		vector2Views.put(field, view);

		add(annotation.fieldName()).spaceBottom(3).row();
		add(view).expandX().fillX().row();
	}

	private void updateVector2View (Field field, Array<EntityProxy> proxies) {
		Vector2ArrayView view = vector2Views.get(field);

		if (proxies.size > 1) {
			view.setMultipleSelected(true);
		} else {
			EntityProxy proxy = proxies.get(0);

			if (proxy instanceof GroupEntityProxy && proxy.getEntities().size > 1)
				view.setMultipleSelected(true);
			else
				view.setVectors(proxies.first().getEntities().first().getComponent(PolygonComponent.class).vertices);
		}
	}

	// ============ SelectBox ============

	@SuppressWarnings("unchecked")
	private void createEnumSelectBox (Field field, Class type, ATEnumProperty annotation) {
		try {
			String fieldName = annotation.fieldName().equals("") ? field.getName() : annotation.fieldName();

			Constructor constructor = annotation.uiNameProvider().getConstructor();
			EnumNameProvider nameProvider = (EnumNameProvider) constructor.newInstance();

			AutoTableEnumSelectBox selectBox = new AutoTableEnumSelectBox<>(type, nameProvider);
			selectBox.getSelection().setProgrammaticChangeEvents(false);
			selectBox.addListener(properties.getSharedSelectBoxChangeListener());
			enumSelectBoxes.put(field, new EnumSelectBoxSet(selectBox, nameProvider));

			VisTable table = new VisTable(true);
			table.add(fieldName);
			table.add(selectBox).expandX().fillX().left();
			add(table).left().expandX().fillX().row();
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateEnumSelectBox (Field field, Array<EntityProxy> proxies) throws IllegalAccessException {
		EnumSelectBoxSet set = enumSelectBoxes.get(field);

		String commonValue = EntityUtils.getCommonString(proxies, AutoTableEnumSelectBox.INDETERMINATE,
				(Entity entity) -> {
					try {
						return set.enumNameProvider.getPrettyName((Enum) field.get(entity.getComponent(componentClass)));
					} catch (IllegalAccessException e) {
						throw new IllegalStateException(e);
					}
				});

		if (commonValue.equals(AutoTableEnumSelectBox.INDETERMINATE)) {
			set.selectBox.setIndeterminate(true);
		} else {
			set.selectBox.setIndeterminate(false);
			set.selectBox.setSelectedEnum((Enum) field.get(proxies.first().getEntities().first().getComponent(componentClass)));
		}
	}

	private void setFromEnumSelectBoxField (Field field, Class type, T component) throws IllegalAccessException {
		EnumSelectBoxSet set = enumSelectBoxes.get(field);

		if (set.selectBox.isIndeterminate() == false) {
			field.set(component, set.selectBox.getSelectedEnum());
		}
	}

	// ============ Other ============

	private static class EnumSelectBoxSet {
		public AutoTableEnumSelectBox selectBox;
		public EnumNameProvider enumNameProvider;

		public EnumSelectBoxSet (AutoTableEnumSelectBox selectBox, EnumNameProvider enumNameProvider) {
			this.selectBox = selectBox;
			this.enumNameProvider = enumNameProvider;
		}
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
