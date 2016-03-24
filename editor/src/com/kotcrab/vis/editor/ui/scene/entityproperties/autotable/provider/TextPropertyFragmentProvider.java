package com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.provider;

import com.artemis.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.IndeterminateTextField;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.autotable.ATTextProperty;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.lang.reflect.Field;

/** @author Kotcrab */
public class TextPropertyFragmentProvider extends AutoTableFragmentProvider<ATTextProperty> {
	private ObjectMap<Field, IndeterminateTextField> textFields = new ObjectMap<>();

	@Override
	public Object getUIByField (Class<?> type, Field field) {
		return textFields.get(field);
	}

	@Override
	public void createUI (ATTextProperty annotation, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		if (fieldType.equals(CharSequence.class) == false) {
			throw new UnsupportedOperationException("Field of this type is not supported by TextPropertyFragmentProvider: " + fieldType);
		}

		String fieldName = annotation.fieldName().equals("") ? field.getName() : annotation.fieldName();
		IndeterminateTextField textField = new IndeterminateTextField();
		properties.setupStdPropertiesTextField(textField.getTextField());

		VisTable table = new VisTable(true);
		table.add(new VisLabel(fieldName));
		table.add(textField.getTextField()).expandX().fillX();
		uiTable.add(table).width(230).row();
		textFields.put(field, textField);
	}

	@Override
	public void updateUIFromEntities (ImmutableArray<EntityProxy> proxies, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		IndeterminateTextField textField = textFields.get(field);

		String commonString = EntityUtils.getCommonString(proxies, null, (EntityProxy entity) -> {
			try {
				field.setAccessible(true);
				return (String) field.get(entity.getComponent(componentClass));
			} catch (ReflectiveOperationException e) {
				Log.exception(e);
				throw new IllegalStateException(e);
			}
		});

		if (commonString == null) {
			textField.setIndeterminate(true);
		} else {
			textField.setIndeterminate(false);
			textField.setText(commonString);
		}
	}

	@Override
	public void setToEntities (Component component, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		field.setAccessible(true);
		IndeterminateTextField textField = textFields.get(field);
		if(textField.isIndeterminate()) return;
		field.set(component, textField.getText());
	}
}
