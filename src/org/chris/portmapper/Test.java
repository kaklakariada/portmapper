/**
 * 
 */
package org.chris.portmapper;

import java.io.IOException;
import java.net.InetAddress;

import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.UPNPResponseException;

/**
 * @author chris
 * 
 */
public class Test {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		int discoveryTimeout = 5000; // 5 secs to receive a response from
										// devices
		try {
			InternetGatewayDevice[] IGDs = InternetGatewayDevice
					.getDevices(discoveryTimeout);
			if (IGDs != null) {
				// let's the the first device found
				InternetGatewayDevice testIGD = IGDs[0];
				System.out.println("Found device "
						+ testIGD.getIGDRootDevice().getModelName());
				System.out.println("External adress: "
						+ testIGD.getExternalIPAddress());
				// now let's open the port
				String localHostIP = InetAddress.getLocalHost()
						.getHostAddress();
				// we assume that localHostIP is something else than 127.0.0.1
				boolean mapped = testIGD.addPortMapping(
						"Some mapping description", null, 9090, 9090,
						localHostIP, 0, "TCP");
				if (mapped) {
					System.out.println("Port 9090 mapped to " + localHostIP);
					// and now close it
					boolean unmapped = testIGD.deletePortMapping(null, 9090,
							"TCP");
					if (unmapped) {
						System.out.println("Port 9090 unmapped");
					}
				}
			} else {
				System.out.println("No devices found");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (UPNPResponseException respEx) {
			respEx.printStackTrace();
		}

	}

}
