package org.chris.portmapper.applet;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketPermission;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.chris.portmapper.gui.LogTextArea;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;
import org.chris.portmapper.router.dummy.DummyRouterFactory;
import org.chris.portmapper.router.sbbi.SBBIRouterFactory;
import org.chris.portmapper.router.weupnp.WeUPnPRouterFactory;

/**
 * This class is a basic Java Applet that can connect to a router and add a
 * default port mapping.
 * 
 * @author chris
 * @version $Id$
 */
@SuppressWarnings("serial")
public class PortMapperApplet extends JApplet {

	/**
	 * The text area to which all log messages are appended.
	 */
	private LogTextArea logTextArea;

	/**
	 * The router to which we are connected or <code>null</code>, if we are not
	 * connected to a router.
	 */
	private IRouter router;

	/**
	 * This is <code>true</code>, when the user granted the neccessary
	 * permissions to the applet or <code>false</code> if he did not grant the
	 * permissions.
	 */
	private boolean permissionsGranted = false;

	/**
	 * This method is called when the applet is initialized for the first time.
	 * It checks the permissions and creates the gui widgets.
	 */
	@Override
	public void init() {
		super.init();

		permissionsGranted = permissionsGranted();

		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(createButtonPanel(), BorderLayout.NORTH);
		contentPane.add(createLogPanel(), BorderLayout.CENTER);
		logMessage("Welcome to PortMapperApplet!");
		if (!permissionsGranted) {
			logMessage("Please allow the PortMapperApplet to connect to your router.");
		} else {
			logMessage("Permissions granted.");
		}
	}

	/**
	 * This method creates a panel containing the factory combo box, the connect
	 * button and the add port mapping button.
	 * 
	 * @return the button panel.
	 */
	private JPanel createButtonPanel() {

		final AbstractRouterFactory[] availableFactories = new AbstractRouterFactory[] {
				new SBBIRouterFactory(), new WeUPnPRouterFactory(),
				new DummyRouterFactory() };
		final JComboBox<AbstractRouterFactory> factoryComboBox = new JComboBox<>(
				new Vector<>(Arrays.asList(availableFactories)));
		factoryComboBox.setSelectedIndex(0);

		final JButton connectButton = new JButton("Connect");
		connectButton.setEnabled(permissionsGranted);
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				final AbstractRouterFactory routerFactory = (AbstractRouterFactory) factoryComboBox
						.getSelectedItem();
				connect(routerFactory);
			}
		});

		final JButton addPortMappingButton = new JButton("Add port mapping");
		addPortMappingButton.setEnabled(permissionsGranted);
		addPortMappingButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				addPortMapping();
			}
		});

		final JPanel buttonPanel = new JPanel();
		buttonPanel.add(factoryComboBox);
		buttonPanel.add(connectButton);
		buttonPanel.add(addPortMappingButton);
		return buttonPanel;
	}

	/**
	 * This method creates a panel containing the log message area.
	 * 
	 * @return the log panel.
	 */
	private JComponent createLogPanel() {
		logTextArea = new LogTextArea();

		final JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView(logTextArea);
		return scrollPane;
	}

	/**
	 * Check, if the permissions are granted to connect the router.
	 * 
	 * @return <code>true</code>, if the permissions are granted or
	 *         <code>false</code> if they are not granted.
	 */
	private boolean permissionsGranted() {
		final SocketPermission permission = new SocketPermission(
				"239.255.255.250", "connect,accept,resolve");
		try {
			AccessController.checkPermission(permission);
			return true;
		} catch (final AccessControlException e) {
			return false;
		}
	}

	/**
	 * Tries to connect to the router using the given router factory.
	 * 
	 * @param routerFactory
	 *            the router factory used to connect.
	 */
	private void connect(final AbstractRouterFactory routerFactory) {
		logMessage("Using UPnP library " + routerFactory.toString());
		if (router != null) {
			logMessage("Disconnecting...");
			router.disconnect();
			router = null;
		}

		logMessage("Searching for router...");
		final Collection<IRouter> routers;
		try {
			routers = routerFactory.findRouters();
			logMessage("Found " + routers.size() + " router.");
		} catch (final RouterException e) {
			logMessage("Failed to connect", e);
			return;
		}

		if (routers == null || routers.size() == 0) {
			logMessage("Found no routers");
			return;
		}

		router = routers.iterator().next();
		logMessage("Connected to " + router.getName());

	}

	/**
	 * Adds a default port mapping.
	 */
	private void addPortMapping() {
		if (router == null) {
			logMessage("Not connected. Please connect before adding a mapping.");
			return;
		}
		String internalClient;
		try {
			internalClient = router.getLocalHostAddress();
		} catch (final RouterException e) {
			logMessage("Could not determine the address of your localhost", e);
			return;
		}
		final PortMapping mapping = new PortMapping(Protocol.TCP, null, 12345,
				internalClient, 12345, "Your new port mapping");
		logMessage("Adding port mapping " + mapping);
		try {
			router.addPortMapping(mapping);
		} catch (final RouterException e) {
			logMessage("Could not add port mapping.", e);
			return;
		}
		logMessage("Port mapping created successfully.");
	}

	/**
	 * Appends a log message to the log message area.
	 * 
	 * @param message
	 *            the message to append.
	 */
	private void logMessage(final String message) {
		if (logTextArea != null) {
			logTextArea.addLogMessage(message + "\n");
		} else {
			System.err.println("Log text area not initialized");
			System.out.println(message);
		}
		showStatus(message);
	}

	/**
	 * Appends an error message with the message of the exception.
	 * 
	 * @see #logMessage(String)
	 * @param message
	 *            the message to display.
	 * @param throwable
	 *            the exception.
	 */
	private void logMessage(final String message, final Throwable throwable) {
		logMessage(message + " [" + throwable + "]");
	}

	/**
	 * This method is called when the applet is stopped. It disconnects from the
	 * router.
	 */
	@Override
	public void stop() {
		if (router != null) {
			logMessage("Disconnect from router");
			router.disconnect();
			router = null;
		}
		logMessage("Applet stopped");
		super.stop();
	}
}
