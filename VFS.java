import java.util.HashMap;

public class VFS implements Device {
	private FakeFileSystem fs = new FakeFileSystem();
	private PipeDevice pipe = new PipeDevice();
	private RandomDevice rand = new RandomDevice();
	private HashMap<Integer, VirtualFile> devices = new HashMap<Integer, VirtualFile>();

	public int Open(String s) throws Exception {
		String[] rawArgs = s.split(" ");
		String arg0 = rawArgs[0].toLowerCase();
		String args = s.substring(arg0.length()).trim();

		Device dev = null;
		switch (arg0) {
			case "file":
				dev = fs;
				break;
			case "pipe":
				dev = pipe;
				break;
			case "random":
				dev = rand;
				break;
		}

		if (dev == null) {
			return -1;
		}

		int deviceId = dev.Open(args);
		if (deviceId == -1) {
			return -1;
		}

		int id = dev.hashCode() + deviceId;
		devices.put(id, new VirtualFile(deviceId, dev));

		return id;
	}

	public void Close(int id) throws Exception {
		if (devices.containsKey(id)) {
			VirtualFile vf = devices.get(id);
			vf.dev.Close(vf.id);
			if (vf.dev instanceof SharedDevice) {
				if (!((SharedDevice) vf.dev).HasAttachedProcesses(vf.id)) {
					devices.remove(vf.id);
				}
			} else {
				devices.remove(vf.id);
			}
		}
	}

	public byte[] Read(int id, int size) throws Exception {
		if (devices.containsKey(id)) {
			VirtualFile vf = devices.get(id);
			return vf.dev.Read(vf.id, size);
		}

		return null;
	}

	public void Seek(int id, int to) throws Exception {
		if (devices.containsKey(id)) {
			VirtualFile vf = devices.get(id);
			vf.dev.Seek(vf.id, to);
		}
	}

	public int Write(int id, byte[] data) throws Exception {
		if (devices.containsKey(id)) {
			VirtualFile vf = devices.get(id);
			return vf.dev.Write(vf.id, data);
		}

		return 0;
	}
}
