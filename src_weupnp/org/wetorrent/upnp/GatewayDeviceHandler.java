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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author casta
 */
public class GatewayDeviceHandler extends DefaultHandler {

	/**
	 * 
	 */
	private static final int STATE_INITIALIZED = 0;

	/**
	 * 
	 */
	private static final int STATE_SERVICE_LIST = 1;

	/**
	 * 
	 */
	private static final int STATE_WAN_COMMON_INTERFACE_CONFIG = 2;

	/**
	 * 
	 */
	private static final int STATE_WAN_IP_CONNECTION = 3;

	private final GatewayDevice device;
	private String currentElement;
	private int level = 0;
	private short state = STATE_INITIALIZED;

	/** Creates a new instance of GatewayDeviceHandler */
	public GatewayDeviceHandler(GatewayDevice device) {
		this.device = device;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentElement = localName;
		level++;
		if (state == STATE_INITIALIZED && "serviceList".equals(currentElement)) {
			state = STATE_SERVICE_LIST;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		currentElement = "";
		level--;
		if (localName.equals("service")) {
			if (device.getServiceTypeCIF() != null
					&& device
							.getServiceTypeCIF()
							.equals(
									"urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1")) {
				state = STATE_WAN_COMMON_INTERFACE_CONFIG;
			}
			if (device.getServiceType() != null
					&& device.getServiceType().equals(
							"urn:schemas-upnp-org:service:WANIPConnection:1")) {
				state = STATE_WAN_IP_CONNECTION;
			}
		}
	}

	public void characters(char[] ch, int start, int length) {
		if (currentElement.equals("URLBase")) {
			device.setURLBase(new String(ch, start, length));
		} else if (state == STATE_INITIALIZED || state == STATE_SERVICE_LIST) {
			if (state == STATE_INITIALIZED) {
				if ("friendlyName".equals(currentElement))
					device.setFriendlyName(new String(ch, start, length));
				else if ("manufacturer".equals(currentElement))
					device.setManufacturer(new String(ch, start, length));
				else if ("modelDescription".equals(currentElement))
					device.setModelDescription(new String(ch, start, length));
				else if ("presentationURL".equals(currentElement))
					device.setPresentationURL(new String(ch, start, length));
			}
			if (currentElement.equals("serviceType"))
				device.setServiceTypeCIF(new String(ch, start, length));
			else if (currentElement.equals("controlURL"))
				device.setControlURLCIF(new String(ch, start, length));
			else if (currentElement.equals("eventSubURL"))
				device.setEventSubURLCIF(new String(ch, start, length));
			else if (currentElement.equals("SCPDURL"))
				device.setSCPDURLCIF(new String(ch, start, length));
			else if (currentElement.equals("deviceType"))
				device.setDeviceTypeCIF(new String(ch, start, length));
		} else if (state == STATE_WAN_COMMON_INTERFACE_CONFIG) {
			if (currentElement.equals("serviceType"))
				device.setServiceType(new String(ch, start, length));
			else if (currentElement.equals("controlURL"))
				device.setControlURL(new String(ch, start, length));
			else if (currentElement.equals("eventSubURL"))
				device.setEventSubURL(new String(ch, start, length));
			else if (currentElement.equals("SCPDURL"))
				device.setSCPDURL(new String(ch, start, length));
			else if (currentElement.equals("deviceType"))
				device.setDeviceType(new String(ch, start, length));
		}
	}

}
