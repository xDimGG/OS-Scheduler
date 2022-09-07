/**
 * Security layer built on top of userland processes
 */
public class KernelandProcess {
	public UserlandProcess userlandProcess;
	public int pid;

	KernelandProcess(UserlandProcess _userlandProcess, int _pid) {
		this.userlandProcess = _userlandProcess;
		this.pid = _pid;
	}
}
