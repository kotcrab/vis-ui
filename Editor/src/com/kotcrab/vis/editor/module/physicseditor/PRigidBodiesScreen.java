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

package com.kotcrab.vis.editor.module.physicseditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.event.bus.Event;
import com.kotcrab.vis.editor.event.bus.EventListener;
import com.kotcrab.vis.editor.event.TexturesReloadedEvent;
import com.kotcrab.vis.editor.module.ModuleInput;
import com.kotcrab.vis.editor.module.physicseditor.PModeController.Mode;
import com.kotcrab.vis.editor.module.physicseditor.input.CreationInputProcessor;
import com.kotcrab.vis.editor.module.physicseditor.input.EditionInputProcessor;
import com.kotcrab.vis.editor.module.physicseditor.input.TestInputProcessor;
import com.kotcrab.vis.editor.module.physicseditor.list.ChangeListener;
import com.kotcrab.vis.editor.module.physicseditor.list.ObservableList;
import com.kotcrab.vis.editor.module.physicseditor.models.CircleModel;
import com.kotcrab.vis.editor.module.physicseditor.models.PolygonModel;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;
import com.kotcrab.vis.editor.module.physicseditor.models.ShapeModel;
import com.kotcrab.vis.editor.module.physicseditor.util.ShapeUtils;
import com.kotcrab.vis.editor.module.project.FileAccessModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class PRigidBodiesScreen extends PhysicsEditorModule implements EventListener {

	private FileAccessModule fileAccess;

	private PRenderer renderer;
	private PCameraModule cameraModule;
	private PModeController modeController;
	private PhysicsEditorSettings settings;

	private final Box2DDebugRenderer debugRdr = new Box2DDebugRenderer();
	private float timeAcc = 0;

	private final List<Sprite> ballsSprites = new ArrayList<Sprite>();
	private final List<Body> ballsBodies = new ArrayList<Body>();
	private final World world = new World(new Vector2(0, 0), true);
	private Sprite bodySprite;

	private RigidBodyModel selectedModel;

	private ModuleInput creationInputProcessor;
	private ModuleInput editionInputProcessor;
	private ModuleInput testInputProcessor;
	private ModuleInput currentProcessor;
	private ChangeListener selectedModelChangeListener;

	public final ObservableList<Vector2> selectedPoints = new ObservableList<Vector2>();
	public Vector2 nextPoint;
	public Vector2 nearestPoint;
	public Vector2 mouseSelectionP1;
	public Vector2 mouseSelectionP2;
	public Vector2 ballThrowP1;
	public Vector2 ballThrowP2;

	@Override
	public boolean onEvent (Event event) {
		if (event instanceof TexturesReloadedEvent)
			createBodySprite();

		return false;
	}

	@Override
	public void init () {
		fileAccess = projectContainer.get(FileAccessModule.class);

		renderer = physicsContainer.get(PRenderer.class);
		cameraModule = physicsContainer.get(PCameraModule.class);
		modeController = physicsContainer.get(PModeController.class);
		settings = physicsContainer.get(PSettingsModule.class).getSettings();

		//dummy processor if no body is selected
		currentProcessor = new ModuleInput() {
		};
		creationInputProcessor = new CreationInputProcessor(cameraModule, this, settings);
		editionInputProcessor = new EditionInputProcessor(cameraModule, this, settings);
		testInputProcessor = new TestInputProcessor(cameraModule, this);

		initializeModelChangeListener();
		initializeSelectedPointsEvents();

		App.eventBus.register(this);

		modeController.setListener(mode -> {
			selectedPoints.clear();
			nextPoint = null;
			nearestPoint = null;

			if (mode == null) {

			} else {

				switch (mode) {
					case CREATION:
						currentProcessor = creationInputProcessor;
						break;

					case EDITION:
						currentProcessor = editionInputProcessor;
						break;

					case TEST:
						currentProcessor = testInputProcessor;
						break;
				}
			}
		});
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	public RigidBodyModel getSelectedModel () {
		return selectedModel;
	}

	private void initializeModelChangeListener () {
		selectedModelChangeListener = new ChangeListener() {
			@Override
			public void propertyChanged (Object source, String propertyName) {
				if (propertyName.equals(RigidBodyModel.PROP_IMAGEPATH)) {
					createBodySprite();
				} else if (propertyName.equals(RigidBodyModel.PROP_PHYSICS)) {
					clearWorld();
					createBody();
				}
			}
		};

//		bodies.addChangeListener(new ChangeListener() {
//			private RigidBodyModel oldModel;
//
//			@Override
//			public void propertyChanged (Object source, String propertyName) {
//				if (propertyName.equals(PRigidBodiesManagerModule.PROP_SELECTION)) {
//					RigidBodyModel model = selectedModel;
//
//					setMode(model != null ? mode == null ? Mode.CREATION : mode : null);
//					resetWorld();
//
//					if (model != null) model.addChangeListener(selectedModelChangeListener);
//					if (oldModel != null) oldModel.removeChangeListener(selectedModelChangeListener);
//					oldModel = model;
//				}
//			}
//		});
	}

	public void switchedSelection (RigidBodyModel model) {
		modeController.setMode(model != null ? (modeController.getMode() == null ? Mode.CREATION : modeController.getMode()) : null);
		resetWorld();

		if (model != null) model.addChangeListener(selectedModelChangeListener);
		if (selectedModel != null) selectedModel.removeChangeListener(selectedModelChangeListener);
		selectedModel = model;
		createBodySprite();
	}

	private void initializeSelectedPointsEvents () {
		selectedPoints.addListChangedListener((source, added, removed) -> {
			RigidBodyModel model = selectedModel;
			if (model == null) return;

			List<Vector2> toAdd = new ArrayList<>();

			for (Vector2 v : added) {
				ShapeModel shape = ShapeUtils.getShape(model, v);
				if (shape == null) continue;

				if (shape.getType() == ShapeModel.Type.CIRCLE) {
					Array<Vector2> vs = shape.getVertices();
					if (selectedPoints.contains(vs.get(0)) && !selectedPoints.contains(vs.get(1))) {
						toAdd.add(vs.get(1));
					}
				}
			}

			selectedPoints.addAll(toAdd);
		});
	}

	// -------------------------------------------------------------------------
	// Render
	// -------------------------------------------------------------------------

	@Override
	public void render (Batch batch) {
		batch.end();
		while (timeAcc < Gdx.graphics.getDeltaTime()) {
			timeAcc += 1f / 60;
			world.step(1f / 60, 10, 10);
		}

		timeAcc -= Gdx.graphics.getDeltaTime();

		renderer.drawBoundingBox(bodySprite);

		batch.setProjectionMatrix(cameraModule.getCamera().combined);
		batch.begin();
		if (bodySprite != null && settings.isImageDrawn) bodySprite.draw(batch);
		for (int i = 0; i < ballsSprites.size(); i++) {
			Sprite sp = ballsSprites.get(i);
			Vector2 pos = ballsBodies.get(i).getPosition();
			float angle = ballsBodies.get(i).getAngle() * MathUtils.radiansToDegrees;
			sp.setPosition(pos.x - sp.getWidth() / 2, pos.y - sp.getHeight() / 2);
			sp.setRotation(angle);
			sp.draw(batch);
		}
		batch.end();

		renderer.drawModel(selectedModel, selectedPoints, nextPoint, nearestPoint);
		renderer.drawGrid();
		renderer.drawMouseSelection(mouseSelectionP1, mouseSelectionP2);
		renderer.drawBallThrowPath(ballThrowP1, ballThrowP2);

		if (settings.isPhysicsDebugEnabled) {
			debugRdr.render(world, cameraModule.getCamera().combined);
		}
		batch.begin();
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void buildBody () {
		clearWorld();
		createBody();
	}

	public void fireBall (Vector2 orig, Vector2 force) {
		createBall(orig, force);
	}

	public void insertPointsBetweenSelected () {
		if (!isInsertEnabled()) return;

		List<Vector2> toAdd = new ArrayList<Vector2>();

		for (ShapeModel shape : selectedModel.getShapes()) {
			if (shape.getType() != ShapeModel.Type.POLYGON) continue;

			Array<Vector2> vs = shape.getVertices();

			for (int i = 0; i < vs.size; i++) {
				Vector2 p1 = vs.get(i);
				Vector2 p2 = i != vs.size - 1 ? vs.get(i + 1) : vs.get(0);

				if (selectedPoints.contains(p1) && selectedPoints.contains(p2)) {
					Vector2 p = new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
					vs.insert(i + 1, p);
					toAdd.add(p);
				}
			}
		}

		selectedPoints.addAll(toAdd);
		selectedModel.computePhysics(settings.polygonizer);
	}

	public void removeSelectedPoints () {
		if (!isRemoveEnabled()) return;

		Array<ShapeModel> shapes = selectedModel.getShapes();

		for (int i = shapes.size - 1; i >= 0; i--) {
			ShapeModel shape = selectedModel.getShapes().get(i);

			switch (shape.getType()) {
				case POLYGON:
					for (Vector2 p : selectedPoints) {
						if (shape.getVertices().contains(p, true)) shape.getVertices().removeValue(p, true);
					}
					if (shape.getVertices().size == 0) shapes.removeIndex(i);
					break;

				case CIRCLE:
					for (Vector2 p : selectedPoints) {
						if (shape.getVertices().contains(p, true)) {
							shapes.removeIndex(i);
							break;
						}
					}
					break;
			}
		}

		selectedPoints.clear();
		selectedModel.computePhysics(settings.polygonizer);
	}

	private boolean isInsertEnabled () {
		RigidBodyModel model = selectedModel;

		if (model == null) return false;
		if (selectedPoints.size() <= 1) return false;

		for (ShapeModel shape : model.getShapes()) {
			if (shape.getType() != ShapeModel.Type.POLYGON) continue;

			Vector2 v1 = null;
			for (Vector2 v2 : shape.getVertices()) {
				if (v1 != null && selectedPoints.contains(v2)) return true;
				v1 = selectedPoints.contains(v2) ? v2 : null;
			}
			if (v1 != null && selectedPoints.contains(shape.getVertices().get(0))) return true;
		}

		return false;
	}

	private boolean isRemoveEnabled () {
		if (selectedModel == null) return false;
		return !selectedPoints.isEmpty();
	}

	private boolean isImageValid () {
		RigidBodyModel model = selectedModel;
		if (model == null) return false;
		//if (model.getAssetDescriptor() == null) return false;
		return true;
	}

	public void autoTrace () {
		if (!isImageValid()) return;

		RigidBodyModel model = selectedModel;
		//PathAsset path = (PathAsset) model.getAssetDescriptor(); //FIXME physics editor resource loading
		//File file = new File(fileAccess.derelativizeFromAssetsFolder(path.getPath()));
//		Vector2[][] polygons = Tracer.trace(file.getPath(),
//				settings.autoTraceHullTolerance,
//				settings.autoTraceAlphaTolerance,
//				settings.autoTraceMultiPartDetection,
//				settings.autoTraceHoleDetection);

//		if (polygons == null) return;
//
//		for (Vector2[] polygon : polygons) {
//			if (polygon.length < 3) continue;
//			ShapeModel shape = new ShapeModel(ShapeModel.Type.POLYGON);
//			shape.getVertices().addAll(polygon);
//			shape.close();
//			model.getShapes().add(shape);
//		}

		model.computePhysics(settings.polygonizer);
		buildBody();
	}

	public void recomputePhysics () {
		selectedModel.computePhysics(settings.polygonizer);
	}

	private void clearPoints () {
		if (selectedModel == null) return;
		selectedPoints.clear();
		selectedModel.clear();
	}

	private void clearWorld () {
		ballsBodies.clear();
		ballsSprites.clear();
		Array<Body> bodiesList = new Array<>();
		world.getBodies(bodiesList);
		Iterator<Body> bodies = bodiesList.iterator();
		while (bodies.hasNext()) world.destroyBody(bodies.next());
	}

	private void createBody () {
		RigidBodyModel model = selectedModel;
		if (model == null) return;
		if (model.getPolygons().size == 0 && model.getCircles().size == 0) return;

		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;

		Body body = world.createBody(bd);

		for (PolygonModel polygon : model.getPolygons()) {
			Vector2[] vs = polygon.vertices.toArray(new Vector2[0]);

			PolygonShape shape = new PolygonShape();
			shape.set(vs);

			FixtureDef fd = new FixtureDef();
			fd.density = 1f;
			fd.friction = 0.5f;
			fd.restitution = 1f;
			fd.shape = shape;

			body.createFixture(fd);
			shape.dispose();
		}

		for (CircleModel circle : model.getCircles()) {
			CircleShape shape = new CircleShape();
			shape.setPosition(circle.center);
			shape.setRadius(circle.radius);

			FixtureDef fd = new FixtureDef();
			fd.density = 1f;
			fd.friction = 0.5f;
			fd.restitution = 1f;
			fd.shape = shape;

			body.createFixture(fd);
			shape.dispose();
		}
	}

	private void createBodySprite () {
		bodySprite = null;

		RigidBodyModel model = selectedModel;
		if (model == null) return;

		TextureRegion region = model.region;
		if (region == null) return;

		bodySprite = new Sprite(region);
		bodySprite.setPosition(0, 0);
		bodySprite.setColor(1, 1, 1, 0.5f);

		float spRatio = bodySprite.getWidth() / bodySprite.getHeight();
		bodySprite.setSize(1, 1 / spRatio);
	}

	private void createBall (Vector2 orig, Vector2 force) {
		Random rand = new Random();
		float radius = rand.nextFloat() * 0.02f + 0.02f;

		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 0.5f;
		bd.linearDamping = 0.5f;
		bd.position.set(orig);
		bd.angle = rand.nextFloat() * MathUtils.PI;

		Body b = world.createBody(bd);
		b.applyLinearImpulse(force, orig, true);

		ballsBodies.add(b);

		CircleShape shape = new CircleShape();
		shape.setRadius(radius);

		FixtureDef fd = new FixtureDef();
		fd.density = 1f;
		fd.friction = 0.5f;
		fd.restitution = 1f;
		fd.shape = shape;

		b.createFixture(fd);

		Sprite sp = new Sprite(Assets.getMiscRegion("ball"));
		sp.setSize(radius * 2, radius * 2);
		sp.setOrigin(sp.getWidth() / 2, sp.getHeight() / 2);
		ballsSprites.add(sp);
	}

	private void resetWorld () {
		bodySprite = null;
		clearWorld();

		RigidBodyModel model = selectedModel;
		if (model == null) return;

		createBody();
		createBodySprite();
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		return currentProcessor.touchDown(event, x, y, pointer, button);
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		currentProcessor.touchUp(event, x, y, pointer, button);
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		currentProcessor.touchDragged(event, x, y, pointer);
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		return currentProcessor.mouseMoved(event, x, y);
	}

	@Override
	public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
		currentProcessor.enter(event, x, y, pointer, fromActor);
	}

	@Override
	public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
		currentProcessor.exit(event, x, y, pointer, toActor);
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		return currentProcessor.scrolled(event, x, y, amount);
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		return currentProcessor.keyDown(event, keycode);
	}

	@Override
	public boolean keyUp (InputEvent event, int keycode) {
		return currentProcessor.keyUp(event, keycode);
	}

	@Override
	public boolean keyTyped (InputEvent event, char character) {
		return currentProcessor.keyTyped(event, character);
	}

}
