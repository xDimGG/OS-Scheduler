public class Startup {
	public static void main(String[] args) {
		BasicScheduler scheduler = new BasicScheduler();
		scheduler.CreateProcess(new HelloWorldProcess());
		scheduler.CreateProcess(new GoodbyeWorldProcess());
		scheduler.run();
	}
}
