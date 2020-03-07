package server;

import java.net.Socket;
import java.util.Random;
import com.Main;

// Runs back-end processing
public class Functions {
	private ClientData[] clients = new ClientData[Server.maxConnections]; // Change number for more connections
	private final Command[] commands = {new Command("/name", "Set your username. Ex: /name John. Type just /name to get your current name."),
			new Command("/help", "Get a list of user commands."),
			new Command("/colors", "Enables or disables terminal colors. Ex: /colors yes, /colors no"),
			new Command("/pm", "Send a personal message to a user. Ex: /pm JohnSmith My message.")};
	private String helpDoc = "\nHelp Documentation:\n";

	public Functions() {
		// build help doc once
		for (int i = 0; i < commands.length; i++) {
			helpDoc += (commands[i].getCmdWord() + " - " + commands[i].getDescription() + "\n");
		}
	}
	public void sendMsg(String _msg, String _to, ClientData _from) {
		// For PMs and public messages from clients
		// Send message to clients
		for (int i = 0; i < clients.length; i++) {
			if (clients[i] != null) {
				// Send if a PM to this user or PUBLIC
				if (clients[i].getName().contentEquals(_to) || _to.contentEquals("PUBLIC")) {
					// Private messages and public messages
					boolean sent = clients[i].sendMsg(_msg, _to, _from);

					// needed?
					if (!sent) {
						// message wasn't added to client queue, remove client?
						killClient(i);
					}
				}
			}
		}
	}
	public boolean sendServerMsg(String _msg) {
		// for messages to all clients from the server
		for (int i = 0; i < clients.length; i++) {
			if (clients[i] != null && clients[i].getAlive()) {
				clients[i].sendServerMsg(_msg);
			}
		}
		return true;
	}
	public boolean addClient(Socket _clientSocket, String _clientIpAddress) {
		// Creates clients and their threads, adds to list to keep track
		for (int i = 0; i < clients.length; i++) {
			if (clients[i] == null) {
				clients[i] = new ClientData(i, _clientSocket, _clientIpAddress);
				return true;
			}
		}
		return false;
	}
	public boolean killClient(int _clientId) {
		if (clients[_clientId] != null) {
			clients[_clientId].kill();
			clients[_clientId] = null;
			return true;
		}
		return false;
	}
	public boolean isCommand(String _msg) {
		for (int i = 0; i < commands.length; i++) {
			if (_msg.contains(commands[i].getCmdWord())) {
				return true;
			}
		}
		return false;
	}
	public void kill() {
		// clear clients
		for (int i = 0; i < clients.length; i++) {
			if (clients[i] != null) {
				clients[i].kill();
				clients[i] = null;
			}
		}
		clients = null;
	}
	public boolean processCommand(String _msg, ClientData _client) {
		// User command processing
		if (_msg.startsWith("/name") && _msg.trim().length() > 5) {
			// Change name of client, must be unique. Append number if not.
			String name = _msg.substring(6, _msg.length());
			return setClientName(name, _client, true);
		} else if (_msg.contentEquals("/name")) {
			// Send clients name to them
			sendMsg("Your name is: " + _client.getName() + ".", _client.getName(), _client);
		} else if (_msg.contentEquals("/help")) {
			// display help info to client only
			sendMsg(helpDoc, _client.getName(), _client);
			return true;
		} else if (_msg.contentEquals("/colors no")) {
			// turn terminal colors off for client
			_client.setColorsEnabled(false);
			sendMsg("Colors disabled.", _client.getName(), _client);
			return true;
		} else if (_msg.contentEquals("/colors yes")) {
			// turn terminal colors on for client
			_client.setColorsEnabled(true);
			sendMsg("Colors enabled.", _client.getName(), _client);
			return true;
		} else if (_msg.length() > 3 && _msg.startsWith("/pm")) {
			// send personal message
			String[] split = _msg.split(" ");
			String toUser = split[1];
			String pm = _msg.substring((4 + toUser.length()), _msg.length());
			if (nameExists(toUser)) {
				sendMsg(pm, toUser, _client);
			}
			return true;
		}
		return true;
	}
	public boolean setClientName(String _newName, ClientData _client, boolean _sendServerMsg) {
		// Change name of client, must be unique. Append number if not.
		// Public so ClientData can check name soon after join
		_newName = _newName.replaceAll("\\s+",""); // remove white space
		try {
			String oldName = _client.getName();
			while (nameExists(_newName)) {
				_newName += getRandomNameId();
			}
			_client.setName(_newName);
			if (_sendServerMsg) {
				sendServerMsg(oldName + " changed their name to " + _newName);
			}
		} catch (Exception e) { return false; }
		return true;
	}
	private boolean nameExists(String _nameToCheck) {
		// Checks to see if the name for the client already exists
		for (int i = 0; i < clients.length; i++) {
			if (clients[i] != null) {
				final String clientName = clients[i].getName();
				if (_nameToCheck.contentEquals(clientName)) {
					return true;
				}
			}
		}
		return false;
	}
	private int getRandomNameId() {
		// For anonymous users entering, so names don't match
		final int min = 0;
		final int max = 9;
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}
