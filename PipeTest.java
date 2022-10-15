import java.util.Arrays;

public class PipeTest extends UserlandProcess {
	private boolean send = true;
	private int sendPipe = -1;

	public RunResult run() throws Exception {
		if (sendPipe == -1) {
			sendPipe = OS.Open("pipe alpha");
		}

		if (send) {
			int rand = OS.Open("random");
			byte[] buf = OS.Read(rand, 5);
			OS.Close(rand);

			OS.Write(sendPipe, buf);
			System.out.println("Sent " + Arrays.toString(buf));
		} else {
			int pipe = OS.Open("pipe alpha");
			byte[] buf = OS.Read(pipe, 5);
			System.out.println("Received " + Arrays.toString(buf));
			OS.Close(pipe);
		}

		send = !send;

		return new RunResult(1, false);
	}
}
