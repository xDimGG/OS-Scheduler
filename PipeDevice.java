public class PipeDevice implements Device, SharedDevice {
	public static final int MAX_DEVICES = 10;

	PipeInstance[] arr = new PipeInstance[MAX_DEVICES];

	public int Open(String s) {
		int available = -1;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == null) {
				available = i;
				continue;
			}

			if (arr[i].name.equals(s)) {
				arr[i].attached++;
				return i;
			}
		}

		if (available != -1) {
			arr[available] = new PipeInstance(1024, s);
		}

		return available;
	}

	public void Close(int id) {
		if (id >= 0 && id < MAX_DEVICES && arr[id] != null) {
			arr[id].attached--;
			if (arr[id].attached == 0) {
				arr[id] = null;
			}
		}
	}

	public byte[] Read(int id, int size) {
		if (id >= 0 && id < MAX_DEVICES && arr[id] != null) {
			byte[] buf = new byte[size];
			arr[id].Read(buf);
			return buf;
		}

		return null;
	}

	public void Seek(int id, int to) {
		Read(id, to);
	}

	public int Write(int id, byte[] data) {
		if (id >= 0 && id < MAX_DEVICES && arr[id] != null) {
			arr[id].Write(data);
			return data.length;
		}

		return -1;
	}

	public boolean HasAttachedProcesses(int id) {
		return arr[id] != null && arr[id].attached != 0;
	}
}
