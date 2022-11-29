public interface ProcessInterface {
	int CreateProcess(UserlandProcess myNewProcess, PriorityEnum priority);

	KernelandProcess getCurrentProcess();
	boolean DeleteProcess(int processId);

	void Sleep(int milliseconds);
	void run();
}
