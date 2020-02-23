package server;

import java.net.Socket;

// Runs back-end processes
public class Functions implements Runnable {
	private ClientData[] clients = new ClientData[Server.maxConnections]; // Change number for more connections
	private final Command[] commands = {new Command("/name", "Set your username. Ex: /name John"),
			new Command("/help", "Get a list of user commands.")};

	public void run() {
		
	}
	public void sendMsg(String _msg, String _to, String _from) {
		Message msg = new Message(_msg, _to, _from);
		
		// Send message to all clients
		for (var i = 0; i < clients.length; i++) {
			if (clients[i] != null) {
				if (clients[i].getName().contentEquals(_to) || _to.contentEquals("PUBLIC")) {
					// Private messages and public messages
					boolean sent = clients[i].sendMsg(msg);
					
					// needed
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
		if (_msg.contains("/name")) {
			String name = _msg.substring(5, _msg.length());
			_client.setName(name);
			return true;
		} else if (_msg.contains("/help")) {
			for (int i = 0; i < commands.length; i++) {
				final String helpList = (commands[i].getCmdWord() + " - " + commands[i].getDescription() + "\n");
				sendMsg(helpList, _client.getName(), "SERVER");
			}
		}
		return true;
	}
}
