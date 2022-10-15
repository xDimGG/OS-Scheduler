import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device {
	public static final int MAX_DEVICES = 10;

	RandomAccessFile[] arr = new RandomAccessFile[MAX_DEVICES];

	public int Open(String s) throws FileNotFoundException {
		if (s.equals("")) {
			throw new FileNotFoundException();
		}

		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == null) {
				arr[i] = new RandomAccessFile(s, "rw");
				return i;
			}
		}

		return -1;
	}

	public void Close(int id) throws IOException {
		if (id >= 0 && id < MAX_DEVICES && arr[id] != null) {
			arr[id].close();
			arr[id] = null;
		}
	}

	public byte[] Read(int id, int size) throws IOException {
		if (id >= 0 && id < MAX_DEVICES && arr[id] != null) {
			byte[] buf = new byte[size];
			arr[id].read(buf);
			return buf;
		}

		return null;
	}

	public void Seek(int id, int to) throws IOException {
		if (id >= 0 && id < MAX_DEVICES && arr[id] != null) {
			arr[id].seek(to);
		}
	}

	public int Write(int id, byte[] data) throws IOException {
		if (id >= 0 && id < MAX_DEVICES && arr[id] != null) {
			arr[id].write(data);
			return data.length;
		}

		return -1;
	}
}
