package org.chris.portmapper;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.gui.MainWindow;
import org.chris.portmapper.gui.Messages;
import org.chris.portmapper.router.PortMapping;
import org.chris.portmapper.router.Router;
import org.chris.portmapper.router.RouterException;

public class Application {
	private Log logger = LogFactory.getLog(this.getClass());
	private MainWindow mainWindow;
	private Router router;

	public enum ApplicationState {
		NOT_CONNECTED, CONNECTING, CONNECTED
	};

	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public Application() {
		mainWindow = new MainWindow(this);
		mainWindow.setApplicationState(ApplicationState.NOT_CONNECTED);
		logger.info("Welcome to UPNP PortMapper");

		connectRouterThreaded();
	}

	private void connectRouter() {
		boolean tryAgain = true;
		mainWindow.setApplicationState(ApplicationState.CONNECTING);
		while (this.router == null && tryAgain) {
			try {
				this.router = Router.findRouter();
				logger.info("Connected to router " + router.getName());
			} catch (RouterException e) {
				logger.error("Could not connect to router", e);
				int answer = JOptionPane.showConfirmDialog(getMainWindow()
						.getFrame(), "Could not find router:\n"
						+ e.getMessage() + "\n\n" + "Try connecting again?",
						"Connection failed", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				tryAgain = (answer == JOptionPane.OK_OPTION);
			}
		}
		if (this.router != null) {
			mainWindow.setApplicationState(ApplicationState.CONNECTED);
			updateRouterIPAddresses();
			updatePortMappings();
		}
	}

	private void connectRouterThreaded() {
		Thread connectThread = new Thread() {
			public void run() {
				connectRouter();
			}
		};
		connectThread.start();
	}

	public void updateRouterIPAddresses() {
		// this.externalIPLabel.setText("");
		if (router == null) {
			mainWindow.setExternalIPLabel(Messages
					.getString("MainWindow.not_connected"));
			mainWindow.setInternalIPLabel(Messages
					.getString("MainWindow.not_connected"));
		} else {
			mainWindow.setExternalIPLabel(Messages
					.getString("MainWindow.updating"));
			mainWindow.setInternalIPLabel(Messages
					.getString("MainWindow.updating"));
			mainWindow.setInternalIPLabel(router.getInternalIPAddress());
			try {
				mainWindow.setExternalIPLabel(router.getExternalIPAddress());
			} catch (RouterException e) {
				logger.error("Did not get exteranl IP address", e);
				mainWindow.setExternalIPLabel("(Error)");
			}
		}
	}

	public void reconnect() {
		mainWindow.setApplicationState(ApplicationState.NOT_CONNECTED);
		if (router != null) {
			router.disconnect();
			router = null;
		}
		this.getMainWindow().setPortMappings(new LinkedList<PortMapping>());
		connectRouterThreaded();
	}

	public void addMapping(PortMapping portMapping) {
		boolean success = false;
		try {
			success = this.getRouter().addPortMapping(portMapping);
		} catch (RouterException e) {
			logger.error("Could not add port mapping", e);
		}

		if (success) {
			logger.info("Portmapping was added successfully");
		} else {
			logger.warn("Portmapping was NOT added successfully");
			JOptionPane.showMessageDialog(this.getMainWindow().getFrame(),
					"The port mapping could not be added.",
					"Error adding port mapping", JOptionPane.WARNING_MESSAGE);
		}
		this.updatePortMappings();
	}

	public void removeMapping() {
		Collection<PortMapping> selectedMappings = this.getMainWindow()
				.getSelectedPortMappings();
		for (PortMapping mapping : selectedMappings) {
			logger.info("Removing mapping " + mapping);
			boolean success = false;
			try {
				success = router.removeMapping(mapping);
			} catch (RouterException e) {
				logger.error("Could not remove port mapping " + mapping, e);
				break;
			}
			if (success) {
				logger.info("Mapping was removed successfully: " + mapping);
			} else {
				logger
						.error("Mapping was not removed successfully: "
								+ mapping);
				break;
			}
		}
		if (selectedMappings.size() > 0) {
			updatePortMappings();
		}
	}

	public void displayRouterInfo() {
		try {
			router.logRouterInfo();
		} catch (RouterException e) {
			logger.error("Could not get router info", e);
		}
	}

	public void updatePortMappings() {
		try {
			Collection<PortMapping> mappings = router.getPortMappings();
			logger.info("Found " + mappings.size() + " mappings");
			this.getMainWindow().setPortMappings(mappings);
		} catch (RouterException e) {
			logger.error("Could not get port mappings", e);
		}
	}

	public Router getRouter() {
		return router;
	}
}
