import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

public class BasicScheduler {
	private int nextPID = 1;
	private HashMap<Integer, KernelandProcess> processes = new HashMap<>();
	private int clock = 0; // OS Time
	private Queue<KernelandProcess> runQueue = new LinkedList<>();
	private LinkedList<KernelandProcess> waitList  = new LinkedList<>();

	/**
	 * Returns the process ID of the new process
	 */
	public int CreateProcess(UserlandProcess myNewProcess) {
		KernelandProcess kp = new KernelandProcess(myNewProcess, nextPID, null);
		nextPID++;
		processes.put(kp.pid, kp);
		return kp.pid;
	}

	/**
	 * Returns true if the process existed and was deleted
	 */
	public boolean DeleteProcess(int processId) {
		return processes.remove(processId) != null;
	}

	/**
	 * Run method that loops forever
	 */
	public void run() {
		// Fill the run queue
		for (KernelandProcess kp : processes.values()) {
			runQueue.add(kp);
		}

		// Endless run loop
		while (true) {
			// Check for any waiting processes
			ListIterator<KernelandProcess> iter = waitList.listIterator();
			while (iter.hasNext()) {
				KernelandProcess el = iter.next();
				if (el.sleepUntil <= clock) {
					runQueue.add(el);
					iter.remove();
				}
			}

			// If there is no element advance the clock until there is
			if (runQueue.isEmpty()) {
				clock++;
				continue;
			}

			KernelandProcess kp = runQueue.remove();
			OS.clearOSCalls();
			RunResult rr = kp.userlandProcess.run();
			clock += rr.millisecondsUsed;

			// Act on any OS calls
			boolean requeue = true;
			var calls = OS.getOSCalls();
			while (calls.hasNext()) {
				OSCall call = calls.next();
				if (call.fn == OSCallFn.SLEEP) {
					kp.sleepUntil = clock + call.intArg0;
					waitList.add(kp);
					requeue = false;
				}
			}

			if (requeue) {
				runQueue.add(kp);
			}
		}
	}
}
