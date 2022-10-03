public class Interactive extends UserlandProcess {
	@Override
	public RunResult run() {
		System.out.println("Interactive");
		return new RunResult(1, false);
	}
}
