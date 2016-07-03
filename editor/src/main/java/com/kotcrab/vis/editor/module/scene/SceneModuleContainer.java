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

package com.kotcrab.vis.editor.module.scene;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.InvocationStrategy;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.entity.EntityScheme.UUIDPolicy;
import com.kotcrab.vis.editor.ext.anim.sprite.EditorSpriteAnimationUpdateSystem;
import com.kotcrab.vis.editor.module.Module;
import com.kotcrab.vis.editor.module.ModuleContainer;
import com.kotcrab.vis.editor.module.ModuleInput;
import com.kotcrab.vis.editor.module.editor.ClonerModule;
import com.kotcrab.vis.editor.module.editor.EditorModuleContainer;
import com.kotcrab.vis.editor.module.project.Project;
import com.kotcrab.vis.editor.module.project.ProjectExtensionStorageModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.scene.system.*;
import com.kotcrab.vis.editor.module.scene.system.inflater.*;
import com.kotcrab.vis.editor.module.scene.system.reloader.FontReloaderManager;
import com.kotcrab.vis.editor.module.scene.system.reloader.ParticleReloaderManager;
import com.kotcrab.vis.editor.module.scene.system.reloader.ShaderReloaderManager;
import com.kotcrab.vis.editor.module.scene.system.reloader.TextureReloaderManager;
import com.kotcrab.vis.editor.module.scene.system.render.AudioRenderSystem;
import com.kotcrab.vis.editor.module.scene.system.render.EditorParticleRenderSystem;
import com.kotcrab.vis.editor.module.scene.system.render.GridRendererSystem;
import com.kotcrab.vis.editor.module.scene.system.render.PointRenderSystem;
import com.kotcrab.vis.editor.plugin.api.EditorEntitySupport;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.SceneTab;
import com.kotcrab.vis.editor.util.BiHolder;
import com.kotcrab.vis.editor.util.vis.NoneInvocationStrategy;
import com.kotcrab.vis.editor.util.vis.SortedEntityEngineConfiguration;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.runtime.system.CameraManager;
import com.kotcrab.vis.runtime.system.DirtyCleanerSystem;
import com.kotcrab.vis.runtime.system.render.ParticleRenderSystem;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;
import com.kotcrab.vis.runtime.system.render.SpriteRenderSystem;
import com.kotcrab.vis.runtime.system.render.TextRenderSystem;
import com.kotcrab.vis.runtime.util.BootstrapInvocationStrategy;
import com.kotcrab.vis.runtime.util.EntityEngine;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import static com.kotcrab.vis.runtime.scene.SceneConfig.Priority.*;

/**
 * Module container for scene scope modules.
 * @author Kotcrab
 */
public class SceneModuleContainer extends ModuleContainer<SceneModule> implements ModuleInput {
	private static final NoneInvocationStrategy noneInvStrategy = new NoneInvocationStrategy();
	private static final InvocationStrategy stdInvStrategy = new InvocationStrategy();

	private Project project;
	private EditorModuleContainer editorModuleContainer;
	private ProjectModuleContainer projectModuleContainer;

	private Array<BiHolder<Object, Field>> delayedCompMapperToInject = new Array<>();

	private ClonerModule cloner;

	private EditorScene scene;
	private SceneTab sceneTab;

	private EntityEngine engine;
	private SortedEntityEngineConfiguration config;

	public SceneModuleContainer (ProjectModuleContainer projectMC, SceneTab sceneTab, EditorScene scene, Batch batch) {
		this.editorModuleContainer = projectMC.getEditorContainer();
		this.projectModuleContainer = projectMC;
		this.scene = scene;
		this.sceneTab = sceneTab;

		cloner = editorModuleContainer.get(ClonerModule.class);

		config = new SortedEntityEngineConfiguration();

		config.setSystem(new CameraManager(SceneViewport.SCREEN, 0, 0, scene.pixelsPerUnit), VIS_ESSENTIAL); //size ignored for screen viewport
		config.setSystem(new LayerManipulator(), VIS_ESSENTIAL);
		config.setSystem(new ZIndexManipulator(), VIS_ESSENTIAL);
		config.setSystem(new DirtySetterSystem(), VIS_ESSENTIAL);

		config.setSystem(new VisUUIDManager(), VIS_ESSENTIAL);
		config.setSystem(new EntityCounterManager(), VIS_ESSENTIAL);

		config.setSystem(new GridRendererSystem(batch, this), VIS_RENDERER.before());
		config.setSystem(new TextureReloaderManager(), VIS_RELOADER);
		config.setSystem(new ParticleReloaderManager(scene.pixelsPerUnit), VIS_RELOADER);
		config.setSystem(new FontReloaderManager(scene.pixelsPerUnit), VIS_RELOADER);
		config.setSystem(new ShaderReloaderManager(), VIS_RELOADER);

		config.setSystem(new EditorMusicInflater(), VIS_INFLATER);
		config.setSystem(new EditorParticleInflater(scene.pixelsPerUnit), VIS_INFLATER);
		config.setSystem(new EditorShaderInflater(), VIS_INFLATER);
		config.setSystem(new EditorSoundInflater(), VIS_INFLATER);
		config.setSystem(new EditorSpriteInflater(), VIS_INFLATER);
		config.setSystem(new EditorTextInflater(scene.pixelsPerUnit), VIS_INFLATER);

		config.setSystem(new EntitiesCollector(), NORMAL);
		config.setSystem(new GroupIdProviderSystem(), NORMAL);
		config.setSystem(new VisComponentManipulator(), NORMAL);
		config.setSystem(new EntityProxyCache(scene.pixelsPerUnit), NORMAL);
		createEssentialsSystems(config);

		RenderBatchingSystem batchingSystem = new RenderBatchingSystem(batch, true);
		config.setSystem(batchingSystem, VIS_RENDERER);


		//common render systems
		config.setSystem(new SpriteRenderSystem(batchingSystem), VIS_RENDERER);
		config.setSystem(new TextRenderSystem(batchingSystem, Assets.getDistanceFieldShader()), VIS_RENDERER);
		config.setSystem(new ParticleRenderSystem(batchingSystem, true), VIS_RENDERER);

		//entities sprites render systems
		config.setSystem(new AudioRenderSystem(batchingSystem, scene.pixelsPerUnit), VIS_RENDERER);
		config.setSystem(new PointRenderSystem(batchingSystem, scene.pixelsPerUnit), VIS_RENDERER);
		config.setSystem(new EditorParticleRenderSystem(batchingSystem, scene.pixelsPerUnit), VIS_RENDERER);
		config.setSystem(new EditorSpriteAnimationUpdateSystem(batchingSystem, scene.pixelsPerUnit), VIS_RENDERER);

		config.setSystem(new DirtyCleanerSystem(), VIS_LOW);

		for (EditorEntitySupport support : projectMC.get(ProjectExtensionStorageModule.class).getEntitySupports()) {
			support.registerSystems(config);
		}
	}

	public static void createEssentialsSystems (SortedEntityEngineConfiguration config) {
		config.setSystem(new AssetsUsageAnalyzer(), NORMAL);
	}

	public static void populateEngine (EntityEngine engine, ClonerModule cloner, EditorScene scene) {
		Array<EntityScheme> schemes = scene.getSchemes();
		schemes.forEach(entityScheme -> entityScheme.build(engine, cloner.getCloner(), UUIDPolicy.PRESERVE));
	}

	@Override
	protected boolean injectField (Object target, Field field, Class<?> type) throws ReflectiveOperationException {
		boolean alreadyInjected = super.injectField(target, field, type);
		if (alreadyInjected) return true;

		//artemis already handles injecting objects inside systems
		if (target instanceof BaseSystem) return false;

		if (BaseSystem.class.isAssignableFrom(type)) {
			field.setAccessible(true);
			field.set(target, engine != null ? engine.getSystem(type.asSubclass(BaseSystem.class)) : config.getSystem(type.asSubclass(BaseSystem.class)));
			return true;
		}

		if (ComponentMapper.class.isAssignableFrom(type)) {
			field.setAccessible(true);
			if (engine == null)
				delayedCompMapperToInject.add(new BiHolder<>(target, field));
			else
				injectComponentMapper(target, field);

			return true;
		}

		return false;
	}

	private void injectComponentMapper (Object target, Field field) throws ReflectiveOperationException {
		ParameterizedType mapperType = (ParameterizedType) field.getGenericType();
		field.set(target, engine.getMapper((Class<Component>) mapperType.getActualTypeArguments()[0]));
	}

	@Override
	public void add (SceneModule module) {
		module.setProject(projectModuleContainer.getProject());
		module.setProjectModuleContainer(projectModuleContainer);
		module.setContainer(editorModuleContainer);
		module.setSceneObjects(this, sceneTab, scene);

		if (module instanceof EntityEngineConfigurator)
			((EntityEngineConfigurator) module).setupEntityEngine(engine);

		super.add(module);
	}

	@Override
	public void init () {
		super.init();
		engine = new EntityEngine(config.build());

		modules.forEach(sceneModule -> sceneModule.setEntityEngine(engine));

		Log.debug("SceneModuleContainer", "Populating EntityEngine");
		populateEngine(engine, cloner, scene);

		engine.getSystems().forEach(this::injectModules);
		engine.setInvocationStrategy(new BootstrapInvocationStrategy());
		engine.process();
		engine.setInvocationStrategy(new InvocationStrategy());

		try {
			for (BiHolder<Object, Field> target : delayedCompMapperToInject) {
				injectComponentMapper(target.first, target.second);
			}
			delayedCompMapperToInject.clear();
			delayedCompMapperToInject = null;
		} catch (ReflectiveOperationException e) {
			Log.exception(e);
		}
	}

	@Override
	public <C extends Module> C findInHierarchy (Class<C> moduleClass) {
		C module = getOrNull(moduleClass);
		if (module != null) return module;

		return projectModuleContainer.findInHierarchy(moduleClass);
	}

	public SceneTab getSceneTab () {
		return sceneTab;
	}

	public EditorScene getScene () {
		return scene;
	}

	public Project getProject () {
		return project;
	}

	public void setProject (Project project) {
		if (getModuleCounter() > 0)
			throw new IllegalStateException("Project can't be changed while modules are loaded!");

		this.project = project;
	}

	@Override
	public void resize () {
		super.resize();
		engine.getSystem(CameraManager.class).resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void render (Batch batch) {
		engine.setDelta(Gdx.graphics.getDeltaTime());
		engine.process();

		for (int i = 0; i < modules.size; i++)
			modules.get(i).render(batch);
	}

	/**
	 * Must be called after changing entity components and those changes must be instantly reflected in entity. Will
	 * also result in notify all subscribers of entities changes.
	 */
	public void updateEntitiesStates () {
		engine.setInvocationStrategy(noneInvStrategy);
		engine.process();
		engine.setInvocationStrategy(stdInvStrategy);
	}

	public void onShow () {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).onShow();
	}

	public void onHide () {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).onHide();
	}

	public void save () {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).save();
	}

	public EntityEngine getEntityEngine () {
		if (engine == null) throw new IllegalStateException("SceneModuleContainer wasn't initialized yet!");
		return engine;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).touchDown(event, x, y, pointer, button)) returnValue = true;

		return returnValue;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).touchUp(event, x, y, pointer, button);
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).touchDragged(event, x, y, pointer);
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).mouseMoved(event, x, y)) returnValue = true;

		return returnValue;
	}

	@Override
	public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).enter(event, x, y, pointer, fromActor);
	}

	@Override
	public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).exit(event, x, y, pointer, toActor);
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).scrolled(event, x, y, amount)) returnValue = true;

		return returnValue;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).keyDown(event, keycode)) returnValue = true;

		return returnValue;
	}

	@Override
	public boolean keyUp (InputEvent event, int keycode) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).keyUp(event, keycode)) returnValue = true;

		return returnValue;
	}

	@Override
	public boolean keyTyped (InputEvent event, char character) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).keyTyped(event, character)) returnValue = true;

		return returnValue;
	}
}
