package server;

import java.io.IOException;

// This class just checks processes idle time.
// Idle time is reset at every command/message in InputThread
public class IdleThread implements Runnable {
	private ClientData client = null;
	private boolean alive = true;
	private Functions functionsInstance = Server.instance.getFunctionsInstance();
	
	public IdleThread(ClientData _client) {
		client = _client;
	}
	public void run() {
		while (alive && client != null) {
			// tick idle time
			client.processIdle(true);
			
			// slow down, may need to lower for busier servers
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				Server.instance.getMain().getTerm().debug("Error with sleep in OutputThread: " + e);
			}
		}
		kill();
	}
	protected void finalize() {
		alive = false;
		try {
			Server.instance.getFunctionsInstance().killClient(client.getId());
		} catch (Exception e) {}
		client = null;
	}
	public void kill() {
		this.finalize();
	}
}
