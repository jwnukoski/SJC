package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import com.Encryption;

// Handles user input to server
public class Input implements Runnable {
	private BufferedReader br = null;
	private PrintWriter pr = null;
	
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
		} catch (Exception e) {
			System.out.println("Error creating buffered reader for input: " + e);
		}
		
		try {
			pr = new PrintWriter(Client.instance.getSocket().getOutputStream(), true);
		} catch (Exception e) {
			System.out.println("Error creating print writer for input: " + e);
		}
		
		// Send initial data like color setting
		if (Client.instance.getSocket() != null && br != null && pr != null) {
			try {
				// Send user input password
				pr.println(Encryption.encrypt("/auth " + Client.instance.getServerHashedPassword()));
				
				// Send color info
				if (Client.instance.getMain().getTerm().getColorsEnabled()) {
					pr.println(Encryption.encrypt("/colors yes"));
				} else {
					pr.println(Encryption.encrypt("/colors no"));
				}
			} catch (Exception e) {
				System.out.println("Error sending intial info. Client settings will default: " + e);
			}
		}
		
		// Main loop
		while (Client.instance.getSocket() != null && br != null && pr != null) {
			try {
				String msg = br.readLine(); // user input
				if (msg.contentEquals("/quit")) {
					pr.println(Encryption.encrypt(msg));
					System.out.println("Quitting...");
					Client.instance.kill();
				} else {
					pr.println(Encryption.encrypt(msg)); // send input to server
				}
			} catch (Exception e) {}
		}
		this.finalize();
	}
	
	protected void finalize() {
		br = null;
		pr = null;
		Client.instance.kill();
	}
}
