public class MutexManager {
	public static final int MUTEX_COUNT = 10;

	private MutexData[] mutexes = new MutexData[MUTEX_COUNT];

	public MutexManager() {
		for (int i = 0; i < MUTEX_COUNT; i++) {
			mutexes[i] = new MutexData();
		}
	}

	public int AttachToMutex(int pid, String name) {
		int mut = -1;
		for (int i = 0; i < MUTEX_COUNT; i++) {
			if (mutexes[i].name.equals(name)) {
				mut = i;
				break;
			}

			if (mutexes[i].name.isEmpty()) {
				mut = i;
			}
		}

		if (mut != -1) {
			mutexes[mut].name = name;
			mutexes[mut].attached.add(pid);
		}

		return mut;
	}

	public boolean Lock(int pid, int mutexId) {
		MutexData mut = mutexes[mutexId];
		if (mut.holder == -1) {
			mut.holder = pid;
		}

		return mut.holder == pid;
	}

	public void Unlock(int pid, int mutexId) {
		MutexData mut = mutexes[mutexId];
		if (mut.holder == pid) {
			mut.holder = -1;
		}
	}

	public void ReleaseMutex(int pid, int mutexId) {
		Unlock(pid, mutexId);

		MutexData mut = mutexes[mutexId];
		mut.attached.remove(pid);
		if (mut.attached.size() == 0) {
			mut.name = "";
		}
	}
}
