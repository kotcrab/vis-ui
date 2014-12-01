package pl.kotcrab.vis.editor;

public interface AsyncTaskListener
{
	public void messageChanged(String newMsg);
	public void progressChanged(int newProgressPercent);
	public void finished();
	public void failed(String reason);
}