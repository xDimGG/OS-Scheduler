import java.util.LinkedList;

/**
 * Security layer built on top of userland processes
 */
public class KernelandProcess {
	public UserlandProcess userlandProcess;
	public int pid;
	public int sleepUntil;
	public PriorityEnum priority;
	public int timesRanToTimeoutConsecutively = 0;
	public LinkedList<Integer> openDevices = new LinkedList<>();
	public int[] pages = new int[MemoryManagement.PAGE_COUNT];

	KernelandProcess(UserlandProcess _userlandProcess, int _pid, PriorityEnum _priority) {
		this.userlandProcess = _userlandProcess;
		this.pid = _pid;
		this.priority = _priority;
		for (int i = 0; i < pages.length; i++) {
			pages[i] = -1;
		}
	}
}
