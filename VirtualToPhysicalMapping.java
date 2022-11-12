public class VirtualToPhysicalMapping {
	public boolean isDirty = false;
	public int physicalPage;
	public int diskPage = -1;

	VirtualToPhysicalMapping(int _physicalPage) {
		physicalPage = _physicalPage;
	}
}
