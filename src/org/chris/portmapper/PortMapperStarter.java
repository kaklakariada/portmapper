/**
 * 
 */
package org.chris.portmapper;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;

/**
 * @author chris
 * @version $Id$
 */
public class PortMapperStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (System.getProperty("os.name").startsWith("Mac")) {
			setupMacOptions();
		}
		PortMapperCli cli = new PortMapperCli();
		cli.start(args);
	}

	private static void setupMacOptions() {
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				"UPnP PortMapper");
		System.setProperty("com.apple.macos.useScreenMenuBar", "true");
		System.setProperty("apple.awt.brushMetalLook", "false");
		System.setProperty("com.apple.mrj.application.growbox.intrudes",
				"false");
		System.setProperty("com.apple.mrj.application.live-resize", "true");

		Application app = Application.getApplication();
		app.setPreferencesHandler(new PreferencesHandler() {
			public void handlePreferences(PreferencesEvent arg0) {
				PortMapperApp.getInstance().getView().changeSettings();
			}
		});
		app.setAboutHandler(new AboutHandler() {
			public void handleAbout(AboutEvent arg0) {
				PortMapperApp.getInstance().getView().showAboutDialog();
			}
		});
		//
		// File iconFile = new File("internet_device_256.png");
		// try {
		// app.setDockIconImage(ImageIO.read(iconFile));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}
