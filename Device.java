public interface Device {
	int Open(String s) throws Exception;
	void Close(int id) throws Exception;
	byte[] Read(int id,int size) throws Exception;
	void Seek(int id,int to) throws Exception;
	int Write(int id, byte[] data) throws Exception;
}
