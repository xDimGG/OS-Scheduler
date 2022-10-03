public class Realtime extends UserlandProcess {
	@Override
	public RunResult run() {
		System.out.println("Realtime");
		return new RunResult(1, true);
	}
}
