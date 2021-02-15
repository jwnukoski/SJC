package server;

import java.net.Socket;

import com.Main;

// Class that contains info for each client, so the server can keep a list of them and other data.
// Main class for interacting with clients and their threads
public class ClientData {
	private int id = 0;
	private String name = Main.instance.entryName;
	private InputThread inputThread = null;
	private OutputThread outputThread = null;
	private IdleThread idleThread = null;
	private Socket clientSocket = null;
	private String clientIpAddress = "";
	private int colorId = 0;
	private String colorScheme = "";
	private boolean colorsEnabled = false;
	private boolean alive = true;
	private int idleTime = 0;
	private boolean isIdle = false;
	private boolean verifiedPassword = false;

	public ClientData(int _id, Socket _clientSocket, String _clientIpAddress) {
		id = _id;
		clientSocket = _clientSocket;
		colorId = Server.instance.getMain().getTerm().getRandomColorId();
		colorScheme = Server.instance.getMain().getTerm().getColorScheme(colorId);

		inputThread = new InputThread(this);
		outputThread = new OutputThread(this);
		idleThread = new IdleThread(this);

		Server.instance.getExSrv().submit(inputThread);
		Server.instance.getExSrv().submit(outputThread);
		Server.instance.getExSrv().submit(idleThread);

		clientIpAddress = _clientIpAddress;
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
		return outputThread.queueMsg(_msg, _to, _from);
	}
	
	public boolean sendServerMsg(String _msg) {
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
		if (_isIdleProcess) {
			if (idleTime < Server.clientIdleTime) {
				idleTime++;
			} else {
				if (!isIdle) {
					isIdle = true;
					Server.instance.getFunctionsInstance().sendServerMsg(name + " went idle.");
				}
			}
		} else {
			if (isIdle) {
				isIdle = false;
				Server.instance.getFunctionsInstance().sendServerMsg(name + " is no longer idle.");
			}
			idleTime = 0;
		}
	}
	
	public boolean verifyPassword(String _input) {
		if (!verifiedPassword) { 
			if (_input.contentEquals(Server.instance.getHashedPassword())) {
				verifiedPassword = true;
				Server.instance.getFunctionsInstance().setClientName(name, this, false);
				Server.instance.getMain().getTerm().debug("Authenticated user from IP: " + clientIpAddress);
			} else {
				Server.instance.getMain().getTerm().debug("Bad password from IP: " + clientIpAddress);
				this.kill();
			}
		}
		return verifiedPassword;
	}
	
	public boolean getVerifiedPassword() {
		return verifiedPassword;
	}
	
	public boolean getIdle() {
		return isIdle;
	}
	
	public void kill() {
		if (alive) {
			alive = false;
			
			if (verifiedPassword)
				Server.instance.getFunctionsInstance().sendServerMsg(name + " has disconnected.");
			
			Server.instance.getFunctionsInstance().killClient(id);

			try {
				clientSocket.close();
			} catch (Exception e) {}
			clientSocket = null;

			id = -1;
			name = "";
			clientIpAddress = "";
			colorId = 0;
			verifiedPassword = false;

			inputThread.kill();
			inputThread = null;
			outputThread.kill();
			outputThread = null;
			idleThread.kill();
			idleThread = null;
		}
	}
}
