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

package org.wetorrent.upnp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class GatewayDiscover {

	private static final int PORT = 1900;
	private final String IP = "239.255.255.250";

	private final Map<InetAddress, GatewayDevice> devices = new HashMap<InetAddress, GatewayDevice>();

	public GatewayDiscover() {
	}

	public Map<InetAddress, GatewayDevice> discover() throws WeUPnPException {

		DatagramSocket ssdp = null;
		try {
			// try binding using the default port
			ssdp = new DatagramSocket(PORT);
		} catch (BindException be) {
			// could not bind to the default port
		} catch (SocketException e) {
			throw new WeUPnPException("Error discovering gateway devices", e);
		}
		try {
			if (null == ssdp) {
				// let the JVM choose an available port
				ssdp = new DatagramSocket();
			}
		} catch (SocketException e) {
			throw new WeUPnPException("Error discovering gateway devices", e);
		}

		int port = ssdp.getLocalPort();

		final String searchMessage = "M-SEARCH * HTTP/1.1\r\n" + "HOST: " + IP
				+ ":" + port + "\r\n" + "ST: "
				+ "urn:schemas-upnp-org:device:InternetGatewayDevice:1"
				+ "\r\n" + "MAN: \"ssdp:discover\"\r\n" + "MX: 2\r\n" + "\r\n";
		try {
			byte[] searchMessageBytes = searchMessage.getBytes();
			DatagramPacket ssdpDiscoverPacket = new DatagramPacket(
					searchMessageBytes, searchMessageBytes.length);
			try {
				ssdpDiscoverPacket.setAddress(InetAddress.getByName(IP));
			} catch (UnknownHostException e1) {
				throw new WeUPnPException("Error resolving host with name "
						+ IP, e1);
			}
			ssdpDiscoverPacket.setPort(PORT);

			ssdp.send(ssdpDiscoverPacket);
			ssdp.setSoTimeout(3000);

			boolean waitingPacket = true;

			while (waitingPacket) {
				DatagramPacket receivePacket = new DatagramPacket(
						new byte[1536], 1536);
				try {
					ssdp.receive(receivePacket);
					byte[] receivedData = new byte[receivePacket.getLength()];
					System.arraycopy(receivePacket.getData(), 0, receivedData,
							0, receivePacket.getLength());

					// TODO: devices should be a map, and receivePacket.address
					// should be the key ;)
					GatewayDevice d = parseMSearchReplay(receivedData);

					/* Get local address as it appears to the Gateway */
					DatagramSocket sock = new DatagramSocket();
					sock.connect(receivePacket.getSocketAddress());
					InetAddress localAddress = sock.getLocalAddress();
					sock.disconnect();
					sock = null;

					d.setLocalAddress(localAddress);
					devices.put(localAddress, d);
				} catch (SocketTimeoutException ste) {
					waitingPacket = false;
				}
			}

			for (GatewayDevice device : devices.values()) {
				try {
					device.loadDescription();
				} catch (Exception e) {
				}
			}
		} catch (IOException e) {
			throw new WeUPnPException("Error discovering gateway devices", e);
		} finally {
			ssdp.close();
		}
		return devices;
	}

	private GatewayDevice parseMSearchReplay(byte[] reply)
			throws WeUPnPException {

		GatewayDevice device = new GatewayDevice();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(reply)));
		String line = null;
		try {
			line = br.readLine().trim();
		} catch (IOException e) {
			throw new WeUPnPException("Error reading search replay", e);
		}

		while (line != null && line.trim().length() > 0) {

			if (line.startsWith("HTTP/1.")) {

			} else {
				String key = line.substring(0, line.indexOf(':'));
				String value = line.length() > key.length() + 1 ? line
						.substring(key.length() + 1) : null;

				key = key.trim();
				if (value != null)
					value = value.trim();

				if (key.compareToIgnoreCase("location") == 0)
					device.setLocation(value);
				else if (key.compareToIgnoreCase("st") == 0)
					device.setSt(value);
			}
			try {
				line = br.readLine().trim();
			} catch (IOException ex) {
			}
		}

		return device;
	}

	public GatewayDevice getValidGateway() throws WeUPnPException {

		if (devices.isEmpty()) {
			throw new WeUPnPException("Did not find any gateways");
		}
		for (GatewayDevice device : devices.values()) {
			if (device.isConnected())
				return device;
		}

		return null;
	}
}