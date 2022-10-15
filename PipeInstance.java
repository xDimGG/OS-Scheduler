public class PipeInstance {
	public String name;
	public int attached = 1;
	private int cap;
	private byte[] buff;
	private int readPos = 0;
	private int writePos = 0;

	PipeInstance(int _cap, String _name) {
		name = _name;
		cap = _cap;
		buff = new byte[_cap];
	}

	int Read(byte[] buf) {
		int i = -1;
		while (i < buf.length - 1) {
			if (readPos == writePos) break;
			buf[++i] = buff[readPos];
			readPos = (readPos + 1) % cap;
		}

		return i + 1;
	}

	int Write(byte[] buf) {
		int i = -1;
		while (i < buf.length - 1) {
			if ((writePos + 1) % cap == readPos) break;
			buff[writePos] = buf[++i];
			writePos = (writePos + 1) % cap;
		}

		return i + 1;
	}
}
