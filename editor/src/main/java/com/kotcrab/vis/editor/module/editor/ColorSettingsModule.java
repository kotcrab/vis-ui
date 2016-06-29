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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;
import com.kotcrab.vis.editor.module.editor.ColorSettingsModule.ColorConfig;
import com.kotcrab.vis.editor.ui.EnumSelectBox;
import com.kotcrab.vis.editor.ui.TintImage;
import com.kotcrab.vis.editor.util.BiHolder;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.scene2d.VisColorPickerListener;
import com.kotcrab.vis.editor.util.vis.PrettyEnumNameProvider;
import com.kotcrab.vis.runtime.util.PrettyEnum;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.color.ColorPicker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * VisEditor colors settings module
 * @author Kotcrab
 */
public class ColorSettingsModule extends EditorSettingsModule<ColorConfig> {
	private static final int COLOR_LABEL_WIDTH = 100;

	private ColorPickerModule colorPickerModule;

	private Stage stage;

	private ColorPicker colorPicker;

	private TintImage backgroundColorImage;
	private TintImage gridColorImage;
	private EnumSelectBox<PresetType> presetsSelectBox;

	private Array<BiHolder<TintImage, Color>> images = new Array<>();

	public ColorSettingsModule () {
		super("Colors", "colorSettings", ColorConfig.class);
	}

	@Override
	public void buildTable () {
		prepareTable();
		colorPicker = colorPickerModule.getPicker();

		presetsSelectBox = new EnumSelectBox<>(PresetType.class, new PrettyEnumNameProvider<>());
		presetsSelectBox.addListener(new VisChangeListener((event, actor) -> updateAllImages()));

		settingsTable.defaults().expand(false, false).left();
		settingsTable.add(TableBuilder.build(new VisLabel("Preset"), presetsSelectBox)).row();
		settingsTable.add(createColorTable("Background", backgroundColorImage = new TintImage(true), config.backgroundColor)).row();
		settingsTable.add(createColorTable("Grid", gridColorImage = new TintImage(true), config.gridColor));
	}

	private void updateAllImages () {
		for (BiHolder<TintImage, Color> holder : images) {
			TintImage image = holder.first;
			Color color = holder.second;
			image.setColor(config.getDefault(presetsSelectBox.getSelectedEnum(), color));
		}
	}

	private VisTable createColorTable (String name, TintImage image, Color configColor) {
		VisTextButton resetButton = new VisTextButton("Reset");

		VisTable table = new VisTable(true);
		table.add(new VisLabel(name)).width(COLOR_LABEL_WIDTH);
		table.add(image).size(80, 30);
		table.add(resetButton);

		images.add(new BiHolder<>(image, configColor));

		resetButton.addListener(new VisChangeListener((event, actor) ->
				image.setColor(config.getDefault(presetsSelectBox.getSelectedEnum(), configColor))));

		image.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				colorPicker.setColor(image.getColor());
				colorPicker.setListener((VisColorPickerListener) image::setColor);
				stage.addActor(colorPicker.fadeIn());
			}
		});

		return table;
	}

	@Override
	public void loadConfigToTable () {
		presetsSelectBox.setSelectedEnum(config.presetType);
		backgroundColorImage.setColor(config.backgroundColor);
		gridColorImage.setColor(config.gridColor);
	}

	@Override
	public void settingsApply () {
		config.presetType = presetsSelectBox.getSelectedEnum();
		config.backgroundColor.set(backgroundColorImage.getColor());
		config.gridColor.set(gridColorImage.getColor());

		settingsSave();
	}

	public Color getBackgroundColor () {
		return config.backgroundColor;
	}

	public Color getGridColor () {
		return config.gridColor;
	}

	public static class ColorConfig {
		@Tag(0) PresetType presetType = PresetType.DARK;

		//@formatter:off
		@Tag(1) @Presets(black = @Preset(r = 0f, g = 0f, b = 0f),
				          dark = @Preset(r = 0.12f, g = 0.16f, b = 0.19f))
		Color backgroundColor = new Color();

		@Tag(2) @Presets(black = @Preset(r = 0.32f, g = 0.32f, b = 0.32f),
				          dark = @Preset(r = 0.19f, g = 0.22f, b = 0.27f))
		Color gridColor = new Color();
		//@formatter:on

		public ColorConfig () {
			Field[] fields = ColorConfig.class.getDeclaredFields();

			try {
				for (Field f : fields) {
					if (f.getType() == Color.class) {
						Preset preset = getPreset(presetType, f.getAnnotation(Presets.class));
						((Color) f.get(this)).set(preset.r(), preset.g(), preset.b(), preset.a());
					}
				}
			} catch (ReflectiveOperationException e) {
				throw new IllegalStateException(e);
			}
		}

		public Color getDefault (PresetType presetType, Color color) {
			Field field = getField(color);
			Preset preset = getPreset(presetType, field.getAnnotation(Presets.class));
			return new Color(preset.r(), preset.g(), preset.b(), preset.a());
		}

		public Field getField (Color color) {
			Field[] fields = ColorConfig.class.getDeclaredFields();

			try {
				for (Field f : fields) {
					if (f.get(this) == color) {
						return f;
					}
				}
			} catch (ReflectiveOperationException e) {
				throw new IllegalStateException(e);
			}

			return null;
		}

		public Preset getPreset (PresetType presetType, Presets presets) {
			switch (presetType) {
				case BLACK:
					return presets.black();
				case DARK:
					return presets.dark();
			}

			throw new IllegalStateException("Missing case: " + presetType);
		}
	}

	private enum PresetType implements PrettyEnum {
		BLACK {
			@Override
			public String toPrettyString () {
				return "Black";
			}
		},
		DARK {
			@Override
			public String toPrettyString () {
				return "Dark (default)";
			}
		}
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Presets {
		Preset black ();

		Preset dark ();
	}

	@Target(ElementType.ANNOTATION_TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Preset {
		float r ();

		float g ();

		float b ();

		float a () default 1f;
	}
}
