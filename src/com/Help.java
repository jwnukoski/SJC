package com;

public class Help {
	private static final String genHelpMsg = "Use --help to view the help doc.";
	private static final String helpDoc = "Client Usage:"
			+ "\n"
			+ "--client [server ip] [server port] [server password] [your username]"
			+ "\n\n"
			+ "Server Usage:"
			+ "\n"
			+ "--server [server port] [server password]"
			+ "\n"
			+ "--colors (Default is disabled, use to enable)"
			+ "\n"
			+ "--debug (Default is disabled, use to enable. Mostly only useful for servers.)";
	
	public static void printHelpDoc() {
		System.out.println(helpDoc);
	}
	public static void printHelpMsg(String _error) {
		System.out.println(_error + "\n" + genHelpMsg);
	}
}
