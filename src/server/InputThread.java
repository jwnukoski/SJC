package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class InputThread implements Runnable {
	private ClientData client = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;

	public InputThread(ClientData _client) {
		client = _client;
	}
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
		} catch (IOException e) {
			Server.instance.getMain().getTerm().debug("Client buffer reader failed to start in input thread: " + e);
		}
		
		try {
			pw = new PrintWriter(client.getSocket().getOutputStream(), true);
		} catch (IOException e) {
			Server.instance.getMain().getTerm().debug("Client print writer failed to start in input thread: " + e);
		}
		
		// Welcome message
		pw.println("Welcome!\nUse /help for commands.");
		
		while (client.getSocket() != null) {
			try {
				// TODO: Change to Message class. Allow from PMs
				String clientMsg = br.readLine(); // Read client messages
				if (Server.instance.getFunctionsInstance().isCommand(clientMsg)) {
					Server.instance.getFunctionsInstance().processCommand(clientMsg, client);
				} else {
					Server.instance.getFunctionsInstance().sendMsg(clientMsg, "PUBLIC", client); // send message to all clients
				}
			} catch (IOException e) {
				Server.instance.getMain().getTerm().debug("Error in client input thread: " + e);
				break;
			}
		}
		this.finalize();
	}
	protected void finalize() {
		client = null;
		br = null;
		pw = null;
	}
	public void kill() {
		this.finalize();
	}
}