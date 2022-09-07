public class GoodbyeWorldProcess extends UserlandProcess {
	@Override
	public RunResult run() {
		RunResult result = new RunResult();
		System.out.println("Goodbye World");
		return result.complete();
	}
}
