package pl.kotcrab.vis.sceneeditor;

import pl.kotcrab.vis.sceneeditor.support.CheckBoxSupport;
import pl.kotcrab.vis.sceneeditor.support.LabelSupport;
import pl.kotcrab.vis.sceneeditor.support.ListSupport;
import pl.kotcrab.vis.sceneeditor.support.ProgressBarSupport;
import pl.kotcrab.vis.sceneeditor.support.SelectBoxSupport;
import pl.kotcrab.vis.sceneeditor.support.TextButtonSupport;
import pl.kotcrab.vis.sceneeditor.support.TouchpadSupport;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class StageSceneEditor extends SceneEditor
{
	private Stage stage;
	
	public StageSceneEditor (Stage stage, boolean devMode) {
		super((OrthographicCamera)stage.getCamera(), devMode);
		
		this.stage = stage;
	}
	
	public StageSceneEditor (FileHandle sceneFile, Stage stage, boolean devMode) {
		super(sceneFile, (OrthographicCamera)stage.getCamera(), devMode);
		
		this.stage = stage;
	}
	
	public StageSceneEditor add (Actor obj, String identifier) {
		super.add(obj, identifier);
		stage.addActor(obj);
		return this;
	}
	
	public void registerScene2dUISupports()
	{
		registerSupport(new LabelSupport());
		registerSupport(new TextButtonSupport());
		registerSupport(new CheckBoxSupport());
		registerSupport(new ProgressBarSupport());
		registerSupport(new ListSupport());
		registerSupport(new SelectBoxSupport());
		registerSupport(new TouchpadSupport());
	}
}