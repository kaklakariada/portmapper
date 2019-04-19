/**
 * UPnP PortMapper - A tool for managing port forwardings via UPnP
 * Copyright (C) 2015 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.chris.portmapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import javax.swing.JOptionPane;

import org.chris.portmapper.gui.PortMapperView;
import org.chris.portmapper.logging.LogMessageListener;
import org.chris.portmapper.logging.LogMessageOutputStream;
import org.chris.portmapper.logging.LogbackConfiguration;
import org.chris.portmapper.model.PortMappingPreset;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.utils.AppHelper;
import org.jdesktop.application.utils.OSXAdapter;
import org.jdesktop.application.utils.PlatformType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main application class
 */
public class PortMapperApp extends SingleFrameApplication {

    /**
     * The name of the system property which will be used as the directory where all configuration files will be stored.
     */
    private static final String CONFIG_DIR_PROPERTY_NAME = "portmapper.config.dir";

    /**
     * The file name for the settings file.
     */
    private static final String SETTINGS_FILENAME = "settings.xml";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private IRouter router;
    private Settings settings;
    private final LogMessageOutputStream logMessageOutputStream = new LogMessageOutputStream();
    private final LogbackConfiguration logbackConfig = new LogbackConfiguration();

    @Override
    protected void startup() {
        logbackConfig.registerOutputStream(logMessageOutputStream);

        setCustomConfigDir();

        loadSettings();

        final PortMapperView view = new PortMapperView(this);
        addExitListener(new ExitListener() {
            @Override
            public boolean canExit(final EventObject arg0) {
                return true;
            }

            @Override
            public void willExit(final EventObject arg0) {
                disconnectRouter();
            }
        });

        show(view);

        if (AppHelper.getPlatform() == PlatformType.OS_X) {
            registerMacOSXListeners();
        }
    }

    private void registerMacOSXListeners() {
        final PortMapperView view = getView();
        OSXAdapter.setPreferencesHandler(view, getMethod(PortMapperView.class, "changeSettings"));
        OSXAdapter.setAboutHandler(view, getMethod(PortMapperView.class, "showAboutDialog"));
    }

    private static Method getMethod(final Class<?> clazz, final String name, final Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new IllegalStateException("Error getting method " + name + " of class " + clazz.getName(), e);
        }
    }

    /**
     * Read the system property with name {@link PortMapperApp#CONFIG_DIR_PROPERTY_NAME} and change the local storage
     * directory if the property is given and points to a writable directory. If there is a directory named
     * <code>PortMapperConf</code> in the current directory, use this as the configuration directory.
     */
    private void setCustomConfigDir() {
        final String customConfigurationDir = System.getProperty(CONFIG_DIR_PROPERTY_NAME);
        final File portableAppConfigDir = new File("PortMapperConf");

        // the property is set: check, if the given directory can be used
        if (customConfigurationDir != null) {
            final File dir = new File(customConfigurationDir);
            if (!dir.isDirectory()) {
                logger.error("Custom configuration directory '{}' is not a directory.", customConfigurationDir);
                System.exit(1);
            }
            if (!dir.canRead() || !dir.canWrite()) {
                logger.error("Can not read or write to custom configuration directory '{}'.", customConfigurationDir);
                System.exit(1);
            }
            logger.info("Using custom configuration directory '{}'.", dir.getAbsolutePath());
            getContext().getLocalStorage().setDirectory(dir);

            // check, if the portable app directory exists and use this one
        } else if (portableAppConfigDir.isDirectory() && portableAppConfigDir.canRead()
                && portableAppConfigDir.canWrite()) {
            logger.info("Found portable app configuration directory '{}'.", portableAppConfigDir.getAbsolutePath());
            getContext().getLocalStorage().setDirectory(portableAppConfigDir);

            // use the default configuration directory
        } else {
            logger.info("Using default configuration directory '{}'.",
                    getContext().getLocalStorage().getDirectory().getAbsolutePath());
        }
    }

    /**
     * Load the application settings from file {@link PortMapperApp#SETTINGS_FILENAME} located in the configuration
     * directory.
     */
    private void loadSettings() {
        logger.debug("Loading settings from file {}", SETTINGS_FILENAME);
        try {
            settings = (Settings) getContext().getLocalStorage().load(SETTINGS_FILENAME);
        } catch (final IOException | ArrayIndexOutOfBoundsException e) {
            logger.warn("Could not load settings from file " + SETTINGS_FILENAME, e);
        }

        if (settings == null) {
            logger.debug("Settings were not loaded from file {}: create new settings", SETTINGS_FILENAME);
            settings = new Settings();
        } else {
            logger.debug("Got settings {}", settings);
            this.setLogLevel(settings.getLogLevel());
        }
    }

    public void setLogMessageListener(final LogMessageListener listener) {
        this.logMessageOutputStream.registerListener(listener);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        logger.debug("Saving settings {} to file {}", settings, SETTINGS_FILENAME);
        if (logger.isTraceEnabled()) {
            for (final PortMappingPreset preset : settings.getPresets()) {
                logger.trace("Saving port mapping {}", preset.getCompleteDescription());
            }
        }
        try {
            getContext().getLocalStorage().save(settings, SETTINGS_FILENAME);
        } catch (final IOException e) {
            logger.warn("Could not save settings to file " + SETTINGS_FILENAME, e);
        }
    }

    public ResourceMap getResourceMap() {
        return getContext().getResourceMap();
    }

    public PortMapperView getView() {
        return (PortMapperView) getMainView();
    }

    public void connectRouter() throws RouterException {
        if (this.router != null) {
            logger.warn("Already connected to router. Cannot create a second connection.");
            return;
        }

        final AbstractRouterFactory routerFactory;
        try {
            routerFactory = createRouterFactory();
        } catch (final RouterException e) {
            logger.error("Could not create router factory: {}", e.getMessage(), e);
            return;
        }
        logger.info("Searching for routers...");

        final Collection<IRouter> foundRouters = routerFactory.findRouters();

        // No routers found
        if (foundRouters == null || foundRouters.isEmpty()) {
            throw new RouterException("Did not find a router");
        }

        // One router found: use it.
        if (foundRouters.size() == 1) {
            router = foundRouters.iterator().next();
            logger.info("Connected to router '{}'", router.getName());
            this.getView().fireConnectionStateChange();
            return;
        }

        // More than one router found: ask user.
        logger.info("Found more than one router (count: {}): ask user.", foundRouters.size());

        final ResourceMap resourceMap = getResourceMap();
        final IRouter selectedRouter = (IRouter) JOptionPane.showInputDialog(this.getView().getFrame(),
                resourceMap.getString("messages.select_router.message"),
                resourceMap.getString("messages.select_router.title"), JOptionPane.QUESTION_MESSAGE, null,
                foundRouters.toArray(), null);

        if (selectedRouter == null) {
            logger.info("No router selected.");
            return;
        }

        this.router = selectedRouter;
        this.getView().fireConnectionStateChange();
    }

    private AbstractRouterFactory createRouterFactory() throws RouterException {
        logger.info("Creating router factory for class {}", settings.getRouterFactoryClassName());
        final Class<AbstractRouterFactory> routerFactoryClass = getClassForName(settings.getRouterFactoryClassName());
        final Constructor<AbstractRouterFactory> constructor = getConstructor(routerFactoryClass);
        final AbstractRouterFactory routerFactory = createInstance(constructor);
        logger.debug("Router factory {} created", routerFactory);
        return routerFactory;
    }

    private AbstractRouterFactory createInstance(final Constructor<AbstractRouterFactory> constructor)
            throws RouterException {
        try {
            return constructor.newInstance(this);
        } catch (final Exception e) {
            throw new RouterException("Could not create a router factory using constructor " + constructor, e);
        }
    }

    private static Constructor<AbstractRouterFactory> getConstructor(final Class<AbstractRouterFactory> clazz)
            throws RouterException {
        try {
            return clazz.getConstructor(PortMapperApp.class);
        } catch (final NoSuchMethodException e) {
            throw new RouterException("Could not find constructor of " + clazz.getName(), e);
        } catch (final SecurityException e1) {
            throw new RouterException("Could not find constructor of " + clazz.getName(), e1);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<AbstractRouterFactory> getClassForName(final String className) throws RouterException {
        try {
            return (Class<AbstractRouterFactory>) Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new RouterException("Did not find router factory class for name " + className, e);
        }
    }

    public void disconnectRouter() {
        if (this.router == null) {
            logger.debug("Not connected to router. Can not disconnect.");
            return;
        }

        this.router.disconnect();
        this.router = null;
        this.getView().fireConnectionStateChange();
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
     * @return IP address of the local host or <code>null</code>, if the address could not be determined.
     * @throws RouterException
     */
    public String getLocalHostAddress() {

        try {
            if (router != null) {
                logger.debug("Connected to router, get IP of localhost from socket...");
                return router.getLocalHostAddress();
            }

            logger.debug("Not connected to router, get IP of localhost from network interface...");
            final InetAddress address = getLocalhostAddressFromNetworkInterface();
            if (address != null) {
                return address.getHostAddress();
            } else {
                logger.warn("Did not get IP of localhost from network interface");
            }

        } catch (final RouterException e) {
            logger.warn("Could not get address of localhost.", e);
            logger.warn("Could not get address of localhost. Please enter it manually.");
        }
        return null;
    }

    private InetAddress getLocalhostAddressFromNetworkInterface() throws RouterException {
        try {
            final List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            logger.trace("Found network interfaces {}", networkInterfaces);
            for (final NetworkInterface nInterface : networkInterfaces) {
                if (nInterface.isLoopback()) {
                    logger.debug("Found loopback network interface {}/{} with IPs {}: ignore.",
                            nInterface.getDisplayName(), nInterface.getName(), nInterface.getInterfaceAddresses());
                } else if (!nInterface.isUp()) {
                    logger.debug("Found inactive network interface {}/{} with IPs {}: ignore.",
                            nInterface.getDisplayName(), nInterface.getName(), nInterface.getInterfaceAddresses());
                } else {
                    logger.debug("Found network interface {}/{} with IPs {}: use this one.",
                            nInterface.getDisplayName(), nInterface.getName(), nInterface.getInterfaceAddresses());
                    final List<InetAddress> addresses = Collections.list(nInterface.getInetAddresses());
                    if (!addresses.isEmpty()) {
                        final InetAddress address = findIPv4Adress(nInterface, addresses);
                        logger.debug("Found one address for network interface {}: using {}", nInterface.getName(),
                                address);
                        return address;
                    }
                    logger.debug("Network interface {} has no addresses.", nInterface.getName());
                }
            }
        } catch (final SocketException e) {
            throw new RouterException("Did not get network interfaces.", e);
        }
        return null;
    }

    private InetAddress findIPv4Adress(final NetworkInterface nInterface, final List<InetAddress> addresses) {
        if (addresses.size() == 1) {
            return addresses.get(0);
        }

        for (final InetAddress inetAddress : addresses) {
            if (inetAddress.getHostAddress().contains(".")) {
                logger.debug("Found IPv4 address {}", inetAddress);
                return inetAddress;
            }
        }
        final InetAddress address = addresses.get(0);
        logger.info("Found more than one address for network interface {}: using {}", nInterface.getName(), address);
        return address;
    }

    public void setLogLevel(final String logLevel) {
        this.logbackConfig.setLogLevel(logLevel);
    }
}
