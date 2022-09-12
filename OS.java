import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OS {
	private static OSInterface instance = new BasicScheduler();
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

	public static int CreateProcess(UserlandProcess myNewProcess) {
		return getInstance().CreateProcess(myNewProcess);
	}

	public static boolean DeleteProcess(int processId) {
		return getInstance().DeleteProcess(processId);
	}
	
	public static void run() {
		BasicScheduler scheduler = new BasicScheduler();
		scheduler.CreateProcess(new HelloWorldProcess());
		scheduler.CreateProcess(new GoodbyeWorldProcess());
		scheduler.run();
	}

	public static void sleep(int ms) {
		var call = new OSCall();
		call.fn = OSCallFn.SLEEP;
		call.intArg0 = ms;
		osCalls.add(call);
	}
}
