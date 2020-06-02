// http://www.oracle.com/technetwork/java/socket-140484.html
package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.Main;
import com.Hash;

// Class handles creating the different main threads
public class Server {
	// Increase these for busier servers
	public static final int maxConnections = 100;
	public static final int messageInterval = 1000;
	public static final int clientMsgQueue = 100;
	public static final int clientIdleTime = 900; // about 15 minutes
	public static Server instance = null;
	private int serverPort = 0;
	private ClientHandler clientHandlerInstance = null; // dishes out the client threads
	private Functions functionsInstance = null; // back-end processes
	private ExecutorService exSrv = Executors.newCachedThreadPool();
	private Main mainInstance = null;
	private String serverHashedPassword = "";
	private final String welcomeMessage = "Welcome!\nUse /help to get a list of commands.";

	public Server(String _serverPort, String _serverHashedPassword, Main _mainInstance) {
		instance = this;
		mainInstance = _mainInstance;
		serverHashedPassword = Hash.hash(_serverHashedPassword);
		System.out.println("password given: " + _serverHashedPassword + "\nserver password: " + serverHashedPassword);
		
		serverPort = Integer.parseInt(_serverPort);
		Server.instance.getMain().getTerm().debug("Set server port to: " + serverPort);
		
		functionsInstance = new Functions();
		
		// Thread handles client connections then makes their individual threads
		clientHandlerInstance = new ClientHandler();
		exSrv.submit(clientHandlerInstance);
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
			Server.instance.getMain().getTerm().debug("Could not close something in finalize:" + e);
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
	public String getHashedPassword() {
		return serverHashedPassword;
	}
	public String getWelcomeMessage() {
		return welcomeMessage;
	}
	public Main getMain() {
		// here so we don't have to import main to every class using terminal
		// Client.getMain().getTerm().print();
		// Client.getMain().getTerm().debug();
		return mainInstance;
	}
	public void sendServerMsg(String _msg) {
		// send a message to all connected clients
		
	}
	public void kill() {
		this.finalize();
	}
}
