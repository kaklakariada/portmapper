/* 
 *              weupnp - Trivial upnp java library 
 *
 * Copyright (C) 2008 Alessandro Bahgat Shehata, Daniele Castagna
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Alessandro Bahgat Shehata - ale dot bahgat at gmail dot com
 * Daniele Castagna - daniele dot castagna at gmail dot com
 * 
 */

/*
 * refer to miniupnpc-1.0-RC8
 */
package org.wetorrent.upnp;

import java.net.InetAddress;

public class Main {

	public Main() {
	}

	public static void main(String[] args) throws Exception {
		GatewayDiscover discover = new GatewayDiscover();
		discover.discover();
		GatewayDevice d = discover.getValidGateway();

		String externalIPAddress = d.getExternalIPAddress();
		System.err.println("ex ip:" + externalIPAddress);
		PortMappingEntry portMapping = new PortMappingEntry();
		if (d.getSpecificPortMappingEntry(6991, "TCP", portMapping) == null) {

			if (d.addPortMapping(6991, 6991, InetAddress.getLocalHost()
					.getHostAddress(), "TCP", "test")) {

				Thread.sleep(1000 * 10);
				d.deletePortMapping(6991, "TCP");
			}

		}

	}

}
