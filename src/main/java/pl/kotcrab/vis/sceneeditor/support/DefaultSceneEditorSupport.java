package pl.kotcrab.vis.sceneeditor.support;

public abstract class DefaultSceneEditorSupport<O> implements SceneEditorSupport<O>
{
	@Override
	public String getIdentifier()
	{
		return getSupportedClass().getName();
	}
}