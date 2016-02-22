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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.PolygonTool;
import com.kotcrab.vis.editor.ui.WindowResultListener;
import com.kotcrab.vis.editor.util.polygon.Tracer;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTextButton;

/** @author Kotcrab */
public class PolygonAutoTraceDialog extends BaseDialog {
	private Stage stage;
	private TextureCacheModule textureCacheModule;

	private VisSlider hullTolerance;
	private VisSlider alphaTolerance;

	private VisAssetDescriptor assetDescriptor;
	private final WindowResultListener<Vector2[]> resultListener;

	/** @param resultListener cords values are between 0..1 */
	public PolygonAutoTraceDialog (ModuleInjector injector, VisAssetDescriptor assetDescriptor, WindowResultListener<Vector2[]> resultListener) {
		super("Polygon Auto Trace");
		this.assetDescriptor = assetDescriptor;
		injector.injectModules(this);

		this.resultListener = resultListener;
		init();
	}

	@Override
	protected void createUI () {
		defaults().left();
		left();

		hullTolerance = new VisSlider(100, 400, 10, false);
		hullTolerance.setValue(250);
		alphaTolerance = new VisSlider(0, 255, 5, false);
		alphaTolerance.setValue(128);

		add(new VisLabel("Hull Tolerance"));
		add(hullTolerance);
		add().growX().row();
		add(new VisLabel("Alpha Tolerance"));
		add(alphaTolerance);
		add().growX().row();
		add(new VisLabel("Auto tracer does not support multi-part images,\nnote that creating points manually will be more precise.")).colspan(3).row();

		VisTextButton traceButton = new VisTextButton("Trace");
		VisTextButton closeButton = new VisTextButton("Close");

		add(TableBuilder.build(traceButton, closeButton)).colspan(3).right();

		traceButton.addListener(new VisChangeListener((event, actor) -> tracePolygon()));
		closeButton.addListener(new VisChangeListener((event, actor) -> fadeOut()));
	}

	private void tracePolygon () {
		TextureRegion region = textureCacheModule.getRegion(assetDescriptor);
		int width = region.getRegionWidth();
		int height = region.getRegionHeight();
		int[] pixelArray = new int[width * height];

		Texture texture = region.getTexture();
		if (texture.getTextureData().isPrepared() == false) {
			texture.getTextureData().prepare();
		}

		Pixmap pixmap = texture.getTextureData().consumePixmap();

		for (int x = 0; x < region.getRegionWidth(); x++) {
			for (int y = 0; y < region.getRegionHeight(); y++) {
				pixelArray[x + y * width] = pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y);
			}
		}

		Vector2[][] vertices = Tracer.trace(pixelArray, width, height, hullTolerance.getValue(), (int) alphaTolerance.getValue());

		if (vertices == null || vertices.length == 0) {
			Dialogs.showErrorDialog(stage, "Auto tracer could not create polygon, please create points manually.");
			Log.warn(PolygonTool.TAG, "Failed to auto trace polygon for asset descriptor: " + assetDescriptor);
			fadeOut();
			return;
		}

		if (vertices.length > 1) {
			Log.warn(PolygonTool.TAG, "Auto tracer found multiple parts, will be discarded. Asset descriptor: " + assetDescriptor);
		}

		resultListener.finished(vertices[0]);
	}
}
