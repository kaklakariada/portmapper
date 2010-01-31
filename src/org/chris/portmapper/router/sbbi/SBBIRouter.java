package org.chris.portmapper.router.sbbi;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sbbi.upnp.devices.UPNPRootDevice;
import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.ActionResponse;
import net.sbbi.upnp.messages.UPNPResponseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.RouterException;
import org.chris.portmapper.util.EncodingUtilities;

/**
 * This class represents a router device and provides methods for managing port
 * mappings and getting information about the router.
 * 
 * @author chris
 * @version $Id$
 */
public class SBBIRouter extends AbstractRouter {

	private final Log logger = LogFactory.getLog(this.getClass());

	/**
	 * The wrapped router device.
	 */
	final private InternetGatewayDevice router;

	/**
	 * The maximum number of port mappings that we will try to retrieve from the
	 * router.
	 */
	private final static int MAX_NUM_PORTMAPPINGS = 100;

	SBBIRouter(InternetGatewayDevice router) {
		super(router.getIGDRootDevice().getModelName());
		if (router == null) {
			throw new IllegalArgumentException("No router given");
		}
		this.router = router;
	}

	public String getExternalIPAddress() throws RouterException {
		logger.debug("Get external IP address...");
		String ipAddress;
		try {
			ipAddress = router.getExternalIPAddress();
		} catch (UPNPResponseException e) {
			throw new RouterException("Could not get external IP", e);
		} catch (IOException e) {
			throw new RouterException("Could not get external IP", e);
		}
		logger.info("Got external IP address " + ipAddress + " for router.");
		return ipAddress;
	}

	public String getInternalHostName() {
		logger.debug("Get internal IP address...");
		final String ipAddress = router.getIGDRootDevice().getPresentationURL()
				.getHost();
		logger.info("Got internal host name '" + ipAddress + "' for router.");
		return ipAddress;
	}

	public int getInternalPort() {
		logger.debug("Get internal port of router...");
		final int port = router.getIGDRootDevice().getPresentationURL()
				.getPort();
		logger.info("Got internal port " + port + " for router.");
		return port;
	}

	public Collection<PortMapping> getPortMappings() throws RouterException {
		logger.info("Get all port mappings...");
		final Collection<PortMapping> mappings = new LinkedList<PortMapping>();
		try {

			/*
			 * This is a little trick to get all port mappings. There is a
			 * method that gets the number of available port mappings
			 * (getNatMappingsCount()), but it seems, that this method just
			 * tries to get all port mappings and checks, if an error is
			 * returned.
			 * 
			 * In order to speed this up, we will do the same here, but stop,
			 * when the first exception is thrown.
			 */
			boolean moreEntries = true;
			int currentMappingNumber = 0;
			while (moreEntries && currentMappingNumber < MAX_NUM_PORTMAPPINGS) {
				logger.debug("Getting port mapping with entry number "
						+ currentMappingNumber + "...");

				try {
					ActionResponse response = router
							.getGenericPortMappingEntry(currentMappingNumber);

					// Create a port mapping for the response.
					if (response != null) {
						mappings.add(PortMapping.create(response));
					} else {
						logger.warn("Got a null port mapping for number "
								+ currentMappingNumber
								+ ". This may be a bug in UPNPLib.");
					}
				} catch (UPNPResponseException e) {

					// The error codes 713 and 714 mean, that no port mappings
					// where found for the current entry. See bug reports
					// https://sourceforge.net/tracker/index.php?func=detail&aid=
					// 1939749&group_id=213879&atid=1027466
					// and http://www.sbbi.net/forum/viewtopic.php?p=394
					if (e.getDetailErrorCode() == 713
							|| e.getDetailErrorCode() == 714) {

						moreEntries = false;
						logger.debug("Got no port mapping for entry number "
								+ currentMappingNumber
								+ ". Stop getting more entries.");
					} else {
						// Also ignore all other exceptions to workaround
						// possible router bugs.
						// https://sourceforge.net/tracker2/?func=detail&aid=2540478&group_id=213879&atid=1027466
						moreEntries = false;
						logger.error(
								"Got exception when fetching port mapping for entry number "
										+ currentMappingNumber
										+ ". Stop getting more entries.", e);
					}
				}

				currentMappingNumber++;
			}

			// Check, if the max number of entries is reached and print a
			// warning message.
			if (currentMappingNumber == MAX_NUM_PORTMAPPINGS) {
				logger
						.warn("Reached max number of port mappings to get ("
								+ MAX_NUM_PORTMAPPINGS
								+ "). Perhaps not all port mappings where retrieved. Try to increase Router.MAX_NUM_PORTMAPPINGS.");
			}

		} catch (IOException e) {
			throw new RouterException("Could not get NAT mappings", e);
		}

		return mappings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouter#logRouterInfo()
	 */
	public void logRouterInfo() throws RouterException {
		final Map<String, String> info = new HashMap<String, String>();
		final UPNPRootDevice rootDevice = router.getIGDRootDevice();
		info.put("friendlyName", rootDevice.getFriendlyName());
		info.put("manufacturer", rootDevice.getManufacturer());
		info.put("modelDescription", rootDevice.getModelDescription());
		info.put("modelName", rootDevice.getModelName());
		info.put("serialNumber", rootDevice.getSerialNumber());
		info.put("vendorFirmware", rootDevice.getVendorFirmware());

		info.put("modelNumber", rootDevice.getModelNumber());
		info.put("modelURL", rootDevice.getModelURL());
		info.put("manufacturerURL", rootDevice.getManufacturerURL()
				.toExternalForm());
		info.put("presentationURL", rootDevice.getPresentationURL()
				.toExternalForm());
		info.put("urlBase", rootDevice.getURLBase().toExternalForm());

		SortedSet<String> sortedKeys = new TreeSet<String>(info.keySet());

		for (String key : sortedKeys) {
			String value = info.get(key);
			logger.info("Router Info: " + key + " \t= " + value);
		}

		logger.info("def loc " + rootDevice.getDeviceDefLoc());
		logger.info("def loc data " + rootDevice.getDeviceDefLocData());
		logger.info("icons " + rootDevice.getDeviceIcons());
		logger.info("device type " + rootDevice.getDeviceType());
		logger.info("direct parent " + rootDevice.getDirectParent());
		logger.info("disc udn " + rootDevice.getDiscoveryUDN());
		logger.info("disc usn " + rootDevice.getDiscoveryUSN());
		logger.info("udn " + rootDevice.getUDN());
	}

	private boolean addPortMapping(String description, Protocol protocol,
			String remoteHost, int externalPort, String internalClient,
			int internalPort, int leaseDuration) throws RouterException {
		String protocolString = (protocol.equals(Protocol.TCP) ? "TCP" : "UDP");

		if (PortMapperApp.getInstance().getSettings().isUseEntityEncoding()) {
			description = EncodingUtilities.htmlEntityEncode(description);
		}

		try {
			boolean success = router.addPortMapping(description, null,
					internalPort, externalPort, internalClient, leaseDuration,
					protocolString);
			return success;
		} catch (IOException e) {
			throw new RouterException("Could not add port mapping", e);
		} catch (UPNPResponseException e) {
			throw new RouterException("Could not add port mapping", e);
		}
	}

	public void addPortMappings(Collection<PortMapping> mappings)
			throws RouterException {
		for (PortMapping portMapping : mappings) {
			logger.info("Adding port mapping " + portMapping);
			addPortMapping(portMapping);
		}
	}

	public void addPortMapping(PortMapping mapping) throws RouterException {
		logger.info("Adding port mapping " + mapping.getCompleteDescription());
		addPortMapping(mapping.getDescription(), mapping.getProtocol(), mapping
				.getRemoteHost(), mapping.getExternalPort(), mapping
				.getInternalClient(), mapping.getInternalPort(), 0);
	}

	public void removeMapping(PortMapping mapping) throws RouterException {
		removePortMapping(mapping.getProtocol(), mapping.getRemoteHost(),
				mapping.getExternalPort());

	}

	public void removePortMapping(Protocol protocol, String remoteHost,
			int externalPort) throws RouterException {
		String protocolString = (protocol.equals(Protocol.TCP) ? "TCP" : "UDP");
		try {
			router.deletePortMapping(remoteHost, externalPort, protocolString);
		} catch (IOException e) {
			throw new RouterException("Could not remove port mapping", e);
		} catch (UPNPResponseException e) {
			throw new RouterException("Could not remove port mapping", e);
		}
	}

	public void disconnect() {
		// Nothing to do right now.
	}

	public long getValidityTime() {
		return router.getIGDRootDevice().getValidityTime();
	}

	public long getUpTime() throws RouterException {
		// TODO Auto-generated method stub
		return 0;
	}
}
