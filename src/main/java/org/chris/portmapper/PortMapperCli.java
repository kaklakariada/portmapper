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

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;
import org.chris.portmapper.router.cling.ClingRouterFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.utils.AppHelper;
import org.jdesktop.application.utils.PlatformType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortMapperCli {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommandLineArguments cmdLineArgs;
    private String routerFactoryClassName = ClingRouterFactory.class.getName();
    private Integer routerIndex = null;

    public PortMapperCli() {
        cmdLineArgs = new CommandLineArguments();
    }

    public void start(final String[] args) {
        if (!cmdLineArgs.parse(args)) {
            System.exit(1);
        }
        if (isStartGuiRequired()) {
            startGui(args);
            return;
        }

        if (cmdLineArgs.getUpnpLib() != null) {
            this.routerFactoryClassName = cmdLineArgs.getUpnpLib();
            logger.info("Using router factory class {}", this.routerFactoryClassName);
        }

        if (cmdLineArgs.getRouterIndex() != null) {
            this.routerIndex = cmdLineArgs.getRouterIndex();
            logger.info("Using router index {}", this.routerIndex);
        }

        if (cmdLineArgs.isPrintHelp()) {
            printHelp();
            return;
        }
        try (final IRouter router = connect()) {
            if (router == null) {
                logger.error("No router found: exit");
                System.exit(1);
                return;
            }
            if (cmdLineArgs.isAddPortMapping()) {
                addPortForwarding(router);
            } else if (cmdLineArgs.isPrintInfo()) {
                printStatus(router);
            } else if (cmdLineArgs.isDeletePortMapping()) {
                deletePortForwardings(router);
            } else if (cmdLineArgs.isListPortMappings()) {
                printPortForwardings(router);
            } else {
                router.disconnect();
                System.err.println("Incorrect usage");
                printHelp();
                System.exit(1);
                return;
            }
            router.disconnect();
        } catch (final RouterException e) {
            logger.error("An error occured", e);
            System.exit(1);
            return;
        }
        System.exit(0);
    }

    private void startGui(final String[] args) {
        if (AppHelper.getPlatform() == PlatformType.OS_X) {
            MacSetup.setupMac();
        }
        Application.launch(PortMapperApp.class, args);
    }

    private void printPortForwardings(final IRouter router) throws RouterException {
        final Collection<PortMapping> mappings = router.getPortMappings();
        if (mappings.isEmpty()) {
            logger.info("No port mappings found");
            return;
        }
        final StringBuilder b = new StringBuilder();
        for (final Iterator<PortMapping> iterator = mappings.iterator(); iterator.hasNext();) {
            final PortMapping mapping = iterator.next();
            b.append(mapping.getCompleteDescription());
            if (iterator.hasNext()) {
                b.append("\n");
            }
        }
        logger.info("Found {} port forwardings:\n{}", mappings.size(), b);
    }

    private void deletePortForwardings(final IRouter router) throws RouterException {

        final String remoteHost = null;
        final int port = cmdLineArgs.getExternalPort();
        final Protocol protocol = cmdLineArgs.getProtocol();
        logger.info("Deleting mapping for protocol {} and external port {}", protocol, port);
        router.removePortMapping(protocol, remoteHost, port);
        printPortForwardings(router);
    }

    private void printStatus(final IRouter router) throws RouterException {
        router.logRouterInfo();
    }

    private void addPortForwarding(final IRouter router) throws RouterException {

        final String remoteHost = null;
        final String internalClient = cmdLineArgs.getInternalIp() != null ? cmdLineArgs.getInternalIp()
                : router.getLocalHostAddress();
        final int internalPort = cmdLineArgs.getInternalPort();
        final int externalPort = cmdLineArgs.getExternalPort();
        final Protocol protocol = cmdLineArgs.getProtocol();

        final String description = cmdLineArgs.getDescription() != null ? cmdLineArgs.getDescription()
                : "PortMapper " + protocol + "/" + internalClient + ":" + internalPort;
        final PortMapping mapping = new PortMapping(protocol, remoteHost, externalPort, internalClient, internalPort,
                description);
        logger.info("Adding mapping {}", mapping);
        router.addPortMapping(mapping);
        printPortForwardings(router);
    }

    private void printHelp() {
        cmdLineArgs.printHelp();
    }

    private boolean isStartGuiRequired() {
        if (cmdLineArgs.isStartGui()) {
            return true;
        }
        return !cliCommand();
    }

    private boolean cliCommand() {
        return cmdLineArgs.isPrintHelp() || cmdLineArgs.isAddPortMapping() || cmdLineArgs.isPrintInfo()
                || cmdLineArgs.isListPortMappings() || cmdLineArgs.isDeletePortMapping();
    }

    private AbstractRouterFactory createRouterFactory() throws RouterException {
        logger.info("Creating router factory for class {}", routerFactoryClassName);
        final Class<AbstractRouterFactory> routerFactoryClass = getClassForName(routerFactoryClassName);
        logger.debug("Creating a new instance of the router factory class {}", routerFactoryClass.getName());
        try {
            final Constructor<AbstractRouterFactory> constructor = routerFactoryClass
                    .getConstructor(PortMapperApp.class);
            return constructor.newInstance(new PortMapperApp());
        } catch (final Exception e) {
            throw new RouterException("Error creating a router factory using class " + routerFactoryClass.getName(), e);
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

    private IRouter connect() throws RouterException {
        final AbstractRouterFactory routerFactory;
        try {
            routerFactory = createRouterFactory();
        } catch (final RouterException e) {
            logger.error("Could not create router factory", e);
            return null;
        }
        logger.info("Searching for routers...");

        final List<IRouter> foundRouters = routerFactory.findRouters();

        return selectRouter(foundRouters);
    }

    private IRouter selectRouter(final List<IRouter> foundRouters) {
        // One router found: use it.
        if (foundRouters.size() == 1) {
            final IRouter router = foundRouters.iterator().next();
            logger.info("Connected to router {}", router.getName());
            return router;
        } else if (foundRouters.isEmpty()) {
            logger.error("Found no router");
            return null;
        } else if (foundRouters.size() > 1 && routerIndex == null) {
            // let user choose which router to use.
            logger.error("Found more than one router. Use option -i <index>");

            int index = 0;
            for (final IRouter iRouter : foundRouters) {
                logger.error("- index {}: {}", index, iRouter.getName());
                index++;
            }
            return null;
        } else if (routerIndex != null && routerIndex >= 0 && routerIndex < foundRouters.size()) {
            final IRouter router = foundRouters.get(routerIndex);
            logger.info("Found more than one router, using {}", router.getName());
            return router;
        } else {
            logger.error("Index {} must be between 0 and {}", routerIndex, (foundRouters.size() - 1));
            return null;
        }
    }
}
