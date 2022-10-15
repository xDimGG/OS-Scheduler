public class FSTest extends UserlandProcess {
	private boolean ran = false;

	public RunResult run() throws Exception {
		int file = OS.Open("file test.txt");

		if (!ran) {
			OS.Write(file, "hey".getBytes());
			ran = true;
		} else {
			byte[] text = OS.Read(file, 3);
			System.out.println(new String(text, "UTF-8"));
		}

		OS.Close(file);

		return new RunResult(1, false);
	}
}
