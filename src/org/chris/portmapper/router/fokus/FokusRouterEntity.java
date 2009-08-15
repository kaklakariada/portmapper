package org.chris.portmapper.router.fokus;

/**
 * @author chris
 * @version $Id$
 */
public class FokusRouterEntity {
	// public class FokusRouterEntity extends TemplateEntity {

	// /**
	// * The maximum number of port mappings that we will try to retrieve from
	// the
	// * router.
	// */
	// private final static int MAX_NUM_PORTMAPPINGS = 100;
	// private static final String SHORTENED_SERVICE_ID = "WANPPPConn1";
	// private final static Log logger = LogFactory
	// .getLog(FokusRouterEntity.class);
	//
	// /**
	// * @param startupConfiguration
	// */
	// public FokusRouterEntity(StartupConfiguration startupConfiguration) {
	// super(startupConfiguration);
	// }
	//
	// public RouterCPDevice getRouterDevice() {
	// return getRouterControlPoint().getRouterDevice();
	// }
	//
	// public boolean isConnected() {
	// return getRouterControlPoint().isConnected();
	// }
	//
	// private RouterControlPoint getRouterControlPoint() {
	// return (RouterControlPoint) getTemplateControlPoint();
	// }
	//
	// public void createRouterControlPoint() {
	// setTemplateControlPoint(new RouterControlPoint(this,
	// getStartupConfiguration()));
	// }
	//
	// private CPAction invokeAction(String actionName) throws RouterException {
	// CPAction action = getAction(actionName);
	// invokeAction(action);
	// return action;
	// }
	//
	// private void invokeAction(CPAction action) throws RouterException {
	// try {
	// getRouterControlPoint().invokeAction(action);
	// } catch (InvokeActionException e) {
	// throw new RouterException("Got exception when invocing action '"
	// + action.getName() + "' for device with udn '"
	// + getRouterDevice().getUDN() + "' and service id '"
	// + SHORTENED_SERVICE_ID + "'.", e);
	// } catch (ActionFailedException e) {
	// throw new RouterException("Got exception when invocing action '"
	// + action.getName() + "' for device with udn '"
	// + getRouterDevice().getUDN() + "' and service id '"
	// + SHORTENED_SERVICE_ID + "'.", e);
	// }
	// }
	//
	// private CPAction getAction(String actionName) throws RouterException {
	// CPAction action = getRouterControlPoint().getCPActionByName(
	// getRouterDevice().getUDN(), SHORTENED_SERVICE_ID, actionName);
	// if (action == null) {
	// throw new RouterException("Action '" + actionName
	// + "' not found for device with udn '"
	// + getRouterDevice().getUDN() + "' and service id '"
	// + SHORTENED_SERVICE_ID + "'.");
	// }
	// return action;
	// }
	//
	// private CPStateVariable getStateVariable(String stateVarName) {
	// String udn = getRouterDevice().getUDN();
	// CPStateVariable stateVar = getRouterControlPoint()
	// .getCPStateVariableByName(udn, SHORTENED_SERVICE_ID,
	// stateVarName);
	// return stateVar;
	// }
	//
	// public void addPortMapping(PortMapping mapping) throws RouterException {
	// CPAction action = getAction("AddPortMapping");
	// logger.debug("Add port mapping " + mapping);
	// String remoteHost = mapping.getRemoteHost();
	// // if (remoteHost != null && remoteHost.isEmpty()) {
	// // remoteHost = null;
	// // }
	// if (remoteHost == null) {
	// remoteHost = "";
	// }
	// try {
	//
	// action.getInArgument("NewRemoteHost")
	// .setValueFromString(remoteHost);
	// action.getInArgument("NewExternalPort").setNumericValue(
	// mapping.getExternalPort());
	// action.getInArgument("NewProtocol").setValueFromString(
	// mapping.getProtocol().getValue());
	// action.getInArgument("NewInternalPort").setNumericValue(
	// mapping.getInternalPort());
	// action.getInArgument("NewInternalClient").setValueFromString(
	// mapping.getInternalClient());
	// action.getInArgument("NewEnabled").setNumericValue(1);
	// // action.getInArgument("NewEnabled").setBooleanValue(true);
	// action.getInArgument("NewPortMappingDescription")
	// .setValueFromString(mapping.getDescription());
	// action.getInArgument("NewLeaseDuration").setNumericValue(0);
	// } catch (Exception e) {
	// throw new RouterException(
	// "Got exception when setting arguments of action AddPortMapping.",
	// e);
	// }
	// invokeAction(action);
	// }
	//
	// public void addPortMappings(Collection<PortMapping> mappings)
	// throws RouterException {
	// for (PortMapping portMapping : mappings) {
	// logger.info("Adding port mapping " + portMapping);
	// addPortMapping(portMapping);
	// }
	// }
	//
	// public void disconnect() {
	// getRouterControlPoint().terminate();
	// }
	//
	// public String getExternalIPAddress() throws RouterException {
	// CPAction action = invokeAction("GetExternalIPAddress");
	// Argument externalIPArgument = action
	// .getOutArgument("NewExternalIPAddress");
	// String externalIP;
	// try {
	// externalIP = externalIPArgument.getStringValue();
	// } catch (Exception e) {
	// throw new RouterException(
	// "Got exception when getting external IP adress.", e);
	// }
	// return externalIP;
	// }
	//
	// public String getInternalHostName() {
	// return getRouterDevice().getCPDevice().getDeviceAddress()
	// .getHostAddress();
	// }
	//
	// public int getInternalPort() throws RouterException {
	// try {
	// return (int) getStateVariable("InternalPort").getNumericValue();
	// } catch (Exception e) {
	// throw new RouterException(
	// "Got exception when getting internal port.", e);
	// }
	// }
	//
	// public String getName() throws RouterException {
	// return getRouterDevice().getCPDevice().getModelName();
	// }
	//
	// public Collection<PortMapping> getPortMappings() throws RouterException {
	// logger.info("Get all port mappings...");
	// Collection<PortMapping> mappings = new LinkedList<PortMapping>();
	//
	// /*
	// * This is a little trick to get all port mappings. There is a method
	// * that gets the number of available port mappings, but it seems, that
	// * this method just tries to get all port mappings and checks, if an
	// * error is returned.
	// *
	// * In order to speed this up, we will do the same here, but stop, when
	// * the first exception is thrown.
	// */
	// boolean moreEntries = true;
	// int currentMappingNumber = 0;
	// while (moreEntries && currentMappingNumber < MAX_NUM_PORTMAPPINGS) {
	//
	// // Create a port mapping for the response.
	// PortMapping mapping = getPortMapping(currentMappingNumber);
	// if (mapping == null) {
	// break;
	// }
	// mappings.add(mapping);
	//
	// currentMappingNumber++;
	// }
	//
	// // Check, if the max number of entries is reached and print a
	// // warning message.
	// if (currentMappingNumber == MAX_NUM_PORTMAPPINGS) {
	// logger
	// .warn("Reached max number of port mappings to get ("
	// + MAX_NUM_PORTMAPPINGS
	// +
	// "). Perhaps not all port mappings where retrieved. Try to increase Router.MAX_NUM_PORTMAPPINGS.");
	// }
	//
	// return mappings;
	// }
	//
	// private PortMapping getPortMapping(int index) throws RouterException {
	// logger.debug("Getting port mapping with entry number " + index + "...");
	//
	// CPAction action = getAction("GetGenericPortMappingEntry");
	// try {
	// action.getInArgument("NewPortMappingIndex").setNumericValue(index);
	// } catch (Exception e) {
	// throw new RouterException(
	// "Got exception when setting arguments of action GetGenericPortMappingEntry.",
	// e);
	// }
	//
	// try {
	// getRouterControlPoint().invokeAction(action);
	// } catch (InvokeActionException e2) {
	// throw new RouterException("Got exception when invocing action '"
	// + action.getName() + "' for device with udn '"
	// + getRouterDevice().getUDN() + "' and service id '"
	// + SHORTENED_SERVICE_ID + "'.", e2);
	// } catch (ActionFailedException e1) {
	//
	// // The error codes 713 and 714 mean, that no port mappings
	// // where found for the current entry. See bug reports
	// // https://sourceforge.net/tracker/index.php?func=detail&aid=
	// // 1939749&group_id=213879&atid=1027466
	// // and http://www.sbbi.net/forum/viewtopic.php?p=394
	// if (e1.getErrorCode() == 713 || e1.getErrorCode() == 714) {
	// return null;
	// }
	// throw new RouterException("Got exception when invocing action '"
	// + action.getName() + "' for device with udn '"
	// + getRouterDevice().getUDN() + "' and service id '"
	// + SHORTENED_SERVICE_ID + "'.", e1);
	// }
	//
	// String remoteHost;
	// int externalPort;
	// String protocolString;
	// int internalPort;
	// String internalClient;
	// boolean enabled;
	// String description;
	// long leaseDuration;
	// try {
	// remoteHost = action.getOutArgument("NewRemoteHost")
	// .getStringValue();
	// externalPort = (int) action.getOutArgument("NewExternalPort")
	// .getNumericValue();
	// protocolString = action.getOutArgument("NewProtocol")
	// .getStringValue();
	// internalPort = (int) action.getOutArgument("NewInternalPort")
	// .getNumericValue();
	// internalClient = action.getOutArgument("NewInternalClient")
	// .getStringValue();
	// enabled = action.getOutArgument("NewEnabled").getBooleanValue();
	// description = action.getOutArgument("NewPortMappingDescription")
	// .getStringValue();
	// leaseDuration = action.getOutArgument("NewLeaseDuration")
	// .getNumericValue();
	// } catch (Exception e) {
	// throw new RouterException(
	// "Got exception when getting arguments of action GetGenericPortMappingEntry.",
	// e);
	// }
	// if (protocolString == null) {
	// return null;
	// }
	//
	// Protocol protocol = protocolString.equalsIgnoreCase("TCP") ? Protocol.TCP
	// : Protocol.UDP;
	// return new PortMapping(protocol, remoteHost, externalPort,
	// internalClient, internalPort, description);
	// }
	//
	// public long getUpTime() throws RouterException {
	// CPAction statusAction = invokeAction("GetStatusInfo");
	// long uptime = -1;
	// try {
	// uptime = statusAction.getOutArgument("NewUptime").getNumericValue();
	// } catch (Exception e) {
	// throw new RouterException("Got exception when getting uptime.", e);
	// }
	// return uptime;
	// }
	//
	// public void logRouterInfo() throws RouterException {
	// CPDevice routerDevice = getRouterDevice().getCPDevice();
	// CPDevice[] devices = routerDevice.getCPDeviceTable();
	// for (CPDevice device : devices) {
	// logger.info("Sub-Device: " + device);
	// }
	//
	// CPService[] services = routerDevice.getCPServiceTable();
	// for (CPService service : services) {
	// logger.info("Service: " + service);
	//
	// // logger.info("Service Descrioption: "
	// // + service.getServiceDescription());
	// logger.info("Control URL: " + service.getControlURL());
	// String serviceId = service.getShortenedServiceId();
	// logger.info("shorted service id: " + serviceId);
	//
	// CPStateVariable[] vars = service.getCPStateVariableTable();
	// for (CPStateVariable stateVariable : vars) {
	// logger.info("Service '" + serviceId + "' state var "
	// + stateVariable.getName() + " = '"
	// + stateVariable.getValue() + "' \t\t(default: '"
	// + stateVariable.getDefaultValue() + "')");
	// logger.info("\tallowed values: "
	// + Arrays.toString(stateVariable.getAllowedValueList())
	// + " range: " + stateVariable.getAllowedValueRange());
	// }
	//
	// CPAction[] actions = service.getCPActionTable();
	// for (CPAction action : actions) {
	// logger.info("Service '" + serviceId + "' action: " + action);
	// Argument[] inArgs = action.getInArgumentTable();
	// if (inArgs != null) {
	// for (Argument argument : inArgs) {
	// logger.info("\tinarg: " + argument.getName());
	// }
	// }
	//
	// if (inArgs == null || inArgs.length == 0) {
	// // invokeAction(action);
	// }
	//
	// Argument[] outArgs = action.getOutArgumentTable();
	// if (outArgs != null) {
	// for (Argument argument : outArgs) {
	// logger.info("\toutarg: " + argument.getName() + ": "
	// + argument.getValue());
	// }
	// }
	// }
	//
	// }
	// }
	//
	// public void removeMapping(PortMapping mapping) throws RouterException {
	// removePortMapping(mapping.getProtocol(), mapping.getRemoteHost(),
	// mapping.getExternalPort());
	// }
	//
	// public void removePortMapping(Protocol protocol, String remoteHost,
	// int externalPort) throws RouterException {
	// CPAction action = getAction("DeletePortMapping");
	// try {
	// action.getInArgument("NewRemoteHost")
	// .setValueFromString(remoteHost);
	// action.getInArgument("NewExternalPort").setNumericValue(
	// externalPort);
	// action.getInArgument("NewProtocol").setValueFromString(
	// protocol.getValue());
	// } catch (Exception e) {
	// throw new RouterException(
	// "Got exception when setting arguments of action DeletePortMapping.",
	// e);
	// }
	// invokeAction(action);
	// }
	//
	// /**
	// * @return
	// */
	// public static IRouter findRouter() {
	// // StartupConfiguration config = new StartupConfiguration("config.xml");
	// StartupConfiguration config = new StartupConfiguration(
	// FokusRouterEntity.class, "upnp-entity.xml");
	// config.setStartKeyboardThread(false);
	// // config.setWorkingDirectory(path);
	// FokusRouterEntity entity = new FokusRouterEntity(config);
	// entity.createRouterControlPoint();
	//
	// while (!entity.isConnected()) {
	// logger.info("Not connected yet. Sleeping...");
	// try {
	// Thread.sleep(500);
	// } catch (InterruptedException e) {
	// }
	// }
	// // return entity;
	// // TODO
	// return null;
	// }

}
