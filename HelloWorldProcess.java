public class HelloWorldProcess extends UserlandProcess {
	@Override
	public RunResult run() {
		System.out.println("Hello World");
		OS.sleep(2000);
		return new RunResult(1, false);
	}
}
