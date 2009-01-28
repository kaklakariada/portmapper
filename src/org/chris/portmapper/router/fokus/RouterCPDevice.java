package org.chris.portmapper.router.fokus;

import java.util.Arrays;

import de.fraunhofer.fokus.upnp.core.Argument;
import de.fraunhofer.fokus.upnp.core.control_point.CPAction;
import de.fraunhofer.fokus.upnp.core.control_point.CPDevice;
import de.fraunhofer.fokus.upnp.core.control_point.CPService;
import de.fraunhofer.fokus.upnp.core.control_point.CPStateVariable;
import de.fraunhofer.fokus.upnp.core.event.ICPStateVariableListener;
import de.fraunhofer.fokus.upnp.core.exceptions.InvokeActionException;
import de.fraunhofer.fokus.upnp.util.exceptions.ActionFailedException;

public class RouterCPDevice {

	private CPDevice device;
	private RouterControlPoint routerControlPoint;

	public RouterCPDevice(RouterControlPoint routerControlPoint,
			CPDevice newDevice) {
		this.device = newDevice;
		this.routerControlPoint = routerControlPoint;
	}

	public CPDevice getCPDevice() {
		// TODO Auto-generated method stub
		return this.device;
	}

	public String getUDN() {
		return device.getUDN();
	}

	public void terminate() {
		device.terminate();
	}

	public void addServerChangeListener(ICPStateVariableListener stateListener) {
		device.addStateVariableListener(stateListener);
	}

	@Override
	public String toString() {

		CPDevice[] devices = device.getCPDeviceTable();
		for (CPDevice device : devices) {
			System.out.println("Sub-Device: " + device);
		}

		CPService[] services = device.getCPServiceTable();
		for (CPService service : services) {
			System.out.println("Service: " + service);

			// System.out.println("Service Descrioption: "
			// + service.getServiceDescription());
			System.out.println("Control URL: " + service.getControlURL());
			String serviceId = service.getShortenedServiceId();
			System.out.println("shorted service id: " + serviceId);

			CPStateVariable[] vars = service.getCPStateVariableTable();
			for (CPStateVariable stateVariable : vars) {
				System.out.println("Service '" + serviceId + "' state var "
						+ stateVariable.getName() + " = '"
						+ stateVariable.getValue() + "' \t\t(default: '"
						+ stateVariable.getDefaultValue() + "')");
				System.out.println("\tallowed values: "
						+ Arrays.toString(stateVariable.getAllowedValueList())
						+ " range: " + stateVariable.getAllowedValueRange());
			}

			CPAction[] actions = service.getCPActionTable();
			for (CPAction action : actions) {
				System.out.println("Service '" + serviceId + "' action: "
						+ action);
				Argument[] inArgs = action.getInArgumentTable();
				if (inArgs != null) {
					for (Argument argument : inArgs) {
						System.out.println("\tinarg: " + argument.getName());
					}
				}

				if (inArgs == null || inArgs.length == 0) {
					try {
						routerControlPoint.invokeAction(action);
					} catch (InvokeActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ActionFailedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				Argument[] outArgs = action.getOutArgumentTable();
				if (outArgs != null) {
					for (Argument argument : outArgs) {
						System.out.println("\toutarg: " + argument.getName()
								+ ": " + argument.getValue());
					}
				}
			}

		}

		return device.toString();
	}
}
