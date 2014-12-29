/**
 * Copyright 2014 Pawel Pastuszak
 * 
 * This file is part of VisEditor.
 * 
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.kotcrab.vis.editor.ui.scene;

import pl.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import pl.kotcrab.vis.editor.module.scene.EditorScene;
import pl.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import pl.kotcrab.vis.editor.ui.tab.TabAdapater;
import pl.kotcrab.vis.editor.ui.tab.TabViewMode;
import pl.kotcrab.vis.ui.VisTable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class SceneTab extends TabAdapater {
	private EditorScene scene;

	private OrthographicCamera camera;

	private SceneModuleContainer moduleContainer;

	private VisTable content;
	private VisTable leftColumn;
	private VisTable rightColumn;

	private SceneOutline outline;
	private ActorProperites actorProperties;

	public SceneTab (EditorScene scene, ProjectModuleContainer projectMC) {
		this.scene = scene;

		moduleContainer = new SceneModuleContainer(projectMC);
		moduleContainer.init();

		camera = new OrthographicCamera();

		outline = new SceneOutline();
		actorProperties = new ActorProperites();

		content = new VisTable(false) {
			@Override
			protected void sizeChanged () {
				super.sizeChanged();
				moduleContainer.resize();
				resize();
			}
		};

		leftColumn = new VisTable(false);
		rightColumn = new VisTable(false);

		leftColumn.top();
		rightColumn.top();

		content.add(leftColumn).width(300).expandY().fillY();
		content.add().fill().expand();
		content.add(rightColumn).width(300).expandY().fillY();

		leftColumn.top();
		leftColumn.add(outline).height(300).expandX().fillX();

		rightColumn.top();
		rightColumn.add(actorProperties).height(300).expandX().fillX();
	}

	private void resize () {
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void render (Batch batch) {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}

	@Override
	public String getButtonText () {
		return scene.getFile().name();
	}

	@Override
	public Table getContentTable () {
		return content;
	}

	@Override
	public TabViewMode getViewMode () {
		return TabViewMode.WITH_PROJECT_ASSETS_MANAGER;
	}

	public EditorScene getScene () {
		return scene;
	}
}
