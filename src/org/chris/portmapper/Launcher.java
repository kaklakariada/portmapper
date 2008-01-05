package org.chris.portmapper;

import javax.swing.SwingUtilities;

public class Launcher {
	public static void main(String[] args) {

		// Locale.setDefault(Locale.GERMAN);

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Application();
			}
		});
	}
}
