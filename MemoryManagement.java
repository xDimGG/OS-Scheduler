import java.util.BitSet;

public class MemoryManagement implements MemoryInterface {
	public static final int PAGE_COUNT = 1024;
	public static final int PAGE_SIZE = 1024;

	public byte[][] memory = new byte[PAGE_COUNT][PAGE_SIZE];
	public BitSet inUse = new BitSet(PAGE_COUNT);

	public int tlbVirtual = -1;
	public int tlbPhysical = -1;

	private PriorityScheduler os;
	private int swapfile;

	public MemoryManagement(PriorityScheduler _os) {
		os = _os;
		try {
			swapfile = os.getVFS().Open("file swapfile");
		} catch (Exception e) {
			OS.panic(e);
		}
	}

	private void clearPage(int physicalPage) {
		inUse.clear(physicalPage);
		for (int i = 0; i < PAGE_SIZE; i++) {
			memory[physicalPage][i] = 0;
		}
	}

	private void stash(VirtualToPhysicalMapping vtp) {
		try {
			// If the page is not on disk or it is dirty
			if (vtp.diskPage == -1 || vtp.isDirty) {
				// Write page to disk
				vtp.diskPage = os.getVFS().Write(swapfile, memory[vtp.physicalPage]) - PAGE_SIZE;
				// Mark mapping as clean
				vtp.isDirty = false;
				// Clear memory
				clearPage(vtp.physicalPage);
			}

			vtp.physicalPage = -1;
		} catch (Exception e) {
			OS.panic(e);
		}
	}

	private void recover(VirtualToPhysicalMapping vtp, int physicalPage) {
		try {
			int ptr = os.getVFS().Write(swapfile, null);
			// Read from swapfile
			os.getVFS().Seek(swapfile, vtp.diskPage);
			memory[physicalPage] = os.getVFS().Read(swapfile, PAGE_SIZE);
			// Move file pointer to end of file
			os.getVFS().Seek(swapfile, ptr);
			// Point to new physical page
			vtp.physicalPage = physicalPage;
			// Mark mapping as clean
			vtp.isDirty = false;
		} catch (Exception e) {
			OS.panic(e);
		}
	}

	// Steals a random page, returns free physical page
	private int steal() {
		VirtualToPhysicalMapping vtp = os.randomMapping();
		int pp = vtp.physicalPage;
		stash(vtp);
		return pp;
	}

	private int getPhysicalPage(int address, boolean write) throws RescheduleException {
		if (tlbVirtual != address) {
			KernelandProcess proc = OS.getInstance().getCurrentProcess();
			int virtualPage = address / PAGE_SIZE;
			if (virtualPage >= PAGE_COUNT) {
				throw new RescheduleException();
			}

			VirtualToPhysicalMapping vtp = proc.pages[virtualPage];
			if (vtp == null) {
				throw new RescheduleException();
			}

			// If the page is on disk
			if (vtp.physicalPage == -1) {
				// Steal a page
				int freePage = this.steal();
				// Recover from disk
				this.recover(vtp, freePage);
			}

			if (write) {
				vtp.isDirty = true;
			}

			tlbPhysical = vtp.physicalPage;
			tlbVirtual = address;
		}

		return tlbPhysical;
	}

	public void WriteMemory(int address, byte value) throws RescheduleException {
		memory[getPhysicalPage(address, true)][address % PAGE_SIZE] = value;
	}

	public byte ReadMemory(int address) throws RescheduleException {
		return memory[getPhysicalPage(address, false)][address % PAGE_SIZE];
	}

	public int sbrk(int amount) throws RescheduleException  {
		KernelandProcess proc = OS.getInstance().getCurrentProcess();
		int freeVirtualPage = 0;
		for (;; freeVirtualPage++) {
			if (freeVirtualPage >= PAGE_COUNT) {
				throw new RescheduleException();
			}

			if (proc.pages[freeVirtualPage] == null) {
				break;
			}
		}

		for (int i = freeVirtualPage; i <= freeVirtualPage + ((amount - 1) / PAGE_SIZE); i++) {
			int freePhysicalPage = inUse.nextClearBit(0);
			if (i >= PAGE_COUNT || freePhysicalPage >= PAGE_COUNT) {
				freePhysicalPage = this.steal();
			}

			inUse.set(freePhysicalPage);
			proc.pages[i] = new VirtualToPhysicalMapping(freePhysicalPage);
		}

		return freeVirtualPage * PAGE_SIZE;
	}

	public void invalidateTLB() {
		tlbPhysical = -1;
		tlbVirtual = -1;
	}

	public void freePages(KernelandProcess proc) {
		for (var p : proc.pages) {
			if (p.physicalPage != -1) {
				clearPage(p.physicalPage);
			}
		}
	}
}
