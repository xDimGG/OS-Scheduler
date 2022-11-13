import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PriorityScheduler implements OSInterface {
	private int nextPID = 1;
	private HashMap<Integer, KernelandProcess> processes = new HashMap<>();
	private int _processToSleep = 0;
	private KernelandProcess _currentProcess;
	private int clock = 0; // OS Time
	private VFS vfs = new VFS();
	private LinkedList<KernelandProcess> realtimeQueue = new LinkedList<>();
	private LinkedList<KernelandProcess> interactiveQueue = new LinkedList<>();
	private LinkedList<KernelandProcess> backgroundQueue = new LinkedList<>();
	private LinkedList<KernelandProcess> waitList  = new LinkedList<>();
	private MemoryManagement mem;

	PriorityScheduler() {
		mem = new MemoryManagement(this);
	}

	public VFS getVFS() {
		return vfs;
	}

	private Queue<KernelandProcess> enumToQueue(PriorityEnum priority) {
		if (priority == PriorityEnum.RealTime) return realtimeQueue;
		if (priority == PriorityEnum.Interactive) return interactiveQueue;
		if (priority == PriorityEnum.Background) return backgroundQueue;
		return null;
	}

	public KernelandProcess getCurrentProcess() {
		return _currentProcess;
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

	public VirtualToPhysicalMapping randomMapping() {
		ArrayList<LinkedList<KernelandProcess>> groups = new ArrayList<>();
		if (realtimeQueue.size() > 0) groups.add(realtimeQueue);
		if (interactiveQueue.size() > 0) groups.add(interactiveQueue);
		if (backgroundQueue.size() > 0) groups.add(backgroundQueue);
		if (waitList.size() > 0) groups.add(waitList);

		while (true) {
			LinkedList<KernelandProcess> group = groups.get((int) Math.floor(groups.size() * Math.random()));
			KernelandProcess proc = group.get((int) Math.floor(group.size() * Math.random()));
			VirtualToPhysicalMapping vpm = proc.pages[(int) Math.floor(proc.pages.length * Math.random())];
			if (vpm != null && vpm.physicalPage != -1) {
				return vpm;
			}
		}
	}

	/**
	 * Returns the process ID of the new process
	 */
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
	public boolean DeleteProcess(int processId) {
		KernelandProcess kp = processes.get(processId);
		if (kp == null) return false;
		mem.freePages(kp);
		for (Integer id : kp.openDevices) {
			try {
				Close(id);
			} catch (Exception e) {}
		}

		if (!enumToQueue(kp.priority).remove(kp)) { // Remove process from run queue
			waitList.remove(kp); // If nothing was removed, remove it from the wait list
		}
		processes.remove(processId); // Remove process from process list
		return true;
	}

	public void Sleep(int milliseconds) {
		_processToSleep = milliseconds;
	}

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
			_currentProcess = kp;
			RunResult rr;
			Boolean reschedule = true;
			try {
				rr = kp.userlandProcess.run();
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
			} catch (RescheduleException e) {
				Logger.getAnonymousLogger().log(Level.SEVERE, "a reschedule exception was thrown", e);
				reschedule = false;
				DeleteProcess(_currentProcess.pid);
			} catch (Exception e) {
				Logger.getAnonymousLogger().log(Level.SEVERE, "an exception was thrown", e);
			}

			// Clear TLB cache
			mem.invalidateTLB();

			// Act on sleep call
			if (reschedule && _processToSleep > 0) {
				kp.sleepUntil = clock + _processToSleep;
				waitList.add(kp);
				reschedule = false;
			}

			_processToSleep = 0;

			if (reschedule) {
				enumToQueue(kp.priority).add(kp);
			}
		}
	}

	public int Open(String s) throws Exception {
		int id = vfs.Open(s);
		_currentProcess.openDevices.add(id);
		return id;
	}

	public void Close(int id) throws Exception {
		vfs.Close(id);
		_currentProcess.openDevices.remove((Object) id);
	}

	public byte[] Read(int id, int size) throws Exception {
		return vfs.Read(id, size);
	}

	public void Seek(int id, int to) throws Exception {
		vfs.Seek(id, to);
	}

	public int Write(int id, byte[] data) throws Exception {
		return vfs.Write(id, data);
	}

	public void WriteMemory(int address, byte value) throws RescheduleException {
		mem.WriteMemory(address, value);
	}

	public byte ReadMemory(int address) throws RescheduleException {
		return mem.ReadMemory(address);
	}

	public int sbrk(int amount) throws RescheduleException {
		return mem.sbrk(amount);
	}
}
