package pl.kotcrab.vis.sceneeditor;

public class EditorState
{
	public boolean devMode;
	public boolean editing;
	public boolean dirty;
	public boolean cameraLocked;
	public boolean hideOutlines;
	public boolean exitingEditMode; // when exiting edit mode and changes are not saved
}