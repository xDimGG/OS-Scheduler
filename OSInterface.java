public interface OSInterface {
	int CreateProcess(UserlandProcess myNewProcess, PriorityEnum priority);

	boolean DeleteProcess(int processId);

	void Sleep(int milliseconds);
	void run();
}

