/**
 * 
 */
package org.chris.portmapper;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
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
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;
import org.chris.portmapper.router.dummy.DummyRouterFactory;
import org.chris.portmapper.router.sbbi.SBBIRouterFactory;
import org.chris.portmapper.router.weupnp.WeUPnPRouterFactory;
import org.jdesktop.application.Application;

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

		boolean useLongOpts = false;

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
		delete.setArgs(2);
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
		addLocalhost.setArgs(2);
		addLocalhost.setArgName("port protocol [...]");
		addLocalhost.setValueSeparator(' ');
		addLocalhost.setType(String.class);

		final Option upnpLib = new Option(UPNP_LIB_OPTION,
				useLongOpts ? "delete" : null, true, "UPnP library");
		upnpLib.setArgs(1);
		upnpLib.setArgName("class name");
		upnpLib.setType(String.class);

		final Option routerIndex = new Option(ROUTER_INDEX_OPTION,
				useLongOpts ? "index" : null, true,
				"Router index (if more than one is found)");
		routerIndex.setArgs(1);
		routerIndex.setArgName("index");
		routerIndex.setType(Integer.class);

		final OptionGroup optionGroup = new OptionGroup();
		optionGroup.setRequired(false);
		optionGroup.addOption(help);
		optionGroup.addOption(startGui);
		optionGroup.addOption(add);
		optionGroup.addOption(addLocalhost);
		optionGroup.addOption(delete);
		optionGroup.addOption(list);
		optionGroup.addOption(status);

		// create Options object
		final Options options = new Options();
		options.addOption(upnpLib);
		options.addOption(routerIndex);
		options.addOptionGroup(optionGroup);

		return options;
	}

	/**
	 * @param args
	 */
	public void start(String[] args) {
		final CommandLine commandLine = parseCommandLine(args);
		if (isStartGuiRequired(commandLine)) {
			Application.launch(PortMapperApp.class, args);
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
			} catch (NumberFormatException e) {
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
				return;
			}
			if (commandLine.hasOption(ADD_OPTION)) {
				addPortForwarding(router, commandLine
						.getOptionValues(ADD_OPTION));
			} else if (commandLine.hasOption(STATUS_OPTION)) {
				printStatus(router);
			} else if (commandLine.hasOption(DELETE_OPTION)) {
				deletePortForwardings(router, commandLine
						.getOptionValues(DELETE_OPTION));
			} else if (commandLine.hasOption(LIST_OPTION)) {
				printPortForwardings(router);
			} else if (commandLine.hasOption(ADD_LOCALHOST_OPTION)) {
				addLocalhostPortForwardings(router, commandLine
						.getOptionValues(ADD_OPTION));
			} else {
				router.disconnect();
				System.err.println("Incorrect usage");
				printHelp();
				System.exit(1);
			}
			router.disconnect();
		} catch (RouterException e) {
			logger.error("An error occured", e);
		}
		System.exit(0);
	}

	/**
	 * @param optionValues
	 * @throws RouterException
	 */
	private void addLocalhostPortForwardings(IRouter router,
			String[] optionValues) throws RouterException {
		// TODO Auto-generated method stub
		System.out.println("add port forwardings "
				+ Arrays.toString(optionValues));

		final Protocol protocol = null;
		final String remoteHost = null;
		final int externalPort = 0;
		final String internalClient = router.getLocalHostAddress();
		final int internalPort = 0;
		final String description = null;
		final PortMapping mapping = new PortMapping(protocol, remoteHost,
				externalPort, internalClient, internalPort, description);
		router.addPortMapping(mapping);
	}

	/**
	 * @throws RouterException
	 * 
	 */
	private void printPortForwardings(IRouter router) throws RouterException {

		Collection<PortMapping> mappings = router.getPortMappings();
		if (mappings.size() == 0) {
			logger.info("No port mappings found");
			return;
		}
		StringBuilder b = new StringBuilder();
		for (PortMapping mapping : mappings) {
			b.append(mapping.getCompleteDescription());
			b.append("\n");
		}
		System.out.println(b);
	}

	/**
	 * @param optionValues
	 * @throws RouterException
	 */
	private void deletePortForwardings(IRouter router, String[] optionValues)
			throws RouterException {
		// TODO Auto-generated method stub
		System.out.println("delete port forwardings "
				+ Arrays.toString(optionValues));

		final Protocol protocol = null;
		final String remoteHost = null;
		final int externalPort = 0;
		router.removePortMapping(protocol, remoteHost, externalPort);
	}

	/**
	 * @throws RouterException
	 * 
	 */
	private void printStatus(IRouter router) throws RouterException {
		router.logRouterInfo();
	}

	/**
	 * @param optionValues
	 * @throws RouterException
	 */
	private void addPortForwarding(IRouter router, String[] optionValues)
			throws RouterException {
		// TODO Auto-generated method stub
		System.out.println("add port forwarding "
				+ Arrays.toString(optionValues));
		final Protocol protocol = null;
		final String remoteHost = null;
		final int externalPort = 0;
		final String internalClient = null;
		final int internalPort = 0;
		final String description = null;
		final PortMapping mapping = new PortMapping(protocol, remoteHost,
				externalPort, internalClient, internalPort, description);
		router.addPortMapping(mapping);
	}

	private void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(80);
		// formatter.setDescPadding(0);
		// formatter.setLeftPadding(0);
		String header = "";
		String cmdLineSyntax = "java -jar PortMapper.jar";
		StringBuilder footer = new StringBuilder();
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

	private boolean isStartGuiRequired(CommandLine commandLine) {
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

	private void initDummyLogAppender() {
		WriterAppender writerAppender = (WriterAppender) Logger.getLogger(
				"org.chris.portmapper").getAppender("jtextarea");
		writerAppender.setWriter(new DummyWriter());
	}

	private CommandLine parseCommandLine(String[] args) {
		try {
			return parser.parse(options, args);
		} catch (ParseException e) {
			initDummyLogAppender();
			logger.error("Could not parse command line: " + e.getMessage());
			System.exit(1);
			return null;
		}
	}

	private static class DummyWriter extends Writer {
		@Override
		public void close() throws IOException {
		}

		@Override
		public void flush() throws IOException {
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
		}
	}

	@SuppressWarnings("unchecked")
	private IRouterFactory createRouterFactory() throws RouterException {
		Class<IRouterFactory> routerFactoryClass;
		logger.info("Creating router factory for class "
				+ routerFactoryClassName);
		try {
			routerFactoryClass = (Class<IRouterFactory>) Class
					.forName(routerFactoryClassName);
		} catch (ClassNotFoundException e1) {
			throw new RouterException(
					"Did not find router factory class for name "
							+ routerFactoryClassName, e1);
		}

		IRouterFactory routerFactory;
		logger.debug("Creating a new instance of the router factory class "
				+ routerFactoryClass);
		try {
			routerFactory = routerFactoryClass.newInstance();
		} catch (Exception e) {
			throw new RouterException(
					"Could not create a router factory for name "
							+ routerFactoryClassName, e);
		}
		logger.debug("Router factory created");
		return routerFactory;
	}

	private IRouter connect() throws RouterException {
		IRouterFactory routerFactory;
		try {
			routerFactory = createRouterFactory();
		} catch (RouterException e) {
			logger.error("Could not create router factory", e);
			return null;
		}
		logger.info("Searching for routers...");

		List<IRouter> foundRouters = routerFactory.findRouters();

		// One router found: use it.
		if (foundRouters.size() == 1) {
			final IRouter router = foundRouters.iterator().next();
			logger.info("Connected to router " + router.getName());
			return router;
		} else if (foundRouters.size() == 1) {
			logger.error("Found no router");
			return null;
		} else if (foundRouters.size() > 1 && routerIndex == null) {
			// let user choose which router to use.
			logger.error("Found more than one router. Use option -i <index>");

			int index = 0;
			for (IRouter iRouter : foundRouters) {
				logger.error("- index " + index + ": " + iRouter.getName());
				index++;
			}
			return null;
		} else if (routerIndex >= 0 && routerIndex < foundRouters.size()) {
			final IRouter router = foundRouters.get(routerIndex);
			logger
					.info("Found more than one router, using "
							+ router.getName());
			return router;
		} else {
			logger.error("Index must be between 0 and "
					+ (foundRouters.size() - 1));
			return null;
		}
	}

	private void disconnect(IRouter router) {
		router.disconnect();
	}
}
