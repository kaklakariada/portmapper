/**
 * 
 */
package org.chris.portmapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.EventObject;

import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.chris.portmapper.gui.PortMapperView;
import org.chris.portmapper.logging.TextAreaWriter;
import org.chris.portmapper.model.PortMappingPreset;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

/**
 * @author chris
 * 
 */
public class PortMapperApp extends SingleFrameApplication {

	/**
	 * The name of the system property which will be used as the directory where
	 * all configuration files will be stored.
	 */
	private static final String CONFIG_DIR_PROPERTY_NAME = "portmapper.config.dir";

	/**
	 * The file name for the settings file.
	 */
	private static final String SETTINGS_FILENAME = "settings.xml";

	private final Log logger = LogFactory.getLog(this.getClass());

	private IRouter router;
	private Settings settings;
	private TextAreaWriter logWriter;

	/**
	 * @see org.jdesktop.application.Application#startup()
	 */
	@Override
	protected void startup() {

		initTextAreaLogger();

		setCustomConfigDir();

		loadSettings();

		PortMapperView view = new PortMapperView();
		addExitListener(new ExitListener() {
			public boolean canExit(EventObject arg0) {
				return true;
			}

			public void willExit(EventObject arg0) {
				disconnectRouter();
			}
		});

		show(view);
	}

	/**
	 * Read the system property with name
	 * {@link PortMapperApp#CONFIG_DIR_PROPERTY_NAME} and change the local
	 * storage directory if the property is given and points to a writable
	 * directory. If there is a directory named <code>PortMapperConf</code> in
	 * the current directory, use this as the configuration directory.
	 */
	private void setCustomConfigDir() {
		String customConfigurationDir = System
				.getProperty(CONFIG_DIR_PROPERTY_NAME);
		File portableAppConfigDir = new File("PortMapperConf");

		// the property is set: check, if the given directory can be used
		if (customConfigurationDir != null) {
			File dir = new File(customConfigurationDir);
			if (!dir.isDirectory()) {
				logger.error("Custom configuration directory '"
						+ customConfigurationDir + "' is not a directory.");
				System.exit(1);
			}
			if (!dir.canRead() || !dir.canWrite()) {
				logger
						.error("Can not read or write to custom configuration directory '"
								+ customConfigurationDir + "'.");
				System.exit(1);
			}
			logger.info("Using custom configuration directory '"
					+ dir.getAbsolutePath() + "'.");
			getContext().getLocalStorage().setDirectory(dir);

			// check, if the portable app directory exists and use this one
		} else if (portableAppConfigDir.isDirectory()
				&& portableAppConfigDir.canRead()
				&& portableAppConfigDir.canWrite()) {
			logger.info("Found portable app configuration directory '"
					+ portableAppConfigDir.getAbsolutePath() + "'.");
			getContext().getLocalStorage().setDirectory(portableAppConfigDir);

			// use the default configuration directory
		} else {
			logger.info("Using default configuration directory '"
					+ getContext().getLocalStorage().getDirectory()
							.getAbsolutePath() + "'.");
		}
	}

	/**
	 * Load the application settings from file
	 * {@link PortMapperApp#SETTINGS_FILENAME} located in the configuration
	 * directory.
	 */
	private void loadSettings() {
		logger.debug("Loading settings from file " + SETTINGS_FILENAME);
		try {
			settings = (Settings) getContext().getLocalStorage().load(
					SETTINGS_FILENAME);
		} catch (IOException e) {
			logger.warn("Could not load settings from file", e);
		}

		if (settings == null) {
			logger
					.debug("Settings were not loaded from file: create new settings");
			settings = new Settings();
		} else {
			logger.debug("Got settings " + settings);
			this.setLogLevel(settings.getLogLevel());
		}
	}

	private void initTextAreaLogger() {
		WriterAppender writerAppender = (WriterAppender) Logger.getLogger(
				"org.chris.portmapper").getAppender("jtextarea");
		logWriter = new TextAreaWriter();
		writerAppender.setWriter(logWriter);
	}

	public void setLoggingTextArea(JTextArea textArea) {
		this.logWriter.setTextArea(textArea);
	}

	@Override
	protected void shutdown() {
		super.shutdown();
		logger.debug("Saving settings " + settings + " to file "
				+ SETTINGS_FILENAME);
		if (logger.isTraceEnabled()) {
			for (PortMappingPreset preset : settings.getPresets()) {
				logger.trace("Saving port mapping "
						+ preset.getCompleteDescription());
			}
		}
		try {
			getContext().getLocalStorage().save(settings, SETTINGS_FILENAME);
		} catch (IOException e) {
			logger.warn("Could not save settings to file", e);
		}
	}

	public static PortMapperApp getInstance() {
		return SingleFrameApplication.getInstance(PortMapperApp.class);
	}

	public static ResourceMap getResourceMap() {
		return PortMapperApp.getInstance().getContext().getResourceMap();
	}

	public PortMapperView getView() {
		return (PortMapperView) PortMapperApp.getInstance().getMainView();
	}

	public boolean connectRouter() throws RouterException {
		if (this.router != null) {
			logger
					.warn("Already connected to router. Cannot create a second connection.");
			return false;
		}

		IRouterFactory routerFactory;
		try {
			routerFactory = createRouterFactory();
		} catch (RouterException e) {
			logger.error("Could not create router factory", e);
			return false;
		}
		logger.info("Searching for router...");
		this.router = routerFactory.findRouter();

		if (router == null) {
			throw new RouterException("Did not find a router");
		}

		try {
			logger.info("Connected to router " + router.getName());
		} catch (RouterException e) {
			throw new RouterException("Could not get router name", e);
		}

		boolean isConnected = this.router != null;
		this.getView().fireConnectionStateChange();
		return isConnected;
	}

	@SuppressWarnings("unchecked")
	private IRouterFactory createRouterFactory() throws RouterException {
		Class<IRouterFactory> routerFactoryClass;
		logger.info("Creating router factory for class "
				+ settings.getRouterFactoryClassName());
		try {
			routerFactoryClass = (Class<IRouterFactory>) Class.forName(settings
					.getRouterFactoryClassName());
		} catch (ClassNotFoundException e1) {
			throw new RouterException(
					"Did not find router factory class for name "
							+ settings.getRouterFactoryClassName(), e1);
		}

		IRouterFactory routerFactory;
		logger.debug("Creating a new instance of the router factory class "
				+ routerFactoryClass.getName());
		try {
			Constructor<IRouterFactory> constructor = routerFactoryClass
					.getConstructor(new Class[0]);
			logger.debug("Found constructor, invoke it");
			routerFactory = constructor.newInstance(new Object[0]);
		} catch (Exception e) {
			throw new RouterException(
					"Could not create a router factory for name "
							+ settings.getRouterFactoryClassName(), e);
		}
		logger.debug("Router factory created");
		return routerFactory;
	}

	/**
	 * @return
	 */
	public boolean disconnectRouter() {
		if (this.router == null) {
			logger.warn("Not connected to router. Can not disconnect.");
			return false;
		}

		this.router.disconnect();
		this.router = null;
		this.getView().fireConnectionStateChange();

		return true;
	}

	public IRouter getRouter() {
		return router;
	}

	public Settings getSettings() {
		return settings;
	}

	public boolean isConnected() {
		return this.getRouter() != null;
	}

	/**
	 * Get the IP address of the local host.
	 * 
	 * @return IP address of the local host or <code>null</code>, if the address
	 *         could not be determined.
	 * @throws RouterException
	 */
	public String getLocalHostAddress() {
		logger.debug("Get IP of localhost...");
		if (router != null) {
			try {
				return router.getLocalHostAddress();
			} catch (RouterException e) {
				logger.warn("Could not get address of localhost.", e);
				logger
						.warn("Could not get address of localhost. Please enter it manually.");
			}
		}
		return null;
	}

	public void setLogLevel(String logLevel) {
		Logger.getLogger("org.chris.portmapper").setLevel(
				Level.toLevel(logLevel));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(PortMapperApp.class, args);
	}

}
