package com;

import java.util.Random;

// Done this way to easily control if colors are used, or if debug is enabled to display extra info.
public class Terminal {
	private boolean colorsEnabled = false;
	private boolean debug = false;
	
	// Color indexes are to match each others index as a pair
	// Thanks to: https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
	private final String[] colorsFG = { // foreground ANSI colors. length should always match colorsBG
			"\033[0;37m", // white
			"\033[0;31m", // red
			"\033[0;30m", // black
			"\033[0;33m", // yellow
			"\033[0;30m" // black
	}; 
	private final String[] colorsBG = { // background ANSI colors. length should always match colorsFG
			"\033[40m", //black
			"\033[40m", //black
			"\033[42m", // green
			"\033[40m", //black
			"\033[47m" // white
	};
	private static final String ANSI_RESET = "\u001B[0m";
	
	public Terminal(boolean _colorsEnabled, boolean _debug) {
		colorsEnabled = _colorsEnabled;
		debug = _debug;
	}
	public void print(String _msg, int _colorId) {
		// Regular replacement for System.out.println
		if (colorsEnabled && colorsFG[_colorId] != null && colorsBG[_colorId] != null) {
			System.out.println(colorsFG[_colorId] + colorsBG[_colorId] + _msg + ANSI_RESET);
		} else {
			// Reset
			System.out.println(_msg);
		}
	}
	public void debug(String _msg) {
		// print only if debug is on
		if (debug) {
			print("Debug: " + _msg, 0);
		}
	}
	public String[] getColorsFG() {
		return colorsFG;
	}
	public String[] getColorsBG() {
		return colorsBG;
	}
	public String getColorScheme(int _id) {
		return colorsFG[_id] + colorsBG[_id];
	}
	public int getRandomColorId() {
		final int min = 0;
		int max = colorsFG.length - 1;
		if (colorsBG.length < max) {
			max = colorsBG.length;
		}
		
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	public String getAnsiReset() {
		return ANSI_RESET;
	}
	public boolean getColorsEnabled() {
		return colorsEnabled;
	}
	public boolean getDebug() {
		return debug;
	}
}