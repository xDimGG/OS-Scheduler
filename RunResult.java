/**
 * Used to track the runtime of a process and other userland data
 */
public class RunResult {
	public boolean ranToTimeout;
	public int millisecondsUsed;

	RunResult(int _millisecondsUsed, boolean _ranToTimeout) {
		millisecondsUsed = _millisecondsUsed;
		ranToTimeout = _ranToTimeout;
	}
}
