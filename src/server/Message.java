package server;

public class Message {
	private String to = "";
	private ClientData from = null;
	private String msg = "";
	private String fromName = "";
	private boolean alive = true;

	public Message(String _msg, String _to, ClientData _from) {
		msg = _msg;
		to = _to;
		from = _from;
		try {
			fromName = from.getName();
		} catch (Exception e) {}
	}
	public String getTo() {
		return to;
	}
	public ClientData getFromClient() {
		return from;
	}
	public String getFromName() {
		return fromName;
	}
	public String getMsg() {
		return msg;
	}
	public boolean getAlive() {
		return alive;
	}
	public void kill() {
		alive = false;
		to = "";
		from = null;
		fromName = "";
		msg = "";
	}
}
