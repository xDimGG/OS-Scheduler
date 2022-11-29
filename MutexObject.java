import java.util.ArrayList;
import java.util.List;

public class MutexObject {
	public String name = "";
	public List<Integer> attached = new ArrayList<>();
	public int holder = -1; // holder == -1 when not mutex not in use
}
