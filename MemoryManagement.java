import java.util.BitSet;

public class MemoryManagement implements MemoryInterface {
	public static final int PAGE_COUNT = 1024;
	public static final int PAGE_SIZE = 1024;

	public byte[][] memory = new byte[PAGE_COUNT][PAGE_SIZE];
	public BitSet inUse = new BitSet(PAGE_COUNT);

	public int tlbVirtual = -1;
	public int tlbPhysical = -1;

	private int getPhysicalPage(int address) throws RescheduleException {
		if (tlbVirtual != address) {
			KernelandProcess proc = OS.getInstance().getCurrentProcess();
			int virtualPage = address / PAGE_SIZE;
			if (virtualPage >= PAGE_COUNT) {
				throw new RescheduleException();
			}

			tlbPhysical = proc.pages[virtualPage];
			if (tlbPhysical == -1) {
				throw new RescheduleException();
			}

			tlbVirtual = address;
		}

		return tlbPhysical;
	}

	public void WriteMemory(int address, byte value) throws RescheduleException {
		memory[getPhysicalPage(address)][address % PAGE_SIZE] = value;
	}

	public byte ReadMemory(int address) throws RescheduleException {
		return memory[getPhysicalPage(address)][address % PAGE_SIZE];
	}

	public int sbrk(int amount) throws RescheduleException  {
		KernelandProcess proc = OS.getInstance().getCurrentProcess();
		int freeVirtualPage = 0;
		for (;; freeVirtualPage++) {
			if (freeVirtualPage >= PAGE_COUNT) {
				throw new RescheduleException();
			}

			if (proc.pages[freeVirtualPage] == -1) {
				break;
			}
		}

		for (int i = freeVirtualPage; i < freeVirtualPage + (amount / PAGE_SIZE) + 1; i++) {
			int freePhysicalPage = inUse.nextClearBit(freeVirtualPage - i);
			if (i >= PAGE_COUNT || freePhysicalPage >= PAGE_COUNT) {
				throw new RescheduleException();
			}

			inUse.set(freePhysicalPage);
			proc.pages[i] = freePhysicalPage;
		}

		return freeVirtualPage * PAGE_SIZE;
	}

	public void invalidateTLB() {
		tlbPhysical = -1;
		tlbVirtual = -1;
	}

	public void freePages() {
		KernelandProcess proc = OS.getInstance().getCurrentProcess();
		for (int i : proc.pages) {
			if (i == -1) break;
			inUse.clear(i);
		}
	}
}
