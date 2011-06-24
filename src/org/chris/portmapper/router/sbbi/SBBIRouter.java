package org.chris.portmapper.router.sbbi;

import java.io.IOException;
import java.net.URL;
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
import org.chris.portmapper.Settings;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.RouterException;
import org.chris.portmapper.util.EncodingUtilities;

/**
 * This class represents a router device and provides methods for managing port
 * mappings and getting information about the router. It useses the SBBI
 * library's {@link InternetGatewayDevice}.
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
	private final static int MAX_NUM_PORTMAPPINGS = 500;

	SBBIRouter(final InternetGatewayDevice router) {
		super(router.getIGDRootDevice().getModelName());
		this.router = router;
	}

	public String getExternalIPAddress() throws RouterException {
		logger.debug("Get external IP address...");
		String ipAddress;
		try {
			ipAddress = router.getExternalIPAddress();
		} catch (final UPNPResponseException e) {
			throw new RouterException("Could not get external IP", e);
		} catch (final IOException e) {
			throw new RouterException("Could not get external IP", e);
		}
		logger.info("Got external IP address " + ipAddress + " for router.");
		return ipAddress;
	}

	public String getInternalHostName() {
		logger.debug("Get internal IP address...");
		final URL presentationURL = router.getIGDRootDevice()
				.getPresentationURL();
		if (presentationURL == null) {
			logger.warn("Did not get presentation url");
			return null;
		}
		final String ipAddress = presentationURL.getHost();
		logger.info("Got internal host name '" + ipAddress + "' for router.");
		return ipAddress;
	}

	public int getInternalPort() {
		logger.debug("Get internal port of router...");
		final URL presentationURL = router.getIGDRootDevice()
				.getPresentationURL();
		// Presentation URL may be null in some situations.
		if (presentationURL != null) {
			final int presentationUrlPort = presentationURL.getPort();
			// https://sourceforge.net/tracker/?func=detail&aid=3198378&group_id=213879&atid=1027466
			// Some routers send an invalid presentationURL, in this case use
			// URLBase.
			if (presentationUrlPort > 0) {
				logger.debug("Got valid internal port " + presentationUrlPort
						+ " from presentation URL.");
				return presentationUrlPort;
			} else {
				logger.debug("Got invalid port " + presentationUrlPort
						+ " from presentation url " + presentationURL);
			}
		} else {
			logger.debug("Presentation url is null");
		}
		final int urlBasePort = router.getIGDRootDevice().getURLBase()
				.getPort();
		logger.debug("Presentation URL is null or returns invalid port: using url base port "
				+ urlBasePort);

		return urlBasePort;
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
					final ActionResponse response = router
							.getGenericPortMappingEntry(currentMappingNumber);

					// Create a port mapping for the response.
					if (response != null) {
						final PortMapping newMapping = PortMapping
								.create(response);
						if (logger.isTraceEnabled()) {
							logger.trace("Got port mapping #"
									+ currentMappingNumber + ": "
									+ newMapping.getCompleteDescription());
						}
						mappings.add(newMapping);
					} else {
						logger.warn("Got a null port mapping for number "
								+ currentMappingNumber
								+ ". This may be a bug in UPNPLib.");
					}
				} catch (final UPNPResponseException e) {

					if (isNoMoreMappingsException(e)) {

						moreEntries = false;
						logger.debug("Got no port mapping for entry number "
								+ currentMappingNumber + " (error code: "
								+ e.getDetailErrorCode()
								+ ", error description: "
								+ e.getDetailErrorDescription()
								+ "). Stop getting more entries.");
					} else {
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
				logger.warn("Reached max number of port mappings to get ("
						+ MAX_NUM_PORTMAPPINGS
						+ "). Perhaps not all port mappings where retrieved. Try to increase SBBIRouter.MAX_NUM_PORTMAPPINGS.");
			}

		} catch (final IOException e) {
			throw new RouterException("Could not get NAT mappings", e);
		}

		return mappings;
	}

	/**
	 * This method checks, if the error code of the given exception means, that
	 * no more mappings are available.
	 * <p>
	 * The following error codes are recognized:
	 * <ul>
	 * <li>SpecifiedArrayIndexInvalid: 713</li>
	 * <li>NoSuchEntryInArray: 714</li>
	 * <li>Invalid Args: 402 (e.g. for DD-WRT, TP-LINK TL-R460 firmware 4.7.6
	 * Build 100714 Rel.63134n)</li>
	 * <li>Other errors, e.g.
	 * "The reference to entity "T" must end with the ';' delimiter" or
	 * "Content is not allowed in prolog": 899 (e.g. ActionTec MI424-WR, Thomson
	 * TWG850-4U)</li>
	 * </ul>
	 * See bug reports
	 * <ul>
	 * <li><a href=
	 * "https://sourceforge.net/tracker/index.php?func=detail&aid=1939749&group_id=213879&atid=1027466"
	 * >https://sourceforge.net/tracker/index.php?func=detail&aid=
	 * 1939749&group_id=213879&atid=1027466</a></li>
	 * <li><a
	 * href="http://www.sbbi.net/forum/viewtopic.php?p=394">http://www.sbbi
	 * .net/forum/viewtopic.php?p=394</a></li>
	 * <li><a href=
	 * "http://sourceforge.net/tracker/?func=detail&atid=1027466&aid=3325388&group_id=213879"
	 * >http://sourceforge.net/tracker/?func=detail&atid=1027466&aid=3325388&
	 * group_id=213879</a></li>
	 * <a href=
	 * "https://sourceforge.net/tracker2/?func=detail&aid=2540478&group_id=213879&atid=1027466"
	 * >https://sourceforge.net/tracker2/?func=detail&aid=2540478&group_id=
	 * 213879&atid=1027466</a></li>
	 * </ul>
	 * 
	 * @param e
	 *            the exception to check
	 * @return <code>true</code>, if the given exception means, that no more
	 *         port mappings are available, else <code>false</code>.
	 */
	private boolean isNoMoreMappingsException(final UPNPResponseException e) {
		final int errorCode = e.getDetailErrorCode();
		switch (errorCode) {
		case 713:
		case 714:
		case 402:
		case 899:
			return true;

		default:
			return false;
		}
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
		info.put("presentationURL",
				rootDevice.getPresentationURL() != null ? rootDevice
						.getPresentationURL().toExternalForm() : null);
		info.put("urlBase", rootDevice.getURLBase().toExternalForm());

		final SortedSet<String> sortedKeys = new TreeSet<String>(info.keySet());

		for (final String key : sortedKeys) {
			final String value = info.get(key);
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

	private boolean addPortMapping(String description, final Protocol protocol,
			final String remoteHost, final int externalPort,
			final String internalClient, final int internalPort,
			final int leaseDuration) throws RouterException {

		final String protocolString = (protocol.equals(Protocol.TCP) ? "TCP"
				: "UDP");

		final Settings settings = PortMapperApp.getInstance().getSettings();
		if (settings == null || settings.isUseEntityEncoding()) {
			description = EncodingUtilities.htmlEntityEncode(description);
		}

		try {
			final boolean success = router.addPortMapping(description, null,
					internalPort, externalPort, internalClient, leaseDuration,
					protocolString);
			return success;
		} catch (final IOException e) {
			throw new RouterException("Could not add port mapping: "
					+ e.getMessage(), e);
		} catch (final UPNPResponseException e) {
			throw new RouterException("Could not add port mapping: "
					+ e.getMessage(), e);
		}
	}

	public void addPortMappings(final Collection<PortMapping> mappings)
			throws RouterException {
		for (final PortMapping portMapping : mappings) {
			logger.info("Adding port mapping " + portMapping);
			addPortMapping(portMapping);
		}
	}

	public void addPortMapping(final PortMapping mapping)
			throws RouterException {
		logger.info("Adding port mapping " + mapping.getCompleteDescription());
		addPortMapping(mapping.getDescription(), mapping.getProtocol(),
				mapping.getRemoteHost(), mapping.getExternalPort(),
				mapping.getInternalClient(), mapping.getInternalPort(), 0);
	}

	public void removeMapping(final PortMapping mapping) throws RouterException {
		removePortMapping(mapping.getProtocol(), mapping.getRemoteHost(),
				mapping.getExternalPort());
	}

	public void removePortMapping(final Protocol protocol,
			final String remoteHost, final int externalPort)
			throws RouterException {
		final String protocolString = (protocol.equals(Protocol.TCP) ? "TCP"
				: "UDP");
		try {
			router.deletePortMapping(remoteHost, externalPort, protocolString);
		} catch (final IOException e) {
			throw new RouterException("Could not remove port mapping", e);
		} catch (final UPNPResponseException e) {
			throw new RouterException("Could not remove port mapping", e);
		}
	}

	public void disconnect() {
		// Nothing to do right now.
	}

	public long getUpTime() throws RouterException {
		// The SBBI library does not provide a method for getting the uptime.
		return 0;
	}
}
