package org.chris.portmapper.router.cling;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

/**
 *
 */
public class ClingRegistryListener extends DefaultRegistryListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final DeviceType IGD_DEVICE_TYPE = new UDADeviceType(
			"InternetGatewayDevice", 1);
	public static final DeviceType CONNECTION_DEVICE_TYPE = new UDADeviceType(
			"WANConnectionDevice", 1);

	public static final ServiceType IP_SERVICE_TYPE = new UDAServiceType(
			"WANIPConnection", 1);
	public static final ServiceType PPP_SERVICE_TYPE = new UDAServiceType(
			"WANPPPConnection", 1);

	private final SynchronousQueue<Service> foundServices;

	public ClingRegistryListener() {
		this.foundServices = new SynchronousQueue<>();
	}

	public Service waitForServiceFound(final long timeout, final TimeUnit unit) {
		try {
			return foundServices.poll(timeout, unit);
		} catch (final InterruptedException e) {
			logger.warn("Interrupted when waiting for a service");
			return null;
		}
	}

	@Override
	public void deviceAdded(final Registry registry, final Device device) {

		final Service connectionService = discoverConnectionService(device);
		if (connectionService == null) {
			logger.debug("Found service " + connectionService
					+ " of wrong type, skip.");
			return;
		}

		logger.debug("Found connection service " + connectionService);
		foundServices.offer(connectionService);
	}

	protected Service discoverConnectionService(final Device device) {
		if (!device.getType().equals(IGD_DEVICE_TYPE)) {
			return null;
		}

		final Device[] connectionDevices = device
				.findDevices(CONNECTION_DEVICE_TYPE);
		if (connectionDevices.length == 0) {
			logger.debug("IGD doesn't support '" + CONNECTION_DEVICE_TYPE
					+ "': " + device);
			return null;
		}

		final Device connectionDevice = connectionDevices[0];
		logger.debug("Using first discovered WAN connection device: "
				+ connectionDevice);

		final Service ipConnectionService = connectionDevice
				.findService(IP_SERVICE_TYPE);
		final Service pppConnectionService = connectionDevice
				.findService(PPP_SERVICE_TYPE);

		if (ipConnectionService == null && pppConnectionService == null) {
			logger.debug("IGD doesn't support IP or PPP WAN connection service: "
					+ device);
		}

		return ipConnectionService != null ? ipConnectionService
				: pppConnectionService;
	}
}
