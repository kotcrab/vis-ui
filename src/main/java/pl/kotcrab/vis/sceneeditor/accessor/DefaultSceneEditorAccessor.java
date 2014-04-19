package pl.kotcrab.vis.sceneeditor.accessor;

public abstract class DefaultSceneEditorAccessor<O> implements SceneEditorAccessor<O>
{
	@Override
	public String getIdentifier()
	{
		return getSupportedClass().getName();
	}
}