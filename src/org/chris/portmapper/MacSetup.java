package org.chris.portmapper;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;

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

		final Application app = Application.getApplication();
		app.setPreferencesHandler(new PreferencesHandler() {
			public void handlePreferences(final PreferencesEvent arg0) {
				PortMapperApp.getInstance().getView().changeSettings();
			}
		});
		app.setAboutHandler(new AboutHandler() {
			public void handleAbout(final AboutEvent arg0) {
				PortMapperApp.getInstance().getView().showAboutDialog();
			}
		});
	}
}
