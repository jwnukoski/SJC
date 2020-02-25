package server;

import java.net.Socket;

// Class that contains info for each client, so the server can keep a list of them and other data.
// Main class for interacting with clients and their threads
public class ClientData {
	private int id = 0; // the index of this client in the functions clients list
	private String name = "Anonymous"; // display name
	private InputThread inputThread = null; // the input thread for this client on the server
	private OutputThread outputThread = null; // the output thread for this client on the server
	private Socket clientSocket = null;
	private String clientIpAddress = "";
	private int colorId = 0;
	private boolean colorsEnabled = false;
	
	public ClientData(int _id, Socket _clientSocket, String _clientIpAddress) {
		id = _id;
		clientSocket = _clientSocket;
		colorId = Server.instance.getMain().getTerm().getRandomColorId();
		
		// Threads
		inputThread = new InputThread(this);
		outputThread = new OutputThread(this);
		
		// Add threads
		Server.instance.getExSrv().submit(inputThread);
		Server.instance.getExSrv().submit(outputThread);

		// Log IP
		clientIpAddress = _clientIpAddress;
		Server.instance.getMain().getTerm().debug("Client connected from: " + clientIpAddress);
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
	public boolean sendMsg(Message _msg) {
		// returns if message was added to clients queue
		return outputThread.queueMsg(_msg);
	}
	public Socket getSocket() {
		return clientSocket;
	}
	public int getColorId() {
		return colorId;
	}
	public boolean getColorsEnabled() {
		return colorsEnabled;
	}
	public void setColorsEnabled(boolean _colorsEnabled) {
		colorsEnabled = _colorsEnabled;
	}
	public void kill() {
		id = -1;
		name = "";
		clientIpAddress = "";
		colorId = 0;
		
		inputThread.kill();
		inputThread = null;
		outputThread.kill();
		outputThread = null;
		
		try {
			clientSocket.close();
		} catch (Exception e) {}
		clientSocket = null;
	}
}
