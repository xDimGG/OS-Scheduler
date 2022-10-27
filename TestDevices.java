public class TestDevices extends UserlandProcess {
	public String label;

	private int addr = -1;

	public TestDevices(String label) {
		this.label = label;
	}

	public RunResult run() throws Exception {
		if (addr == -1) {
			addr = OS.sbrk(1);
			OS.WriteMemory(addr, (byte) 0);
		}

		byte val = OS.ReadMemory(addr);
		if (val == 0) {
			val = (byte) (Math.random() * 10);
			OS.WriteMemory(addr, val);
		} else {
			val--;
			OS.WriteMemory(addr, val);
		}

		System.out.println(label + ": " + val);

		return new RunResult(1, false);
	}
}
