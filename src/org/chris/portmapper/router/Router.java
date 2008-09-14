package org.chris.portmapper.router;

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
import org.chris.portmapper.util.EncodingUtilities;

/**
 * This class represents a router device and provides methods for managing port
 * mappings and getting information about the router.
 * 
 * @author chris
 * 
 */
public class Router {

	private Log logger = LogFactory.getLog(this.getClass());

	/**
	 * The wrapped router device.
	 */
	private InternetGatewayDevice router = null;

	/**
	 * The timeout in milliseconds for finding a router device.
	 */
	private final static int DISCOVERY_TIMEOUT = 5000;

	/**
	 * The maximum number of port mappings that we will try to retrieve from the
	 * router.
	 */
	private final static int MAX_NUM_PORTMAPPINGS = 100;

	private Router(InternetGatewayDevice router) {
		if (router == null) {
			throw new IllegalArgumentException("No router given");
		}
		this.router = router;
	}

	/**
	 * Find the router device in the network.
	 * 
	 * @return the router device.
	 * @throws RouterException
	 *             if no or more than one router devices where found.
	 */

	public static Router findRouter() throws RouterException {
		InternetGatewayDevice devices = findInternetGatewayDevice();
		Router r = new Router(devices);
		return r;
	}

	/**
	 * Find all router devices in the network and check, that only one is found.
	 * 
	 * @return the router device.
	 * @throws RouterException
	 *             if no or more than one router devices where found.
	 */
	private static InternetGatewayDevice findInternetGatewayDevice()
			throws RouterException {
		InternetGatewayDevice[] devices;
		try {
			devices = InternetGatewayDevice.getDevices(DISCOVERY_TIMEOUT);
		} catch (IOException e) {
			throw new RouterException("Could not find devices", e);
		}

		if (devices == null || devices.length == 0) {
			throw new RouterException("No router devices found");
		}

		if (devices.length != 1) {
			throw new RouterException("Found more than one router devices ("
					+ devices.length + ")");
		}
		return devices[0];
	}

	public String getName() throws RouterException {
		return router.getIGDRootDevice().getModelName();
	}

	/**
	 * Get the external IP of the router.
	 * 
	 * @return the external IP of the router.
	 */
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

	/**
	 * Get the internal host name or IP of the router.
	 * 
	 * @return the internal host name or IP of the router.
	 */
	public String getInternalHostName() {
		logger.debug("Get internal IP address...");
		String ipAddress;
		ipAddress = router.getIGDRootDevice().getPresentationURL().getHost();
		logger.info("Got internal host name '" + ipAddress + "' for router.");
		return ipAddress;
	}

	/**
	 * Get the internal port of the router.
	 * 
	 * @return the internal port of the router.
	 */
	public int getInternalPort() {
		logger.debug("Get internal port of router...");
		int port = router.getIGDRootDevice().getPresentationURL().getPort();
		logger.info("Got internal port " + port + " for router.");
		return port;
	}

	/**
	 * Get all port mappings from the router.
	 * 
	 * @return all port mappings from the router.
	 * @throws RouterException
	 *             if something went wrong when getting the port mappings.
	 */
	public Collection<PortMapping> getPortMappings() throws RouterException {
		logger.info("Get all port mappings...");
		Collection<PortMapping> mappings = new LinkedList<PortMapping>();
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
					//https://sourceforge.net/tracker/index.php?func=detail&aid=
					// 1939749&group_id=213879&atid=1027466
					// and http://www.sbbi.net/forum/viewtopic.php?p=394
					if (e.getDetailErrorCode() == 713
							|| e.getDetailErrorCode() == 714) {

						moreEntries = false;
						logger.debug("Got no port mapping for entry number "
								+ currentMappingNumber
								+ ". Stop getting more entries.");
					} else {
						throw e;
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
		} catch (UPNPResponseException e) {
			throw new RouterException("Could not get NAT mappings", e);
		}

		return mappings;
	}

	public void logRouterInfo() throws RouterException {
		Map<String, String> info = new HashMap<String, String>();
		UPNPRootDevice rootDevice = router.getIGDRootDevice();
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

	public boolean addPortMappings(Collection<PortMapping> mappings)
			throws RouterException {
		for (PortMapping portMapping : mappings) {
			logger.info("Adding port mapping " + portMapping);
			boolean success = addPortMapping(portMapping);
			if (!success) {
				return false;
			}
		}
		return true;
	}

	public boolean addPortMapping(PortMapping mapping) throws RouterException {
		logger.info("Adding port mapping " + mapping.getCompleteDescription());
		return addPortMapping(mapping.getDescription(), mapping.getProtocol(),
				mapping.getRemoteHost(), mapping.getExternalPort(), mapping
						.getInternalClient(), mapping.getInternalPort(), 0);
	}

	public boolean removeMapping(PortMapping mapping) throws RouterException {
		return removePortMapping(mapping.getProtocol(),
				mapping.getRemoteHost(), mapping.getExternalPort());

	}

	public boolean removePortMapping(Protocol protocol, String remoteHost,
			int externalPort) throws RouterException {
		String protocolString = (protocol.equals(Protocol.TCP) ? "TCP" : "UDP");
		try {
			boolean success = router.deletePortMapping(remoteHost,
					externalPort, protocolString);
			return success;
		} catch (IOException e) {
			throw new RouterException("Could not remove SSH port mapping", e);
		} catch (UPNPResponseException e) {
			throw new RouterException("Could not remove SSH port mapping", e);
		}
	}

	public void disconnect() {
		// Nothing to do right now.
	}

	public long getValidityTime() {
		return router.getIGDRootDevice().getValidityTime();
	}
}
