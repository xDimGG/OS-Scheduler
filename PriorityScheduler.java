import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

public class PriorityScheduler implements OSInterface {
	private int nextPID = 1;
	private HashMap<Integer, KernelandProcess> processes = new HashMap<>();
	private int _processToSleep = 0;
	private int clock = 0; // OS Time
	private Queue<KernelandProcess> realtimeQueue = new LinkedList<>();
	private Queue<KernelandProcess> interactiveQueue = new LinkedList<>();
	private Queue<KernelandProcess> backgroundQueue = new LinkedList<>();
	private LinkedList<KernelandProcess> waitList  = new LinkedList<>();

	private Queue<KernelandProcess> enumToQueue(PriorityEnum priority) {
		if (priority == PriorityEnum.RealTime) return realtimeQueue;
		if (priority == PriorityEnum.Interactive) return interactiveQueue;
		if (priority == PriorityEnum.Background) return backgroundQueue;
		return null;
	}

	// Brute force random queue selection
	// Not efficient by any metrics
	private PriorityEnum selectPriority() {
		// Pick a priority queue
		double x = Math.random();
		PriorityEnum q = null;
		if (x >= 0.4) q = PriorityEnum.RealTime;
		else if (x >= 0.1) q = PriorityEnum.Interactive;
		else q = PriorityEnum.Background;
		// Check if there is anything in that priority queue
		if (!enumToQueue(q).isEmpty()) return q;
		// Try again if queue is empty
		return selectPriority();
	}

	/**
	 * Returns the process ID of the new process
	 */
	@Override
	public int CreateProcess(UserlandProcess myNewProcess, PriorityEnum priority) {
		KernelandProcess kp = new KernelandProcess(myNewProcess, nextPID, priority);
		nextPID++;
		processes.put(kp.pid, kp);
		enumToQueue(priority).add(kp);
		return kp.pid;
	}

	/**
	 * Returns true if the process existed and was deleted
	 */
	@Override
	public boolean DeleteProcess(int processId) {
		KernelandProcess kp = processes.get(processId);
		if (kp == null) return false;
		if (!enumToQueue(kp.priority).remove(kp)) { // Remove process from run queue
			waitList.remove(kp); // If nothing was removed, remove it from the wait list
		}
		processes.remove(processId); // Remove process from process list
		return true;
	}

	@Override
	public void Sleep(int milliseconds) {
		_processToSleep = milliseconds;
	}

	@Override
	public void run() {
		// Endless run loop
		while (true) {
			// Check for any waiting processes
			ListIterator<KernelandProcess> iter = waitList.listIterator();
			while (iter.hasNext()) {
				KernelandProcess el = iter.next();
				if (el.sleepUntil <= clock) {
					enumToQueue(el.priority).add(el);
					iter.remove();
				}
			}

			// If all queues are empty advance the clock until there is something
			if (realtimeQueue.isEmpty() && interactiveQueue.isEmpty() && backgroundQueue.isEmpty()) {
				clock++;
				continue;
			}

			KernelandProcess kp = enumToQueue(selectPriority()).remove();
			RunResult rr = kp.userlandProcess.run();
			clock += rr.millisecondsUsed;
			if (rr.ranToTimeout) kp.timesRanToTimeoutConsecutively++;
			else kp.timesRanToTimeoutConsecutively = 0;

			if (kp.timesRanToTimeoutConsecutively >= 5) {
				kp.timesRanToTimeoutConsecutively = 0;
				if (kp.priority == PriorityEnum.RealTime) {
					kp.priority = PriorityEnum.Interactive;
				} else if (kp.priority == PriorityEnum.Interactive) {
					kp.priority = PriorityEnum.Background;
				}
			}

			Boolean requeue = true;

			// Act on sleep call
			if (_processToSleep > 0) {
				kp.sleepUntil = clock + _processToSleep;
				waitList.add(kp);
				requeue = false;
			}

			_processToSleep = 0;

			if (requeue) {
				enumToQueue(kp.priority).add(kp);
			}
		}
	}
}
