import java.util.Random;

public class RandomDevice implements Device {
	public static final int MAX_DEVICES = 10;

	Random[] arr = new Random[MAX_DEVICES];

	public int Open(String s) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == null) {
				arr[i] = s == "" ? new Random() : new Random(s.hashCode());
				return i;
			}
		}

		return -1;
	}

	public void Close(int id) {
		if (id >= 0 && id < MAX_DEVICES) {
			arr[id] = null;
		}
	}

	public byte[] Read(int id, int size) {
		if (id >= 0 && id < MAX_DEVICES && arr[id] != null) {
			byte[] buf = new byte[size];
			arr[id].nextBytes(buf);
			return buf;
		}

		return null;
	}

	public void Seek(int id, int to) {
		Read(id, to);
	}

	public int Write(int id, byte[] data) {
		return 0;
	}
}
