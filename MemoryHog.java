public class MemoryHog extends UserlandProcess {
	private boolean ranOnce = false;

	public RunResult run() throws RescheduleException {
		if (!ranOnce) {
			OS.sbrk(1024 * 1024);

			// Summon 2000 memory test processes
			for (int i = 0; i < 2000; i++) {
				OS.CreateProcess(new MemoryTest(), PriorityEnum.Interactive);
			}

			ranOnce = true;
		}

		return new RunResult(1, false);
	}
}
