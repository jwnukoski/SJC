package server;

import java.net.Socket;

import com.Main;

// Class that contains info for each client, so the server can keep a list of them and other data.
// Main class for interacting with clients and their threads
public class ClientData {
	private int id = 0; // the index of this client in the functions clients list
	private String name = Main.instance.entryName; // display name
	private InputThread inputThread = null; // the input thread for this client on the server
	private OutputThread outputThread = null; // the output thread for this client on the server
	private IdleThread idleThread = null; // checks connection and idleness of client
	private Socket clientSocket = null;
	private String clientIpAddress = "";
	private int colorId = 0;
	private String colorScheme = "";
	private boolean colorsEnabled = false;
	private boolean alive = true;
	private int idleTime = 0;
	private boolean isIdle = false;

	public ClientData(int _id, Socket _clientSocket, String _clientIpAddress) {
		id = _id;
		clientSocket = _clientSocket;
		colorId = Server.instance.getMain().getTerm().getRandomColorId();
		colorScheme = Server.instance.getMain().getTerm().getColorScheme(colorId);

		// Threads
		inputThread = new InputThread(this);
		outputThread = new OutputThread(this);
		idleThread = new IdleThread(this);

		// Add threads
		Server.instance.getExSrv().submit(inputThread);
		Server.instance.getExSrv().submit(outputThread);
		Server.instance.getExSrv().submit(idleThread);

		// Log IP
		clientIpAddress = _clientIpAddress;

		// Tell everyone
		Server.instance.getFunctionsInstance().setClientName(name, this, false);
		Server.instance.getFunctionsInstance().sendServerMsg(name + " has joined the sever.");

	}
	public String getName() {
		return name;
	}
	public void setName(String _name) {
		name = _name;
	}
	public int getId() {
		return id;
	}
	public boolean sendMsg(String _msg, String _to, ClientData _from) {
		// returns if message was added to clients queue
		return outputThread.queueMsg(_msg, _to, _from);
	}
	public boolean sendServerMsg(String _msg) {
		// sends client message from the server
		return outputThread.queueServerMsg(_msg);
	}
	public Socket getSocket() {
		return clientSocket;
	}
	public int getColorId() {
		return colorId;
	}
	public String getColorScheme() {
		return colorScheme;
	}
	public boolean getColorsEnabled() {
		return colorsEnabled;
	}
	public void setColorsEnabled(boolean _colorsEnabled) {
		colorsEnabled = _colorsEnabled;
	}
	public boolean getAlive() {
		return alive;
	}
	public void processIdle(boolean _isIdleProcess) {
		// pass true to increment idle time, false to reset
		if (_isIdleProcess) {
			if (idleTime < Server.clientIdleTime) {
				idleTime++;
			} else {
				isIdle = true;
				Server.instance.getFunctionsInstance().sendServerMsg(name + " went idle.");
			}
		} else {
			if (isIdle) {
				isIdle = false;
				Server.instance.getFunctionsInstance().sendServerMsg(name + " is no longer idle.");
			}
			idleTime = 0;
		}
	}
	public boolean getIdle() {
		return isIdle;
	}
	public void kill() {
		if (alive) {
			alive = false;
			Server.instance.getFunctionsInstance().sendServerMsg(name + " has disconnected.");
			Server.instance.getFunctionsInstance().killClient(id); // make to sure remove from process list

			try {
				clientSocket.close();
			} catch (Exception e) {}
			clientSocket = null;

			
			id = -1;
			name = "";
			clientIpAddress = "";
			colorId = 0;

			inputThread.kill();
			inputThread = null;
			outputThread.kill();
			outputThread = null;
			idleThread.kill();
			idleThread = null;
		}
	}
}
