public class TestMutex extends UserlandProcess {
	private int mutexId = -1;
	private String name;
	private int run = 0;

	TestMutex(String name) {
		this.name = name;
	}

	public RunResult run() throws Exception {
		if (mutexId == -1) {
			mutexId = OS.AttachToMutex("test");
		}

		System.out.println(this.name + " is trying to use mutex test");
		if (OS.Lock(mutexId)) {
			System.out.println(this.name + " is using mutex test");
			run++;

			if (run % 5 == 0) {
				OS.Unlock(mutexId);
				System.out.println(this.name + " is unlocking mutex test");
			}
		}

		return new RunResult(0, true);
	}
}
