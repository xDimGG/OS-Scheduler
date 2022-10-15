public class Background extends UserlandProcess {
	public RunResult run() throws Exception {
		System.out.println("Background");
		return new RunResult(1, false);
	}
}
