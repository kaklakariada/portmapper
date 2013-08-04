package org.chris.portmapper;

import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;
import org.chris.portmapper.router.dummy.DummyRouterFactory;
import org.chris.portmapper.router.sbbi.SBBIRouterFactory;
import org.chris.portmapper.router.weupnp.WeUPnPRouterFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.utils.AppHelper;
import org.jdesktop.application.utils.PlatformType;

/**
 * @author chris
 * 
 */
public class PortMapperCli {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final String ADD_OPTION = "a";
	private static final String HELP_OPTION = "h";
	private static final String START_GUI_OPTION = "g";
	private static final String STATUS_OPTION = "s";
	private static final String DELETE_OPTION = "d";
	private static final String LIST_OPTION = "l";
	private static final String ADD_LOCALHOST_OPTION = "r";
	private static final String UPNP_LIB_OPTION = "u";
	private static final String ROUTER_INDEX_OPTION = "i";

	private final Options options;
	private final CommandLineParser parser;
	private String routerFactoryClassName = SBBIRouterFactory.class.getName();
	private Integer routerIndex = null;

	public PortMapperCli() {
		options = createOptions();
		parser = new PosixParser();
	}

	/**
	 * @return
	 */
	private Options createOptions() {

		final boolean useLongOpts = false;

		final Option help = new Option(HELP_OPTION,
				useLongOpts ? "help" : null, false, "print this message");
		final Option startGui = new Option(START_GUI_OPTION,
				useLongOpts ? "gui" : null, false,
				"Start graphical user interface (default)");
		final Option add = new Option(ADD_OPTION, useLongOpts ? "add" : null,
				true, "Add port forwarding");
		add.setArgs(4);
		add.setArgName("ip port external_port protocol");
		add.setValueSeparator(' ');
		add.setType(String.class);

		final Option delete = new Option(DELETE_OPTION, useLongOpts ? "delete"
				: null, true, "Delete port forwarding");
		delete.setArgs(20);
		delete.setArgName("external_port protocol [...]");
		delete.setValueSeparator(' ');
		delete.setType(String.class);

		final Option status = new Option(STATUS_OPTION, useLongOpts ? "status"
				: null, false, "Get Connection status");

		final Option list = new Option(LIST_OPTION,
				useLongOpts ? "list" : null, false, "List forwardings");

		final Option addLocalhost = new Option(ADD_LOCALHOST_OPTION,
				useLongOpts ? "addlocalhost" : null, true,
				"Add all forwardings to the current host");
		addLocalhost.setArgs(20);
		addLocalhost.setArgName("port protocol [...]");
		addLocalhost.setValueSeparator(' ');
		addLocalhost.setType(String.class);

		final Option upnpLib = new Option(UPNP_LIB_OPTION,
				useLongOpts ? "delete" : null, true, "UPnP library");
		upnpLib.setArgs(1);
		upnpLib.setArgName("class name");
		upnpLib.setType(String.class);

		final Option routerIndexOption = new Option(ROUTER_INDEX_OPTION,
				useLongOpts ? "index" : null, true,
				"Router index (if more than one is found)");
		routerIndexOption.setArgs(1);
		routerIndexOption.setArgName("index");
		routerIndexOption.setType(Integer.class);

		final OptionGroup optionGroup = new OptionGroup();
		optionGroup.setRequired(false);
		optionGroup.addOption(help);
		optionGroup.addOption(startGui);
		optionGroup.addOption(add);
		optionGroup.addOption(addLocalhost);
		optionGroup.addOption(delete);
		optionGroup.addOption(list);
		optionGroup.addOption(status);

		final Options allOptions = new Options();
		allOptions.addOption(upnpLib);
		allOptions.addOption(routerIndexOption);
		allOptions.addOptionGroup(optionGroup);

		return allOptions;
	}

	/**
	 * @param args
	 */
	public void start(final String[] args) {
		final CommandLine commandLine = parseCommandLine(args);
		if (isStartGuiRequired(commandLine)) {
			startGui(args);
			return;
		}

		initDummyLogAppender();

		if (commandLine.hasOption(UPNP_LIB_OPTION)) {
			this.routerFactoryClassName = commandLine
					.getOptionValue(UPNP_LIB_OPTION);
			logger.info("Using router factory class '"
					+ this.routerFactoryClassName + "'");
		}

		if (commandLine.hasOption(ROUTER_INDEX_OPTION)) {
			try {
				this.routerIndex = Integer.parseInt(commandLine
						.getOptionValue(ROUTER_INDEX_OPTION));
			} catch (final NumberFormatException e) {
				printHelp();
				System.exit(1);
			}
			logger.info("Using router index " + this.routerIndex);
		}

		if (commandLine.hasOption(HELP_OPTION)) {
			printHelp();
			return;
		}
		try {
			final IRouter router = connect();
			if (router == null) {
				logger.error("No router found: exit");
				System.exit(1);
				return;
			}
			if (commandLine.hasOption(ADD_OPTION)) {
				addPortForwarding(router,
						commandLine.getOptionValues(ADD_OPTION));
			} else if (commandLine.hasOption(STATUS_OPTION)) {
				printStatus(router);
			} else if (commandLine.hasOption(DELETE_OPTION)) {
				deletePortForwardings(router,
						commandLine.getOptionValues(DELETE_OPTION));
			} else if (commandLine.hasOption(LIST_OPTION)) {
				printPortForwardings(router);
			} else if (commandLine.hasOption(ADD_LOCALHOST_OPTION)) {
				addLocalhostPortForwardings(router,
						commandLine.getOptionValues(ADD_LOCALHOST_OPTION));
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

	/**
	 * @param args
	 */
	private void startGui(final String[] args) {
		if (AppHelper.getPlatform() == PlatformType.OS_X) {
			MacSetup.setupMac();
		}
		Application.launch(PortMapperApp.class, args);
	}

	/**
	 * @param optionValues
	 * @throws RouterException
	 */
	private void addLocalhostPortForwardings(final IRouter router,
			final String[] optionValues) throws RouterException {

		if (optionValues.length == 0 || optionValues.length % 2 != 0) {
			logger.error("Invalid number of arguments for option "
					+ ADD_LOCALHOST_OPTION);
			return;
		}

		final String internalClient = router.getLocalHostAddress();
		for (int i = 0; i < optionValues.length; i += 2) {
			final int port = Integer.parseInt(optionValues[i]);
			final Protocol protocol = Protocol.valueOf(optionValues[i + 1]);
			final String description = "PortMapper forwarding for " + protocol
					+ "/" + internalClient + ":" + port;
			final PortMapping mapping = new PortMapping(protocol, null, port,
					internalClient, port, description);
			logger.info("Adding mapping " + mapping.getCompleteDescription());
			router.addPortMapping(mapping);
		}
		printPortForwardings(router);
	}

	/**
	 * @throws RouterException
	 * 
	 */
	private void printPortForwardings(final IRouter router)
			throws RouterException {

		final Collection<PortMapping> mappings = router.getPortMappings();
		if (mappings.size() == 0) {
			logger.info("No port mappings found");
			return;
		}
		final StringBuilder b = new StringBuilder();
		for (final Iterator<PortMapping> iterator = mappings.iterator(); iterator
				.hasNext();) {
			final PortMapping mapping = iterator.next();
			b.append(mapping.getCompleteDescription());
			if (iterator.hasNext()) {
				b.append("\n");
			}

		}
		logger.info("Found " + mappings.size() + " port forwardings:\n"
				+ b.toString());
	}

	/**
	 * @param optionValues
	 * @throws RouterException
	 */
	private void deletePortForwardings(final IRouter router,
			final String[] optionValues) throws RouterException {

		if (optionValues.length == 0 || optionValues.length % 2 != 0) {
			logger.error("Invalid number of arguments for option "
					+ DELETE_OPTION);
			return;
		}

		final String remoteHost = null;
		for (int i = 0; i < optionValues.length; i += 2) {
			final int port = Integer.parseInt(optionValues[i]);
			final Protocol protocol = Protocol.valueOf(optionValues[i + 1]);
			logger.info("Deleting mapping for protocol " + protocol
					+ " and external port " + port);
			router.removePortMapping(protocol, remoteHost, port);
		}
		printPortForwardings(router);
	}

	/**
	 * @throws RouterException
	 * 
	 */
	private void printStatus(final IRouter router) throws RouterException {
		router.logRouterInfo();
	}

	/**
	 * @param optionValues
	 * @throws RouterException
	 */
	private void addPortForwarding(final IRouter router,
			final String[] optionValues) throws RouterException {

		final String remoteHost = null;
		final String internalClient = optionValues[0];
		final int internalPort = Integer.parseInt(optionValues[1]);
		final int externalPort = Integer.parseInt(optionValues[2]);
		final Protocol protocol = Protocol.valueOf(optionValues[3]);

		final String description = "PortMapper " + protocol + "/"
				+ internalClient + ":" + internalPort;
		final PortMapping mapping = new PortMapping(protocol, remoteHost,
				externalPort, internalClient, internalPort, description);
		logger.info("Adding mapping " + mapping);
		router.addPortMapping(mapping);
		printPortForwardings(router);
	}

	private void printHelp() {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(80);
		// formatter.setDescPadding(0);
		// formatter.setLeftPadding(0);
		final String header = "";
		final String cmdLineSyntax = "java -jar PortMapper.jar";
		final StringBuilder footer = new StringBuilder();
		footer.append("Protocol is UDP or TCP\n");
		footer.append("UPnP library class names:\n");
		footer.append("- ");
		footer.append(SBBIRouterFactory.class.getName());
		footer.append(" (default)\n");
		footer.append("- ");
		footer.append(WeUPnPRouterFactory.class.getName());
		footer.append("\n- ");
		footer.append(DummyRouterFactory.class.getName());

		formatter.printHelp(formatter.getWidth(), cmdLineSyntax, header,
				options, footer.toString(), true);
	}

	private boolean isStartGuiRequired(final CommandLine commandLine) {
		if (commandLine.hasOption(START_GUI_OPTION)) {
			return true;
		}
		return !(commandLine.hasOption(HELP_OPTION)
				|| commandLine.hasOption(ADD_LOCALHOST_OPTION)
				|| commandLine.hasOption(ADD_OPTION)
				|| commandLine.hasOption(STATUS_OPTION)
				|| commandLine.hasOption(LIST_OPTION) || commandLine
					.hasOption(DELETE_OPTION));
	}

	@SuppressWarnings("resource")
	private void initDummyLogAppender() {
		final WriterAppender writerAppender = (WriterAppender) Logger
				.getLogger("org.chris.portmapper").getAppender("jtextarea");
		writerAppender.setWriter(new DummyWriter());
	}

	private CommandLine parseCommandLine(final String[] args) {
		try {
			return parser.parse(options, args);
		} catch (final ParseException e) {
			initDummyLogAppender();
			logger.error("Could not parse command line: " + e.getMessage());
			System.exit(1);
			return null;
		}
	}

	private static class DummyWriter extends Writer {
		@Override
		public void close() {
			// ignore
		}

		@Override
		public void flush() {
			// ignore
		}

		@Override
		public void write(final char[] cbuf, final int off, final int len) {
			// ignore
		}
	}

	@SuppressWarnings("unchecked")
	private AbstractRouterFactory createRouterFactory() throws RouterException {
		Class<AbstractRouterFactory> routerFactoryClass;
		logger.info("Creating router factory for class "
				+ routerFactoryClassName);
		try {
			routerFactoryClass = (Class<AbstractRouterFactory>) Class
					.forName(routerFactoryClassName);
		} catch (final ClassNotFoundException e1) {
			throw new RouterException(
					"Did not find router factory class for name "
							+ routerFactoryClassName, e1);
		}

		AbstractRouterFactory routerFactory;
		logger.debug("Creating a new instance of the router factory class "
				+ routerFactoryClass);
		try {
			routerFactory = routerFactoryClass.newInstance();
		} catch (final Exception e) {
			throw new RouterException(
					"Could not create a router factory for name "
							+ routerFactoryClassName, e);
		}
		logger.debug("Router factory created");
		return routerFactory;
	}

	private IRouter connect() throws RouterException {
		AbstractRouterFactory routerFactory;
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

	/**
	 * @param foundRouters
	 * @return
	 */
	private IRouter selectRouter(final List<IRouter> foundRouters) {
		// One router found: use it.
		if (foundRouters.size() == 1) {
			final IRouter router = foundRouters.iterator().next();
			logger.info("Connected to router " + router.getName());
			return router;
		} else if (foundRouters.size() == 0) {
			logger.error("Found no router");
			return null;
		} else if (foundRouters.size() > 1 && routerIndex == null) {
			// let user choose which router to use.
			logger.error("Found more than one router. Use option -i <index>");

			int index = 0;
			for (final IRouter iRouter : foundRouters) {
				logger.error("- index " + index + ": " + iRouter.getName());
				index++;
			}
			return null;
		} else if (routerIndex >= 0 && routerIndex < foundRouters.size()) {
			final IRouter router = foundRouters.get(routerIndex);
			logger.info("Found more than one router, using " + router.getName());
			return router;
		} else {
			logger.error("Index must be between 0 and "
					+ (foundRouters.size() - 1));
			return null;
		}
	}
}
