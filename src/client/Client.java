package client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.Socket;
import com.Main;


public class Client {
	public static Client instance = null;
	private Main mainInstance = null;
	
	private String serverIp = "";
	private int serverPort = 0;
	
	private Input requestsInstance = null;
	private Output notificationsInstance = null;
	private ExecutorService exSrv = Executors.newCachedThreadPool();
	
	private Socket socket = null;
	
	public Client(String _serverIp, String _serverPort, Main _main) {
		instance = this;
		mainInstance = _main;
		
		serverIp = _serverIp;
		serverPort = Integer.parseInt(_serverPort);
		System.out.println("Accepted: Server IP: " + serverIp + ", Server Port: " + serverPort);
		
		// Handles the connection to the server
		while (true) {
			try {
				socket = new Socket(serverIp, serverPort);
				System.out.println("Successfully connected to: " + serverIp + ":" + serverPort);
				break;
			} catch (Exception e) {
				System.out.println("\nCannot connect to: " + serverIp + ":" + serverPort + ". Is the server up?\n" + e + "\nTrying again in 10 seconds.\n");
				try {
					Thread.sleep(10000);
				} catch (Exception e1) {
					System.out.println(e1);
				}
			}
		}

		// Start threads
		requestsInstance = new Input();
		exSrv.submit(requestsInstance);
		notificationsInstance = new Output();
		exSrv.submit(notificationsInstance);
	}
	protected void finalize() {
		System.out.println("Stopping system.");
		try {
			socket.close();
		} catch (Exception e) {
			System.out.println("Error closing socket. Likely already closed: " + e);
		}
		
		try {
			socket = null;
			exSrv.shutdownNow();
			requestsInstance = null;
			notificationsInstance = null;
		} 
		catch (Exception e) {
			System.out.println("Error in client finalize: " + e);
			System.exit(-1);
		}
		
		System.exit(0);
	}
	public String getServerIp() {
		return serverIp;
	}
	public int getServerPort() {
		return serverPort;
	}
	public Input getRequestsInstance() {
		return requestsInstance;
	}
	public Output getNotificationsInstance() {
		return notificationsInstance;
	}
	public Socket getSocket() {
		return socket;
	}
	public Main getMain() {
		// here so we don't have to import main to every class using terminal
		// Client.getMain().getTerm().print();
		// Client.getMain().getTerm().debug();
		return mainInstance;
	}
	public void kill() {
		finalize();
	}
}