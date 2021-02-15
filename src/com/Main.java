package com;
import client.Client;
import server.Server;

public class Main {
	public static Main instance = null;
	private Terminal terminalInstance = null;
	public final String entryName = "Anonymous"; // Base name used when first joining servers.
	
	public static void main(String[] args) {
		new Main(args);
	}
	
	public Main(String[] args) {
		instance = this;
		boolean terminalColors = false;
		boolean terminalDebug = false;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].contentEquals("--colors")) {
				terminalColors = true;
			} else if (args[i].contentEquals("--debug")) {
				terminalDebug = true;
			}
		}
		terminalInstance = new Terminal(terminalColors, terminalDebug);
		
		if (args.length > 0 && args[0].contentEquals("--server")) {
			System.out.println("Running as server.");
			
			if (args.length >= 3) {
				new Server(args[1], args[2], instance);
			}
		} else if (args.length > 0 && args[0].contentEquals("--client")) {
			System.out.println("Running as client.");

			if (args.length >= 4) {
				new Client(args[1], args[2], args[3], instance);
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
