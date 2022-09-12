public class GoodbyeWorldProcess extends UserlandProcess {
	@Override
	public RunResult run() {
		System.out.println("Goodbye World");
		OS.sleep(4000);
		return new RunResult(1, false);
	}
}
