package server;

import java.io.IOException;
import java.io.PrintWriter;

public class OutputThread implements Runnable {
	private ClientData client = null;
	private Message[] msgs = new Message[Server.clientMsgQueue]; // Message queue. May need to increase for busy servers
	private PrintWriter pw = null;
	
	public OutputThread(ClientData _client) {
		client = _client;
	}
	public void run () {
		try {
			pw = new PrintWriter(client.getSocket().getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Printwriter failed to start in output thread: " + e);
		}
		
		while (client.getSocket() != null) {
			try {
				// Print message queue
				for (var i = 0; i < msgs.length; i++) {
					if (msgs[i] != null) {
						pw.println(msgs[i].getFrom() + ": " + msgs[i].getMsg()); // print message to user
						msgs[i] = null; // clear from list
					}
				}
				
				// slow down, may need to lower for busier servers
				try {
					Thread.sleep(Server.messageInterval);
				} catch (Exception e1) {
					System.out.println(e1);
				}
			} catch (Exception e) {
				System.out.println("Could not connect to send notification. Closing thread.");
				break;
			}
		}
		this.finalize();
	}
	public boolean queueMsg(Message _msg) {
		for (var i = 0; i < msgs.length; i++) {
			if (msgs[i] == null) {
				msgs[i] = _msg;
				return true;
			}
		}
		return false; // queue full
	}
	protected void finalize() {
		client = null;
		pw = null;
		msgs = null;
	}
	public void kill() {
		this.finalize();
	}
}
