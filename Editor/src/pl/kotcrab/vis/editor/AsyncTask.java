
package pl.kotcrab.vis.editor;

public abstract class AsyncTask {
	private Thread thread;
	private Runnable runnable;

	private int progressPercent;
	private String message;

	private AsyncTaskListener listener;

	public AsyncTask (String threadName) {
		runnable = new Runnable() {
			@Override
			public void run () {
				execute();
				if (listener != null) listener.finished();
			}
		};
		thread = new Thread(runnable, threadName);
	}

	public void start () {
		thread.start();
	}

	public void failed (String reason) {
		if (listener != null) listener.failed(reason);
	}

	public abstract void execute ();

	public int getProgressPercent () {
		return progressPercent;
	}

	public void setProgressPercent (int progressPercent) {
		this.progressPercent = progressPercent;
		if (listener != null) listener.progressChanged(progressPercent);
	}

	public void setRunnable (Runnable runnable) {
		this.runnable = runnable;
	}

	public void setMessage (String message) {
		this.message = message;
		if (listener != null) listener.messageChanged(message);
	}

	public String getMessage () {
		return message;
	}

	public void setListener (AsyncTaskListener listener) {
		this.listener = listener;
	}
}
