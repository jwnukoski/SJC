package server;

import java.net.ServerSocket;
import java.net.Socket;

// Handles client connections and adding them to the threads.
public class ClientHandler implements Runnable {
	private ServerSocket serverSocket = null;
	private boolean alive = true;
	
	public ClientHandler() {}
	public void run() {
		while (serverSocket == null) {
			try {
				serverSocket = new ServerSocket(Server.instance.getServerPort());
				System.out.println("Port " + Server.instance.getServerPort() + " opened for clients.");

				while (alive) {
					try {
						Socket clientSocket = serverSocket.accept();
						Server.instance.getFunctionsInstance().addClient(clientSocket, clientSocket.getRemoteSocketAddress().toString());
					} catch (Exception e) {
						Server.instance.getMain().getTerm().debug("Client accept failed: " + e);
					}
				}
				
				try {
					serverSocket.close();
				} catch (Exception e) {
					Server.instance.getMain().getTerm().debug("Failed closing serverSocket: " + e);
				}
			} catch (Exception e) {
				Server.instance.getMain().getTerm().debug("Problem with opening socket on port " + Server.instance.getServerPort() + ". Trying again in ~10 secs...");
				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					Server.instance.getMain().getTerm().debug("Problem sleeping request handler thead! " + e1);
				}
			}
		}
	}
	protected void finalize() {
		alive = false;
		
		try {
			serverSocket.close();
		} catch (Exception e) {
			Server.instance.getMain().getTerm().debug("Could not close something in finalize: " + e);
		}
		
		serverSocket = null;
	}
	public void kill() {
		finalize();
	}
}
