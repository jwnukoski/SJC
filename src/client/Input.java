package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.Main;

// Handles user input to server
public class Input implements Runnable {
	private BufferedReader br = null; // reading user input
	private PrintWriter pr = null; // sending message to server
	
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
				if (Client.instance.getMain().getTerm().getColorsEnabled()) {
					pr.println("/colors yes");
				} else {
					pr.println("/colors no");
				}
				pr.println("/name " + Main.instance.entryName);
			} catch (Exception e) {
				System.out.println("Error sending intial info. Client settings will default: " + e);
			}
		}
		// Main loop
		while (Client.instance.getSocket() != null && br != null && pr != null) {
			try {
				
				String msg = br.readLine(); // read user input (loop)
				if (msg.contentEquals("/quit")) {
					System.out.println("Quitting...");
					Client.instance.kill();
				} else {
					pr.println(msg); // send input to server
				}
			} catch (Exception e) {}
		}
		this.finalize();
	}
	
	protected void finalize() {
		br = null;
		pr = null;
		
		Client.instance.kill(); // already called by output
	}
}
