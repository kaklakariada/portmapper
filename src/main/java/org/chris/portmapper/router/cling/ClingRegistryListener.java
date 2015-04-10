package org.chris.portmapper.router.cling;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClingRegistryListener extends DefaultRegistryListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final DeviceType IGD_DEVICE_TYPE = new UDADeviceType("InternetGatewayDevice", 1);
    public static final DeviceType CONNECTION_DEVICE_TYPE = new UDADeviceType("WANConnectionDevice", 1);

    public static final ServiceType IP_SERVICE_TYPE = new UDAServiceType("WANIPConnection", 1);
    public static final ServiceType PPP_SERVICE_TYPE = new UDAServiceType("WANPPPConnection", 1);

    private final SynchronousQueue<Service<?, ?>> foundServices;

    public ClingRegistryListener() {
        this.foundServices = new SynchronousQueue<>();
    }

    public Service<?, ?> waitForServiceFound(final long timeout, final TimeUnit unit) {
        try {
            return foundServices.poll(timeout, unit);
        } catch (final InterruptedException e) {
            logger.warn("Interrupted when waiting for a service");
            return null;
        }
    }

    @Override
    public void deviceAdded(final Registry registry, @SuppressWarnings("rawtypes") final Device device) {
        @SuppressWarnings("unchecked")
        final Service<?, ?> connectionService = discoverConnectionService(device);
        if (connectionService == null) {
            return;
        }

        logger.debug("Found connection service {}", connectionService);
        foundServices.offer(connectionService);
    }

    protected Service<?, ?> discoverConnectionService(@SuppressWarnings("rawtypes") final Device<?, Device, ?> device) {
        if (!device.getType().equals(IGD_DEVICE_TYPE)) {
            logger.debug("Found service of wrong type {}, expected {}.", device.getType(), IGD_DEVICE_TYPE);
            return null;
        }

        @SuppressWarnings("rawtypes")
        final Device[] connectionDevices = device.findDevices(CONNECTION_DEVICE_TYPE);
        if (connectionDevices.length == 0) {
            logger.debug("IGD doesn't support '{}': {}", CONNECTION_DEVICE_TYPE, device);
            return null;
        }

        logger.debug("Found {} devices", connectionDevices.length);

        return findConnectionService(connectionDevices);
    }

    @SuppressWarnings("rawtypes")
    private Service<?, ?> findConnectionService(final Device[] connectionDevices) {
        for (final Device connectionDevice : connectionDevices) {

            final Service ipConnectionService = connectionDevice.findService(IP_SERVICE_TYPE);
            final Service pppConnectionService = connectionDevice.findService(PPP_SERVICE_TYPE);

            if (ipConnectionService != null) {
                logger.debug("Device {} supports ip service type: {}", connectionDevice, ipConnectionService);
                return ipConnectionService;
            }
            if (pppConnectionService != null) {
                logger.debug("Device {} supports ppp service type: {}", connectionDevice, pppConnectionService);
                return pppConnectionService;
            }

            logger.debug("IGD {} doesn't support IP or PPP WAN connection service", connectionDevice);
        }
        logger.debug("None of the {} devices supports IP or PPP WAN connections", connectionDevices.length);
        return null;
    }
}
