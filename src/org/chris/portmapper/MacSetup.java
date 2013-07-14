package org.chris.portmapper;

/**
 * This class contains mac specific settings for the application name and the
 * application menu.
 * 
 * @author chris
 * 
 */
public class MacSetup {

	public static void setupMac() {
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				"UPnP PortMapper");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.brushMetalLook", "false");
		System.setProperty("com.apple.mrj.application.growbox.intrudes",
				"false");
		System.setProperty("com.apple.mrj.application.live-resize", "true");
	}
}
