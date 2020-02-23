// http://www.oracle.com/technetwork/java/socket-140484.html
package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Class handles creating the different main threads
public class Server {
	// Increase these for busier servers
	public static final int maxConnections = 100;
	public static final int messageInterval = 100;
	public static final int clientMsgQueue = 100;
	
	public static Server instance = null;
	private int serverPort = 0;
	
	private ClientHandler clientHandlerInstance = null; // dishes out the client threads
	private Functions functionsInstance = null; // back-end processes
	
	private ExecutorService exSrv = Executors.newCachedThreadPool();

	public Server(String _serverPort) {
		instance = this;
		
		serverPort = Integer.parseInt(_serverPort);
		System.out.println("Set server port to: " + _serverPort);
		
		functionsInstance = new Functions();
		exSrv.submit(functionsInstance);
		clientHandlerInstance = new ClientHandler();
		exSrv.submit(clientHandlerInstance);
		//notifyHandler = new NotifyHandler();
		//exSrv.submit(notifyHandler);
	}
	protected void finalize() {
		try {
			exSrv.shutdownNow();
			clientHandlerInstance.kill();
			clientHandlerInstance = null;
			functionsInstance.kill();
			functionsInstance = null;
		} 
		catch (Exception e) {
			System.out.println("Could not close something in finalize:" + e);
			System.exit(-1);
		}
	}
	public int getServerPort() {
		return serverPort;
	}
	public ClientHandler getClientHandlerInstance() {
		return clientHandlerInstance;
	}
	public Functions getFunctionsInstance() {
		return functionsInstance;
	}
	public ExecutorService getExSrv() {
		return exSrv;
	}
	public void kill() {
		this.finalize();
	}
}
