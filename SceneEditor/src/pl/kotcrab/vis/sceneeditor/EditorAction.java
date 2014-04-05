package pl.kotcrab.vis.sceneeditor;

class EditorAction
{
	public Object obj;
	public ActionType type;
	public float xDiff;
	public float yDiff;
	
	public EditorAction(Object obj, ActionType type, float xDiff, float yDiff)
	{
		this.obj = obj;
		this.type = type;
		this.xDiff = xDiff;
		this.yDiff = yDiff;
	}
}

enum ActionType
{
	POS, SIZE, SCALE, ORIGIN, ROTATION
}