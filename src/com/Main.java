package com;
import client.Client;
import server.Server;

// Start of the program. Handles arguments and starts the main client or server class.
public class Main {
	public static Main instance = null;
	private Terminal terminalInstance = null;
	public final String entryName = "Anonymous"; // Base name used when first joining servers.
	
	// Start
	public static void main(String[] args) {
		new Main(args);
	}
	public Main(String[] args) {
		instance = this;
		// Main arguments
		// Check all arguments for colors and options. Start 'terminal' first.
		boolean terminalColors = false;
		boolean terminalDebug = false;
		for (var i = 0; i < args.length; i++) {
			if (args[i].contentEquals("--colors")) {
				terminalColors = true;
			} else if (args[i].contentEquals("--debug")) {
				terminalDebug = true;
			}
		}
		terminalInstance = new Terminal(terminalColors, terminalDebug);
		
		// Use first argument to decide how to run. Must equal 'server'. Strict on options here.
		if (args.length > 0 && args[0].contentEquals("--server")) {
			System.out.println("Running as server.");
			// Check for and pass the server port. Always 127.0.0.1
			if (args.length >= 2) {
				new Server(args[1], instance);
			}
		} else if (args.length > 0 && args[0].contentEquals("--client")) {
			System.out.println("Running as client.");
			// Check for and pass the server IP and port.
			if (args.length >= 3) {
				new Client(args[1], args[2], instance);
			} else {
				Help.printHelpMsg("You must pass an IP then a port for a client connection.\nEx: --client 192.168.1.1 99");
			}
		} else if (args.length > 0 && args[0].contentEquals("--help")) {
			Help.printHelpDoc();
		}
		else {
			Help.printHelpMsg("You must pass --client or --server to pick how to run this program.");
		}
	}
	public Terminal getTerm() {
		return terminalInstance;
	}
}
