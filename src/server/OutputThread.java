package server;

import java.io.IOException;
import java.io.PrintWriter;

import com.Main;

// Thread for every client
// Receives data from server and sends it for display
public class OutputThread implements Runnable {
	private ClientData client = null;
	private Message[] msgs = new Message[Server.clientMsgQueue]; // Client messages queue. May need to increase for busy servers
	private String[] serverMsgs = new String[Server.clientMsgQueue]; // Messages from server. Dont need message class to keep track of from who etc.
	private PrintWriter pw = null;
	private boolean alive = true;

	// intiables that a user can change
	private String ANSI_RESET = "";
	private boolean serverColorsEnabled = false;
	private boolean clientColorsEnabled = false;
	private String clientName = "";

	public OutputThread(ClientData _client) {
		client = _client;
		ANSI_RESET = Server.instance.getMain().getTerm().getAnsiReset();
		serverColorsEnabled = Server.instance.getMain().getTerm().getColorsEnabled();
	}
	public void run () {
		while (alive && client != null) {
			try {
				pw = new PrintWriter(client.getSocket().getOutputStream(), true);
			} catch (IOException e) {
				Server.instance.getMain().getTerm().debug("Printwriter failed to start in output thread: " + e);
			}

			// Make sure to check, in case user changed settings
			clientName = client.getName();
			clientColorsEnabled = client.getColorsEnabled();

			// Print client messages
			try {
				for (int i = 0; i < msgs.length; i++) {
					if (msgs[i] != null) {
						String from = msgs[i].getFromName();
						String msg = msgs[i].getMsg();
						String to = msgs[i].getTo();

						// if message is to public or a PM, and if has been verified with server password
						if ((to.contentEquals("PUBLIC") || to.contentEquals(clientName)) && client.getVerifiedPassword()) {
						
							// if colors are enabled
							if (clientColorsEnabled && serverColorsEnabled) {
								// colored
								// get color scheme
								String fromColorScheme = "";
								if (msgs[i].getFromClient() != null) {
									fromColorScheme = msgs[i].getFromClient().getColorScheme();
								}

								// try to print in color
								try {
									pw.println(fromColorScheme + from + ": " + msg + ANSI_RESET);
								} catch (Exception e) {}
							} else {
								// no color
								// try to print no color
								try {
									pw.println(from + ": " + msg);
								} catch (Exception e) {}
							}
						}

						// try clean message
						try {
							msgs[i].kill();
						} catch (Exception e) { System.out.println("Error killing message."); }

						// mark array index null
						msgs[i] = null;
					}
				}
			} catch (Exception e) { System.out.println("Error in output thread message: " + e); }



			// slow down, may need to lower for busier servers
			try {
				Thread.sleep(Server.messageInterval);
			} catch (Exception e) {
				Server.instance.getMain().getTerm().debug("Error with sleep in OutputThread: " + e);
			}



			// Print server messages
			for (int i = 0; i < serverMsgs.length; i++) {
				if (serverMsgs[i] != null) {
					pw.println("SERVER MESSAGE: " + serverMsgs[i]);
					serverMsgs[i] = null;
				}
			}



			// slow down, may need to lower for busier servers
			try {
				Thread.sleep(Server.messageInterval);
			} catch (Exception e) {
				Server.instance.getMain().getTerm().debug("Error with sleep in OutputThread: " + e);
			}

		}
		kill();
	}
	public boolean queueMsg(String _msg, String _to, ClientData _from) {
		if (client != null && client.getAlive()) {
			for (int i = 0; i < msgs.length; i++) {
				if (msgs[i] == null) {
					Message msg = new Message(_msg, _to, _from);
					msgs[i] = msg;
					return true;
				}
			}
		}

		try {
			Server.instance.getMain().getTerm().debug("Couldn't queue message for: " + client.getName());
		} catch (Exception e) {}

		return false; // queue full, or client dead
	}
	public boolean queueServerMsg(String _msg) {
		// queue up a server message in an empty slot
		if (client != null && client.getAlive()) {
			for (int i = 0; i < serverMsgs.length; i++) {
				if (serverMsgs[i] == null) {
					serverMsgs[i] = _msg;
					return true;
				}
			}
		}

		try {
			Server.instance.getMain().getTerm().debug("Server message queue full for: " + client.getName());
		} catch (Exception e) {}

		return false;
	}
	protected void finalize() {
		alive = false;

		// make sure client dies
		try {
			Server.instance.getFunctionsInstance().killClient(client.getId());
		} catch (Exception e) {}
		client = null;
		pw = null;

		// drop all messages
		for (int i = 0; i < msgs.length; i++) {
			msgs[i] = null;
		}
		msgs = null;

		// drop all server messages
		for (int i = 0; i < serverMsgs.length; i++) {
			serverMsgs[i] = null;
		}
		serverMsgs = null;
	}
	public void kill() {
		this.finalize();
	}
}
