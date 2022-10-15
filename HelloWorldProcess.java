public class HelloWorldProcess extends UserlandProcess {
	public RunResult run() {
		System.out.println("Hello World");
		OS.Sleep(20);
		return new RunResult(1, false);
	}
}
