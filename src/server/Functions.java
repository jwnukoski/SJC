package server;

import java.net.Socket;

// Runs back-end processing
public class Functions implements Runnable {
	private ClientData[] clients = new ClientData[Server.maxConnections]; // Change number for more connections
	private final Command[] commands = {new Command("/name", "Set your username. Ex: /name John"),
			new Command("/help", "Get a list of user commands."),
			new Command("/colors", "Enables or disables terminal colors. Ex: /colors yes, /colors no")};

	public void run() {
		
	}
	public void sendMsg(String _msg, String _to, ClientData _from) {
		Message msg = new Message(_msg, _to, _from);
		
		// Send message to all clients
		for (var i = 0; i < clients.length; i++) {
			if (clients[i] != null) {
				// Send if a PM to this user or PUBLIC
				if (clients[i].getName().contentEquals(_to) || _to.contentEquals("PUBLIC")) {
					// Private messages and public messages
					boolean sent = clients[i].sendMsg(msg);
					
					// needed?
					if (!sent) {
						// message wasn't added to client queue, remove client?
						clients[i].kill();
						clients[i] = null;
					}
				}
			}
		}
	}
	public boolean addClient(Socket _clientSocket, String _clientIpAddress) {
		// Creates clients and their threads, adds to list to keep track
		for (var i = 0; i < clients.length; i++) {
			if (clients[i] == null) {
				clients[i] = new ClientData(i, _clientSocket, _clientIpAddress);
				return true;
			}
		}
		return false;
	}
	public boolean isCommand(String _msg) {
		for (var i = 0; i < commands.length; i++) {
			if (_msg.contains(commands[i].getCmdWord())) {
				return true;
			}
		}
		return false;
	}
	protected void finalize() {
		// clear clients
		for (var i = 0; i < clients.length; i++) {
			if (clients[i] != null) {
				clients[i].kill();
				clients[i] = null;
			}
		}
		clients = null;
	}
	public void kill() {
		this.finalize();
	}
	public boolean processCommand(String _msg, ClientData _client) {
		// User command processing
		if (_msg.startsWith("/name")) {
			// change name of client
			String name = _msg.substring(5, _msg.length());
			_client.setName(name);
			return true;
		} else if (_msg.contentEquals("/help")) {
			// display help info to client only
			for (int i = 0; i < commands.length; i++) {
				final String helpList = (commands[i].getCmdWord() + " - " + commands[i].getDescription() + "\n");
				sendMsg(helpList, _client.getName(), _client);
			}
		} else if (_msg.contentEquals("/colors no")) {
			// turn terminal colors off for client
			_client.setColorsEnabled(false);
			return true;
		} else if (_msg.contentEquals("/colors yes")) {
			// turn terminal colors on for client
			_client.setColorsEnabled(true);
			return true;
		}
		return true;
	}
}
