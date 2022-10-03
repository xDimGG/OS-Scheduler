public class Background extends UserlandProcess {
	@Override
	public RunResult run() {
		System.out.println("Background");
		return new RunResult(1, false);
	}
}
