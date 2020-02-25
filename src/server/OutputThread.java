package server;

import java.io.IOException;
import java.io.PrintWriter;

// Thread for every client
// Receives data from server and sends it for display
public class OutputThread implements Runnable {
	private ClientData client = null;
	private Message[] msgs = new Message[Server.clientMsgQueue]; // Message queue. May need to increase for busy servers
	private PrintWriter pw = null;
	
	// For formatting messages. Colors need to be enabled on both server and client
	private String ANSI_RESET = Server.instance.getMain().getTerm().getAnsiReset();
	private boolean serverColorsEnabled = Server.instance.getMain().getTerm().getColorsEnabled();
	
	public OutputThread(ClientData _client) {
		client = _client;
	}
	public void run () {
		try {
			pw = new PrintWriter(client.getSocket().getOutputStream(), true);
		} catch (IOException e) {
			Server.instance.getMain().getTerm().debug("Printwriter failed to start in output thread: " + e);
		}
		
		while (client.getSocket() != null) {
			try {
				// Loop message queue and send to necessary clients
				for (var i = 0; i < msgs.length; i++) {
					if (msgs[i] != null) {
						if (serverColorsEnabled && client.getColorsEnabled()) {
							final int clientColorId = msgs[i].getFromClient().getColorId();
							final String clientColorScheme = Server.instance.getMain().getTerm().getColorScheme(clientColorId);
							pw.println(clientColorScheme + msgs[i].getFromName() + ": " + msgs[i].getMsg() + ANSI_RESET); // print message to user
						} else {
							pw.println(msgs[i].getFromName() + ": " + msgs[i].getMsg()); // print message to user
						}
						msgs[i] = null; // clear from list
					}
				}
				
				// slow down, may need to lower for busier servers
				try {
					Thread.sleep(Server.messageInterval);
				} catch (Exception e) {
					Server.instance.getMain().getTerm().debug("Error with sleep in OutputThread: " + e);
				}
			} catch (Exception e) {
				Server.instance.getMain().getTerm().debug("Could not connect to send notification. Closing thread.");
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
