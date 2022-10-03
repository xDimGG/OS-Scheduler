/**
 * Security layer built on top of userland processes
 */
public class KernelandProcess {
	public UserlandProcess userlandProcess;
	public int pid;
	public int sleepUntil;
	public PriorityEnum priority;
	public int timesRanToTimeoutConsecutively = 0;

	KernelandProcess(UserlandProcess _userlandProcess, int _pid, PriorityEnum _priority) {
		this.userlandProcess = _userlandProcess;
		this.pid = _pid;
		this.priority = _priority;
	}
}
