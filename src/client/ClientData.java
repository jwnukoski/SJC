package client;

public class ClientData {
	private int id = 0;
	private String info = "";
	private String status = "";
	
	public ClientData(int _id, String _info, String _status) {
		// Status should remain blank. 'finished' to be marked for removal.
		id = _id;
		info = _info;
		status = _status;
	}
	public String getInfo() {
		return info;
	}
	public String getStatus() {
		return status;
	}
	public int getId() {
		return id;
	}
}
