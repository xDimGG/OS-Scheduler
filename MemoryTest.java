public class MemoryTest extends UserlandProcess {
	private static final int UID_SIZE = 2048;

	private int addr = -1;
	private byte[] uid;

	public RunResult run() throws Exception {
		if (addr == -1) {
			// Make UID random bytes
			int rand = OS.Open("random");
			uid = OS.Read(rand, UID_SIZE);
			OS.Close(rand);

			addr = OS.sbrk(UID_SIZE * 2);
			for (int i = 0; i < UID_SIZE; i++) {
				OS.WriteMemory(addr + i, uid[i]);
				OS.WriteMemory(addr + i + UID_SIZE, uid[i]);
			}
		} else {
			// Alter memory sometimes
			if (uid[1] % 50 == 0) {
				uid[0] += 1;
				OS.WriteMemory(addr, uid[0]);
				OS.WriteMemory(addr + UID_SIZE, uid[0]);
			}
		}

		for (int i = 0; i < UID_SIZE; i++) {
			byte b = OS.ReadMemory(addr + i);
			byte bCopy = OS.ReadMemory(addr + i + UID_SIZE);

			if (b != uid[i]) {
				System.out.println("corrupt memory when comparing to Java memory");
				throw new RescheduleException();
			}

			if (b != bCopy) {
				System.out.println("corrupt memory when comparing to own memory");
				throw new RescheduleException();
			}
		}

		return new RunResult(1, false);
	}
}
