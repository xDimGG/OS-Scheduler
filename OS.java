import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OS {
	private static OSInterface instance = new PriorityScheduler();
	private static List<OSCall> osCalls = new LinkedList<>();

	private OS() { }

	public static OSInterface getInstance() {
		return instance;
	}

	public static void clearOSCalls() {
		osCalls = new LinkedList<>();
	}

	public static Iterator<OSCall> getOSCalls() {
		return osCalls.iterator();
	}

	public static int CreateProcess(UserlandProcess myNewProcess, PriorityEnum priority) {
		return getInstance().CreateProcess(myNewProcess, priority);
	}

	public static boolean DeleteProcess(int processId) {
		return getInstance().DeleteProcess(processId);
	}
	
	public static void run() {
		// Background process, never runs to timeout
		CreateProcess(new Background(), PriorityEnum.Background);
		// Interactive process, never runs to timeout
		CreateProcess(new Interactive(), PriorityEnum.Interactive);
		// Realtime process, always runs to timeout, should become interactive then background
		CreateProcess(new Realtime(), PriorityEnum.RealTime);
		// Realtime process, never runs to timeout, sleeps for 20 ms
		CreateProcess(new HelloWorldProcess(), PriorityEnum.RealTime);
		getInstance().run();
	}

	public static void sleep(int ms) {
		getInstance().Sleep(ms);
	}
}
