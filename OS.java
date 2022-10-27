public class OS {
	private static OSInterface instance = new PriorityScheduler();

	private OS() { }

	public static OSInterface getInstance() {
		return instance;
	}

	public static int CreateProcess(UserlandProcess myNewProcess, PriorityEnum priority) {
		return getInstance().CreateProcess(myNewProcess, priority);
	}

	public static boolean DeleteProcess(int processId) {
		return getInstance().DeleteProcess(processId);
	}
	
	public static void run() {
		// Background process, never runs to timeout
		// CreateProcess(new Background(), PriorityEnum.Background);
		// Interactive process, never runs to timeout
		// CreateProcess(new Interactive(), PriorityEnum.Interactive);
		// Realtime process, always runs to timeout, should become interactive then background
		// CreateProcess(new Realtime(), PriorityEnum.RealTime);
		// Realtime process, never runs to timeout, sleeps for 20 ms
		// CreateProcess(new HelloWorldProcess(), PriorityEnum.RealTime);
		// CreateProcess(new PipeTest(), PriorityEnum.RealTime);
		// CreateProcess(new FSTest(), PriorityEnum.Background);
		CreateProcess(new MemTest("a"), PriorityEnum.RealTime);
		CreateProcess(new MemTest("b"), PriorityEnum.RealTime);
		CreateProcess(new MemTest("c"), PriorityEnum.RealTime);
		getInstance().run();
	}

	public static void Sleep(int ms) {
		getInstance().Sleep(ms);
	}

	public static int Open(String s) throws Exception {
		return getInstance().Open(s);
	}

	public static void Close(int id) throws Exception {
		getInstance().Close(id);
	}

	public static byte[] Read(int id, int size) throws Exception {
		return getInstance().Read(id, size);
	}

	public static void Seek(int id, int to) throws Exception {
		getInstance().Seek(id, to);
	}

	public static int Write(int id, byte[] data) throws Exception {
		return getInstance().Write(id, data);
	}

	public static void WriteMemory(int address, byte value) throws RescheduleException {
		getInstance().WriteMemory(address, value);
	}

	public static byte ReadMemory(int address) throws RescheduleException {
		return getInstance().ReadMemory(address);
	}

	public static int sbrk(int amount) throws RescheduleException {
		return getInstance().sbrk(amount);
	}
}
