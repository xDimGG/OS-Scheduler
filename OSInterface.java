public interface OSInterface {
	int CreateProcess(UserlandProcess myNewProcess);
	boolean DeleteProcess(int processId);
}
