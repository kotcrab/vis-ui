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

package com.kotcrab.vis.editor.extension;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent.ResourceType;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.module.project.SpriterDataIOModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetsUIContextGenerator;
import com.kotcrab.vis.editor.ui.dialog.AsyncTaskProgressDialog;
import com.kotcrab.vis.editor.ui.dialog.SpriterImportDialog;
import com.kotcrab.vis.editor.util.async.SteppedAsyncTask;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.EnumSet;

/** @author Kotcrab */
public class SpriterUIContextGenerator implements AssetsUIContextGenerator {
	private AssetsMetadataModule assetsMetadata;
	private SpriterDataIOModule spriterDataIO;

	private Stage stage;

	private VisTable importTable;
	private VisTable updateTable;

	private FileHandle animFolder;
	private String relativePath;

	@Override
	public void init () {
		VisTextButton importButton = new VisTextButton("Import", "blue");
		importButton.addListener(new VisChangeListener((event, actor) -> stage.addActor(new SpriterImportDialog(animFolder, relativePath).fadeIn())));

		importTable = new VisTable();
		importTable.pad(3);
		importTable.add(new VisLabel("Spriter animation requires importing before use")).padRight(4);
		importTable.add(importButton);
		importTable.add().expandX().fillX();

		VisTextButton updateButton = new VisTextButton("Update", "blue");
		updateButton.addListener(new VisChangeListener((event, actor) ->
				stage.addActor(new AsyncTaskProgressDialog("Updating Animation", new UpdateAnimationAsyncTask()))));

		updateTable = new VisTable();
		updateTable.pad(3);
		updateTable.add(new VisLabel("Apply animation update?")).padRight(4);
		updateTable.add(updateButton);
		updateTable.add().expandX().fillX();
	}

	@Override
	public VisTable provideContext (FileHandle fileHandle, String relativePath) {
		if (ProjectPathUtils.isNotImportedSpriterAnimationDir(assetsMetadata, fileHandle)) {
			this.animFolder = fileHandle;
			this.relativePath = relativePath;

			FileHandle visFolder = fileHandle.child(".vis");
			if (visFolder.exists() == false) {
				return importTable;
			}

			if (visFolder.child("update").list().length > 0) {
				return updateTable;
			}
		}

		return null;
	}

	private class UpdateAnimationAsyncTask extends SteppedAsyncTask {
		public UpdateAnimationAsyncTask () {
			super("SpriterAnimationUpdater");
		}

		@Override
		public void execute () throws Exception {
			FileHandle visAnimFolder = animFolder.child(".vis");
			FileHandle updateFolder = visAnimFolder.child("update");
			FileHandle beforeUpdateFolder = visAnimFolder.child("before-update");
			FileHandle originalFolder = visAnimFolder.child("original");

			FileHandle[] updateFiles = updateFolder.list();
			setTotalSteps(updateFiles.length * 3 + 1);

			float imageScale = spriterDataIO.loadData(animFolder.child(".vis").child("data.json")).imageScale;

			setMessage("Cleaning old files");

			originalFolder.copyTo(beforeUpdateFolder);

			for (FileHandle file : animFolder.list()) {
				if (file.name().equals(".vis")) continue;
				file.delete();
			}

			for (FileHandle file : originalFolder.list()) {
				file.delete();
			}

			nextStep();

			for (FileHandle file : updateFiles) {
				setMessage("Copying " + file.name());
				file.copyTo(originalFolder);
				nextStep();
			}

			for (FileHandle file : updateFiles) {
				String fileName = file.name();

				if (file.extension().equals("png")) {
					setMessage("Converting " + fileName);
					BufferedImage image = ImageIO.read(file.file());
					BufferedImage scaledImage = Scalr.resize(image, MathUtils.round(image.getWidth() * imageScale), MathUtils.round(image.getHeight() * imageScale));
					ImageIO.write(scaledImage, "png", animFolder.child(fileName).file());
				} else
					file.copyTo(animFolder);

				nextStep();
			}

			setMessage("Cleaning update files");
			for (FileHandle file : updateFiles) {
				file.delete();
				nextStep();
			}

			App.eventBus.postToMainThread(new ResourceReloadedEvent(EnumSet.of(ResourceType.SPRITER_DATA)));
		}
	}
}
