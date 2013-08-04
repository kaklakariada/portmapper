package org.chris.portmapper.router.sbbi;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.ActionResponse;
import net.sbbi.upnp.messages.UPNPResponseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.router.RouterException;

/**
 * This class fetches all {@link PortMapping} from an
 * {@link InternetGatewayDevice}.
 * 
 * @author chris
 */
class PortMappingExtractor {

	private final Log logger;
	private final InternetGatewayDevice router;
	private final Collection<PortMapping> mappings;
	private boolean moreEntries;
	private int currentMappingNumber;
	private int nullPortMappings;

	/**
	 * The maximum number of port mappings that we will try to retrieve from the
	 * router.
	 */
	private final int maxNumPortMappings;

	PortMappingExtractor(final InternetGatewayDevice router,
			final int maxNumPortMappings) {
		this(router, maxNumPortMappings, LogFactory
				.getLog(PortMappingExtractor.class));
	}

	PortMappingExtractor(final InternetGatewayDevice router,
			final int maxNumPortMappings, final Log logger) {
		this.router = router;
		this.maxNumPortMappings = maxNumPortMappings;
		this.logger = logger;
		this.mappings = new LinkedList<>();
		this.moreEntries = true;
		this.currentMappingNumber = 0;
		this.nullPortMappings = 0;
	}

	public Collection<PortMapping> getPortMappings() throws RouterException {

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

			while (morePortMappingsAvailable()) {
				logger.debug("Getting port mapping with entry number "
						+ currentMappingNumber + "...");

				try {
					final ActionResponse response = router
							.getGenericPortMappingEntry(currentMappingNumber);
					addResponse(response);
				} catch (final UPNPResponseException e) {
					handleUPNPResponseException(e);
				}

				currentMappingNumber++;
			}

			checkMaxNumPortMappingsReached();

		} catch (final IOException e) {
			throw new RouterException("Could not get NAT mappings: "
					+ e.getMessage(), e);
		}

		logger.info("Found " + mappings.size() + " mappings, "
				+ nullPortMappings + " mappings returned as null.");
		return mappings;
	}

	/**
	 * Check, if the max number of entries is reached and print a warning
	 * message.
	 */
	private void checkMaxNumPortMappingsReached() {
		if (currentMappingNumber == maxNumPortMappings) {
			logger.warn("Reached max number of port mappings to get ("
					+ maxNumPortMappings
					+ "). Perhaps not all port mappings where retrieved. Try to increase SBBIRouter.MAX_NUM_PORTMAPPINGS.");
		}
	}

	private boolean morePortMappingsAvailable() {
		return moreEntries && currentMappingNumber < maxNumPortMappings;
	}

	private void addResponse(final ActionResponse response) {
		// Create a port mapping for the response.
		if (response != null) {
			final PortMapping newMapping = PortMapping.create(response);
			if (logger.isTraceEnabled()) {
				logger.trace("Got port mapping #" + currentMappingNumber + ": "
						+ newMapping.getCompleteDescription());
			}
			mappings.add(newMapping);
		} else {
			nullPortMappings++;
			if (logger.isTraceEnabled()) {
				logger.trace("Got a null port mapping for number "
						+ currentMappingNumber + " (" + nullPortMappings
						+ " so far).");
			}
		}
	}

	private void handleUPNPResponseException(final UPNPResponseException e) {
		if (isNoMoreMappingsException(e)) {

			moreEntries = false;
			logger.debug("Got no port mapping for entry number "
					+ currentMappingNumber + " (error code: "
					+ e.getDetailErrorCode() + ", error description: "
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

}
