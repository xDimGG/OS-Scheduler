public class HelloWorldProcess extends UserlandProcess {
	@Override
	public RunResult run() {
		System.out.println("Hello World");
		OS.sleep(20);
		return new RunResult(1, false);
	}
}
