package server;

public class Message {
	private String to = "";
	private ClientData from = null;
	private String msg = "";

	public Message(String _msg, String _to, ClientData _from) {
		msg = _msg;
		to = _to;
		from = _from;
	}
	public String getTo() {
		return to;
	}
	public ClientData getFromClient() {
		return from;
	}
	public String getFromName() {
		return from.getName();
	}
	public String getMsg() {
		return msg;
	}
	public void kill() {
		to = "";
		from = null;
		msg = "";
	}
}
