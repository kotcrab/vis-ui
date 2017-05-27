/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.FileIconProvider;

/**
 * {@link FileIconProvider} implementation supporting extended file chooser view modes (big icons, medium icons and
 * small icons). To use this you must include high resolution texture atlas for files icons. (see `vis-ui-contrib/file-chooser-high-res.atlas`)
 * @author Kotcrab
 */
public class HighResFileChooserIconProvider extends FileChooser.DefaultFileIconProvider implements Disposable {
	private TextureAtlas highResTextures;

	private Drawable[] iconFolderBig = new Drawable[3];
	private Drawable[] iconFileText = new Drawable[3];
	private Drawable[] iconFileImage = new Drawable[3];
	private Drawable[] iconFilePdf = new Drawable[3];
	private Drawable[] iconFileAudio = new Drawable[3];

	public HighResFileChooserIconProvider (FileChooser chooser) {
		super(chooser);
		highResTextures = new TextureAtlas(Gdx.files.classpath("file-chooser-high-res.atlas"));
		loadIcons(iconFolderBig, "icon-folder");
		loadIcons(iconFileText, "icon-file-text");
		loadIcons(iconFileImage, "icon-file-image");
		loadIcons(iconFilePdf, "icon-file-pdf");
		loadIcons(iconFileAudio, "icon-file-audio");
	}

	private void loadIcons (Drawable[] target, String prefix) {
		target[0] = new TextureRegionDrawable(highResTextures.findRegion(prefix + "-small"));
		target[1] = new TextureRegionDrawable(highResTextures.findRegion(prefix + "-medium"));
		target[2] = new TextureRegionDrawable(highResTextures.findRegion(prefix + "-big"));
	}

	private Drawable getIcon (Drawable[] source, FileChooser.ViewMode viewMode) {
		if (viewMode == FileChooser.ViewMode.SMALL_ICONS) return source[0];
		if (viewMode == FileChooser.ViewMode.MEDIUM_ICONS) return source[1];
		if (viewMode == FileChooser.ViewMode.BIG_ICONS) return source[2];
		return null;
	}

	@Override
	public boolean isThumbnailModesSupported () {
		return true;
	}

	@Override
	protected Drawable getDirIcon (FileChooser.FileItem item) {
		Drawable icon = getIcon(iconFolderBig, chooser.getViewMode());
		if (icon == null)
			return super.getDirIcon(item);
		return icon;
	}

	@Override
	protected Drawable getImageIcon (FileChooser.FileItem item) {
		Drawable icon = getIcon(iconFileImage, chooser.getViewMode());
		if (icon == null)
			return super.getImageIcon(item);
		return icon;
	}

	@Override
	protected Drawable getAudioIcon (FileChooser.FileItem item) {
		Drawable icon = getIcon(iconFileAudio, chooser.getViewMode());
		if (icon == null)
			return super.getAudioIcon(item);
		return icon;
	}

	@Override
	protected Drawable getPdfIcon (FileChooser.FileItem item) {
		Drawable icon = getIcon(iconFilePdf, chooser.getViewMode());
		if (icon == null)
			return super.getPdfIcon(item);
		return icon;
	}

	@Override
	protected Drawable getTextIcon (FileChooser.FileItem item) {
		Drawable icon = getIcon(iconFileText, chooser.getViewMode());
		if (icon == null)
			return super.getTextIcon(item);
		return icon;
	}

	@Override
	public void dispose () {
		highResTextures.dispose();
	}
}
