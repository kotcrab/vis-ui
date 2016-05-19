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

package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** @author Kotcrab */
public class ImgScalrFileChooserIconProvider extends HighResFileChooserIconProvider {
	private static final Color tmpColor = new Color();
	private static final int MAX_CACHED = 600;
	private static final int MAX_THREADS = 1;

	private final FileChooser chooser;
	private ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
	private Array<Thumbnail> thumbnails = new Array<Thumbnail>();

	public ImgScalrFileChooserIconProvider (FileChooser chooser) {
		super(chooser);
		this.chooser = chooser;
		System.setProperty("java.awt.headless", "true");
	}

	@Override
	protected Drawable getImageIcon () {
		if (chooser.getViewMode().isThumbnailMode()) {
			final FileChooser.FileItem item = currentItem;
			final FileHandle file = currentFile;
			final FileChooser.ViewMode viewMode = chooser.getViewMode();
			final float thumbSize = viewMode.getGridSize(chooser.getSizes());

			Thumbnail thumbnail = getThumbnail(file);
			if (thumbnail == null) {
				thumbnail = new Thumbnail(file);
				thumbnails.add(thumbnail);
			}

			if (thumbnail.getThumb(viewMode) != null) return thumbnail.getThumb(viewMode);

			final Thumbnail fThumbnail = thumbnail;
			executor.execute(new Runnable() {
				@Override
				public void run () {
					try {
						final BufferedImage imageFile = ImageIO.read(file.file());
						final BufferedImage scaledImg = Scalr.resize(imageFile, Scalr.Method.BALANCED, Scalr.Mode.AUTOMATIC, (int) thumbSize);

						FileHandle tmpThumbFile = null;
						if (scaledImg.getType() != BufferedImage.TYPE_INT_RGB && scaledImg.getType() != BufferedImage.TYPE_INT_ARGB) {
							tmpThumbFile = FileHandle.tempFile("filechooser");
							ImageIO.write(scaledImg, "png", tmpThumbFile.file());
						}
						updateItemImage(fThumbnail, viewMode, item, scaledImg, tmpThumbFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

		return super.getImageIcon();
	}

	private void updateItemImage (final Thumbnail thumbnail, final FileChooser.ViewMode viewMode, final FileChooser.FileItem item,
								  final BufferedImage scaledImg, final FileHandle thumbFile) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				try {
					Texture texture;
					if (thumbFile == null) {
						Pixmap pixmap = imageToPixmap(scaledImg);
						texture = new Texture(pixmap);
						pixmap.dispose();
					} else {
						texture = new Texture(thumbFile);
					}

					thumbnail.addThumb(viewMode, texture);
					item.setIcon(thumbnail.getThumb(viewMode));
				} catch (GdxRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Pixmap imageToPixmap (BufferedImage image) {
		final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		Pixmap pixmap = new Pixmap(width, height, hasAlphaChannel ? Pixmap.Format.RGBA8888 : Pixmap.Format.RGB888);

		if (hasAlphaChannel) {
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel++) {

				Color.argb8888ToColor(tmpColor, pixels[pixel]);
				pixmap.drawPixel(col, row, Color.rgba8888(tmpColor));

				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		} else {
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel++) {
				int color = pixels[pixel];
				tmpColor.r = ((color & 0x00ff0000) >>> 16) / 255f;
				tmpColor.g = ((color & 0x0000ff00) >>> 8) / 255f;
				tmpColor.b = ((color & 0x000000ff)) / 255f;
				tmpColor.a = 1f;

				pixmap.drawPixel(col, row, Color.rgba8888(tmpColor));

				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		}

		return pixmap;
	}

	@Override
	public void directoryChanged (FileHandle newDirectory) {
		super.directoryChanged(newDirectory);
		restartThumbnailGeneration();
	}

	@Override
	public void viewModeChanged (FileChooser.ViewMode newViewMode) {
		super.viewModeChanged(newViewMode);
		restartThumbnailGeneration();
	}

	private void restartThumbnailGeneration () {
		executor.shutdownNow();
		executor = Executors.newFixedThreadPool(MAX_THREADS);
		optimizeCache();
	}

	private Thumbnail getThumbnail (FileHandle file) {
		for (Thumbnail thumbnail : thumbnails) {
			if (thumbnail.file.equals(file)) {
				return thumbnail;
			}
		}

		return null;
	}

	private void optimizeCache () {
		if (thumbnails.size > MAX_CACHED) {
			for (int i = 0; i <= thumbnails.size - MAX_CACHED; i++) {
				thumbnails.get(i).dispose();
			}
			thumbnails.removeRange(0, thumbnails.size - MAX_CACHED);
		}
	}

	@Override
	public void dispose () {
		executor.shutdown();
		super.dispose();
	}

	private static class Thumbnail implements Disposable {
		private FileHandle file;
		private Texture textures[] = new Texture[3];
		private TextureRegionDrawable thumbs[] = new TextureRegionDrawable[3];

		public Thumbnail (FileHandle file) {
			this.file = file;
		}

		public void addThumb (FileChooser.ViewMode viewMode, Texture texture) {
			int index = -1;
			if (viewMode == FileChooser.ViewMode.SMALL_ICONS) index = 0;
			if (viewMode == FileChooser.ViewMode.MEDIUM_ICONS) index = 1;
			if (viewMode == FileChooser.ViewMode.BIG_ICONS) index = 2;

			textures[index] = texture;
			thumbs[index] = new TextureRegionDrawable(new TextureRegion(texture));
		}

		public Drawable getThumb (FileChooser.ViewMode viewMode) {
			if (viewMode == FileChooser.ViewMode.SMALL_ICONS) return thumbs[0];
			if (viewMode == FileChooser.ViewMode.MEDIUM_ICONS) return thumbs[1];
			if (viewMode == FileChooser.ViewMode.BIG_ICONS) return thumbs[2];
			return null;
		}

		@Override
		public void dispose () {
			if (textures[0] != null) textures[0].dispose();
			if (textures[1] != null) textures[1].dispose();
			if (textures[2] != null) textures[2].dispose();
		}
	}
}
