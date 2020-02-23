package server;

import java.net.ServerSocket;
import java.net.Socket;

// Handles all requests from clients and their threads. Commands and etc
public class ClientHandler implements Runnable {
	private ServerSocket serverSocket = null;
	private boolean run = true;
	
	public ClientHandler() {}
	public void run() {
		// Create listening socket
		while (serverSocket == null) {
			try {
				serverSocket = new ServerSocket(Server.instance.getServerPort());
				System.out.println("Port " + Server.instance.getServerPort() + " opened for clients.");

				while (run) {
					try {
						Socket clientSocket = serverSocket.accept(); // new socket
						Server.instance.getFunctionsInstance().addClient(clientSocket, clientSocket.getRemoteSocketAddress().toString()); // add to clients list in functions, and create everything
					} catch (Exception e) {
						System.out.println("Client accept failed: " + e);
					}
				}
				
				try {
					serverSocket.close();
				} catch (Exception e) {
					System.out.println("Failed closing serverSocket: " + e);
				}
			} catch (Exception e) {
				System.out.println("Problem with opening socket on port " + Server.instance.getServerPort() + ". Trying again in ~10 secs...");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					System.out.println("Problem sleeping request handler thead! " + e1);
				}
			}
		}
	}
	protected void finalize() {
		try {
			serverSocket.close();
		} catch (Exception e) {
			System.out.println("Could not close something in finalize: " + e);
		}
		
		serverSocket = null;
	}
	public void kill() {
		finalize();
	}
}
