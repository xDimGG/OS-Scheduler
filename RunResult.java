import java.util.Date;

/**
 * Used to track the runtime of a process and other userland data
 */
public class RunResult {
	public boolean ranToTimeout;
	public int millisecondsUsed;
	private long _start;

	RunResult() {
		this._start = (new Date()).getTime();
	}

	private void calcRuntime() {
		this.millisecondsUsed = (int) ((new Date()).getTime() - this._start);
	}

	public RunResult complete() {
		this.calcRuntime();
		this.ranToTimeout = true;
		return this;
	}

	public RunResult pause() {
		this.calcRuntime();
		this.ranToTimeout = false;
		return this;
	}
}
