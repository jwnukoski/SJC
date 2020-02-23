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
	
	public ClientData(int _id, Socket _clientSocket, String _clientIpAddress) {
		// Status should remain blank at creation. 'finished' to be marked for removal.
		id = _id;
		clientSocket = _clientSocket;
		clientIpAddress = _clientIpAddress;
		
		// Threads
		inputThread = new InputThread(this);
		outputThread = new OutputThread(this);
		
		// Add threads
		Server.instance.getExSrv().submit(inputThread);
		Server.instance.getExSrv().submit(outputThread);
		
		System.out.println("Client connected from: " + clientIpAddress);
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
	public void kill() {
		id = -1;
		name = "";
		clientIpAddress = "";
		
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
