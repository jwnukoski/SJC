package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.Main;

// Thread for every client
// Reads client messages and sends them to the server
public class InputThread implements Runnable {
	private ClientData client = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;
	private boolean alive = true;

	public InputThread(ClientData _client) {
		client = _client;
	}
	public void run() {
		while (alive && client != null) {
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

			while (client != null && client.getSocket() != null && client.getAlive()) {
				try {
					String clientMsg = br.readLine(); // Read client messages
					if (clientMsg != null) {
						System.out.println(clientMsg);
						if (Server.instance.getFunctionsInstance().isCommand(clientMsg)) {
							client.processIdle(false); // reset idle time
							Server.instance.getFunctionsInstance().processCommand(clientMsg, client); // send command
						} else {
							client.processIdle(false); // reset idle time
							Server.instance.getFunctionsInstance().sendMsg(clientMsg, "PUBLIC", client); // send message to all clients
						}
					} else {
						// client disconnected
						Server.instance.getFunctionsInstance().killClient(client.getId());
					}
				} catch (IOException e) {
					Server.instance.getMain().getTerm().debug("Error in client input thread: " + e + "\nKilling client connection for: " + client.getName());
					Server.instance.getFunctionsInstance().killClient(client.getId());
					break;
				}
			}
		}
		kill();
	}
	protected void finalize() {
		alive = false;
		
		// make sure client dies
		try {
			Server.instance.getFunctionsInstance().killClient(client.getId());
		} catch (Exception e) {}
		
		client = null;
		br = null;
		pw = null;
	}
	public void kill() {
		this.finalize();
	}
}