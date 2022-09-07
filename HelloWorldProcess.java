public class HelloWorldProcess extends UserlandProcess {
	@Override
	public RunResult run() {
		RunResult result = new RunResult();
		System.out.println("Hello World");
		return result.complete();
	}
}
