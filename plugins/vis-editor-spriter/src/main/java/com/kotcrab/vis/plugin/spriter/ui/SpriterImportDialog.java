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

package com.kotcrab.vis.plugin.spriter.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.ui.dialog.DetailsDialog;
import com.kotcrab.vis.editor.util.async.Async;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.vis.EditorException;
import com.kotcrab.vis.plugin.spriter.module.SpriterDataIOModule;
import com.kotcrab.vis.plugin.spriter.util.SpriterAssetData;
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.async.SteppedAsyncTask;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.*;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

/** @author Kotcrab */
public class SpriterImportDialog extends VisWindow {
	private final VisImage image;
	private FileHandle animFolder;
	private FileHandle visAnimFolder;
	private final VisValidatableTextField scaleField;

	private Array<String> warnings = new Array<>();

	public SpriterImportDialog (FileHandle animFolder, String relativePath) {
		super("Import Spriter Animation");
		this.animFolder = animFolder;

		visAnimFolder = animFolder.child(".vis");

		left();
		defaults().left();

		setModal(true);
		addCloseButton();
		closeOnEscape();
		TableUtils.setSpacingDefaults(this);

		scaleField = new VisValidatableTextField("1.00");
		scaleField.setTextFieldFilter(new FloatDigitsOnlyFilter(false));
		image = new VisImage(Icons.QUESTION_BIG.drawable());
		new Tooltip.Builder("If your images are too big you can use this to rescale source images").target(image).build();

		VisTable scaleRatioTable = new VisTable();

		scaleRatioTable.add(new VisLabel("Image Scale Ratio: "));
		scaleRatioTable.add(scaleField);
		scaleRatioTable.add(image).size(22);

		VisLabel helpLabel = new VisLabel("Animation will be converted. Do not change files inside this directory after importing. To update animation go to " +
				relativePath + "/.vis/update and paste new animation files there.");
		helpLabel.setWrap(true);

		VisTextButton cancelButton = new VisTextButton("Cancel");
		VisTextButton importButton = new VisTextButton("Import");

		VisLabel errorLabel = new VisLabel("", Color.RED);

		VisTable buttonTable = new VisTable(true);
		buttonTable.add(cancelButton);
		buttonTable.add(importButton);

		add(scaleRatioTable).expandX().fillX().colspan(2);
		row();
		add(helpLabel).expandX().fillX().colspan(2);
		row();
		add(errorLabel).expandX().fillX().minWidth(300);
		add(buttonTable).padBottom(3);

		pack();
		centerWindow();

		FormValidator formValidator = new FormValidator(importButton, errorLabel);
		formValidator.valueGreaterThan(scaleField, "Scale ratio must be greater than 0.01", 0.01f, true);
		formValidator.valueLesserThan(scaleField, "Scale ratio must be lesser than 1", 1f, true);

		cancelButton.addListener(new VisChangeListener((event, actor) -> fadeOut()));
		importButton.addListener(new VisChangeListener((event, actor) -> {
			try {
				checkSettings();
			} catch (EditorException e) {
				Dialogs.showErrorDialog(getStage(), e.getMessage());
				return;
			}

			if (warnings.size > 0) {
				Dialogs.showConfirmDialog(getStage(), "Warning", "Some problem were found during files check",
						new String[]{"Details", "Import Anyway"},
						new Integer[]{0, 1}, result -> {
							if (result == 0) {
								final StringBuilder warningMsg = new StringBuilder();
								warnings.forEach(s -> warningMsg.append(s + "\n\n"));
								getStage().addActor(new DetailsDialog("Some problem were found during files check", "Warning Details", warningMsg.toString()));
							} else {
								importAnimation();
								fadeOut();
							}
						});
				return;
			}

			importAnimation();
			fadeOut();
		}));
	}

	private void importAnimation () {
		SteppedAsyncTask importTask = new SteppedAsyncTask("SpriterImporter") {
			@Override
			public void doInBackground () throws IOException {
				FileHandle[] files = animFolder.list();
				setTotalSteps(files.length * 2);

				float scaleFactor = Float.valueOf(scaleField.getText());

				visAnimFolder.mkdirs();
				visAnimFolder.child("update").mkdirs();

				FileHandle target = visAnimFolder.child("original");
				target.mkdirs();

				for (FileHandle file : files) {
					setMessage("Copying " + file.name());
					file.copyTo(target);
					nextStep();
				}

				for (FileHandle file : files) {
					if (file.extension().equals("png") == false) continue;

					setMessage("Converting " + file.name());
					BufferedImage image = ImageIO.read(file.file());
					BufferedImage scaledImage = Scalr.resize(image, MathUtils.round(image.getWidth() * scaleFactor), MathUtils.round(image.getHeight() * scaleFactor));
					ImageIO.write(scaledImage, "png", file.file());
					nextStep();
				}

				setMessage("Saving data");

				Json json = SpriterDataIOModule.getNewJson();
				SpriterAssetData assetData = new SpriterAssetData(scaleFactor);
				json.toJson(assetData, visAnimFolder.child("data.json"));
			}
		};
		Async.startTask(getStage(), "Importing animation", importTask);
	}

	private void checkSettings () throws EditorException {
		warnings.clear();
		for (FileHandle file : animFolder.list()) {
			if (file.extension().equals("png") == false) continue;

			try (ImageInputStream in = ImageIO.createImageInputStream(file.file())) {
				final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
				if (readers.hasNext()) {
					ImageReader reader = readers.next();
					try {
						reader.setInput(in);
						float scale = Float.valueOf(scaleField.getText());

						int w = reader.getWidth(0);
						int h = reader.getHeight(0);

						if (scale != 1) {
							if (w < 10 || h < 10)
								throw new EditorException("Images are too small to scale them, set scale ratio to 1 or modify your animation.");

							int rw = (int) (w * scale);
							int rh = (int) (h * scale);

							checkIfTooBigForAtlas(file, rw, rh);
							checkIfVeryBig(file, rw, rh);
						} else {
							checkIfTooBigForAtlas(file, w, h);
							checkIfVeryBig(file, w, h);
						}

					} finally {
						reader.dispose();
					}
				}
			} catch (IOException e) {
				Dialogs.showErrorDialog(getStage(), "");
				Log.exception(e);
			}
		}
	}

	private void checkIfVeryBig (FileHandle file, int w, int h) {
		if (w > 400 || h > 400) {
			warnings.add("Image " + file.name() + " is very big. This may affect performance on mobile devices.\nConsider decreasing scale ratio.");
		}
	}

	private void checkIfTooBigForAtlas (FileHandle file, int w, int h) throws EditorException {
		if (w > 2048 || h > 2048)
			throw new EditorException("Image " + file.name() + " is too big to load it to memory on most mobile devices.\n" +
					"Animation can't be imported. Decrease scale ratio.");
	}
}
