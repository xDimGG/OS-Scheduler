import java.util.HashMap;

public class BasicScheduler implements OSInterface {
	private int nextPID = 1;
	private HashMap<Integer, KernelandProcess> processes = new HashMap<>();

	/**
	 * Returns the process ID of the new process
	 */
	@Override
	public int CreateProcess(UserlandProcess myNewProcess) {
		KernelandProcess kp = new KernelandProcess(myNewProcess, nextPID);
		nextPID++;
		processes.put(kp.pid, kp);
		return kp.pid;
	}

	/**
	 * Returns true if the process existed and was deleted
	 */
	@Override
	public boolean DeleteProcess(int processId) {
		return processes.remove(processId) != null;
	}

	/**
	 * Run method that loops forever
	 */
	public void run() {
		while (true) {
			for (KernelandProcess kp : processes.values()) {
				RunResult result = kp.userlandProcess.run();
			}
		}
	}
}
