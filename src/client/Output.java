package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.Encryption;

// Handles writing all users messages to this user.
public class Output implements Runnable {
	private BufferedReader br = null; // reading server output
	
	public void run () {
		try {
			br = new BufferedReader(new InputStreamReader(Client.instance.getSocket().getInputStream()));
		} catch (Exception e) {
			System.out.println("Error creating buffered reader for output: " + e);
		}
		
		while (Client.instance.getSocket() != null && br != null) {
			try {
				String serverMsg = br.readLine();
				
				if (serverMsg == null) {
					System.out.println("Connection lost.");
					break;
				} else {
					System.out.println(Encryption.decrypt(serverMsg)); // print message to client
				}
			} catch (Exception e) {}
		}

		this.finalize();
	}
	
	protected void finalize() {
		br = null;
		
		Client.instance.kill();
	}
}
