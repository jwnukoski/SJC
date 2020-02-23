package server;

public class Message {
	private String to = "";
	private String from = "";
	private String msg = "";

	public Message(String _msg, String _to, String _from) {
		msg = _msg;
		to = _to;
		from = _from;
	}
	public String getTo() {
		return to;
	}
	public String getFrom() {
		return from;
	}
	public String getMsg() {
		return msg;
	}
	public void kill() {
		to = "";
		from = "";
		msg = "";
	}
}
