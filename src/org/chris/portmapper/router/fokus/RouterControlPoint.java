package org.chris.portmapper.router.fokus;

import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.fraunhofer.fokus.upnp.core.control_point.CPDevice;
import de.fraunhofer.fokus.upnp.core.templates.TemplateControlPoint;
import de.fraunhofer.fokus.upnp.core.xml.StartupConfiguration;

public class RouterControlPoint extends TemplateControlPoint {

	private Log logger = LogFactory.getLog(this.getClass());
	private final static String DEVICE_NAME = "urn:schemas-upnp-org:device:WANConnectionDevice:";
	Vector<RouterCPDevice> routerList = new Vector<RouterCPDevice>();

	public RouterControlPoint(FokusRouterEntity anEntity,
			StartupConfiguration startupConfiguration) {
		super(anEntity, startupConfiguration);
	}

	public FokusRouterEntity getRouterEntity() {
		return (FokusRouterEntity) getTemplateEntity();
	}

	public void newDevice(CPDevice newDevice) {
		super.newDevice(newDevice);

		if (newDevice.getDeviceType().startsWith(DEVICE_NAME)) {
			if (!isKnownRouter(newDevice.getUDN())) {
				RouterCPDevice newRouter = new RouterCPDevice(this, newDevice);
				newRouter.addServerChangeListener(this);
				routerList.add(newRouter);
			}
		}
	}

	public void deviceGone(CPDevice goneDevice) {
		// remove from local list
		if (goneDevice.getDeviceType().startsWith(DEVICE_NAME)) {
			int index = getRouterIndex(goneDevice.getUDN());
			if (index != -1) {
				RouterCPDevice routerDevice = routerList.elementAt(index);
				routerDevice.terminate();
				routerList.remove(index);
			}
		}

		super.deviceGone(goneDevice);
	}

	/**
	 * Checks if a device with this udn is already in the list
	 */
	protected boolean isKnownRouter(String udn) {
		for (Object element : routerList) {
			RouterCPDevice router = (RouterCPDevice) element;
			if (router.getUDN().equals(udn)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the index for a router
	 */
	private int getRouterIndex(String udn) {
		for (int i = 0; i < routerList.size(); i++) {
			if ((routerList.elementAt(i).getCPDevice().getUDN().equals(udn))) {
				return i;
			}
		}
		return -1;
	}

	public void terminate() {
		for (Iterator<RouterCPDevice> iterator = routerList.iterator(); iterator
				.hasNext();) {
			RouterCPDevice router = iterator.next();
			router.terminate();
		}
		super.terminate();
	}

	public boolean isConnected() {
		return routerList.size() > 0;
	}

	public RouterCPDevice getRouterDevice() {
		return routerList.size() > 0 ? routerList.firstElement() : null;
	}

	// public void initPersonalization(StartupConfiguration
	// startupConfiguration) {
	// }
}
