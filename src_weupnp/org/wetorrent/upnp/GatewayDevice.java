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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 * @author casta
 */
public class GatewayDevice {

	private String st;
	private String location;

	private String serviceType;
	private String serviceTypeCIF;

	private String uRLBase;

	private String controlURL;
	private String controlURLCIF;
	private String eventSubURL;
	private String eventSubURLCIF;
	private String sCPDURL;
	private String sCPDURLCIF;
	private String deviceType;
	private String deviceTypeCIF;

	// description data
	private String friendlyName;
	private String manufacturer;
	private String modelDescription;
	private String presentationURL;

	/**
	 * The address used to reach this machine from the GatewayDevice
	 */
	private InetAddress localAddress;

	public InetAddress getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(InetAddress localAddress) {
		this.localAddress = localAddress;
	}

	/**
	 * Creates a new instance of GatewayDevice
	 */
	public GatewayDevice() {
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	private String copyOrCatUrl(String dst, String src) {
		if (src == null) {
			return dst;
		}

		if (src.startsWith("http://")) {
			return src;
		} else {
			if (!src.startsWith("/")) {
				dst += "/";
			}
			dst += src;
		}

		return dst;
	}

	public void loadDescription() throws WeUPnPException {

		URLConnection urlConn;
		try {
			urlConn = new URL(getLocation()).openConnection();

			XMLReader parser;
			parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(new GatewayDeviceHandler(this));
			parser.parse(new InputSource(urlConn.getInputStream()));

			/* fix urls */
			String ipConDescURL;
			if (uRLBase != null && uRLBase.trim().length() > 0)
				ipConDescURL = uRLBase;
			else
				ipConDescURL = location;

			int lastSlashIndex = ipConDescURL.indexOf('/', 7);
			if (lastSlashIndex > 0)
				ipConDescURL = ipConDescURL.substring(0, lastSlashIndex);

			sCPDURL = copyOrCatUrl(ipConDescURL, sCPDURL);
			controlURL = copyOrCatUrl(ipConDescURL, controlURL);
			controlURLCIF = copyOrCatUrl(ipConDescURL, controlURLCIF);
		} catch (MalformedURLException e) {
			throw new WeUPnPException("Could not load description", e);
		} catch (IOException e) {
			throw new WeUPnPException("Could not load description", e);
		} catch (SAXException e) {
			throw new WeUPnPException("Could not load description", e);
		}
	}

	public boolean isConnected() throws WeUPnPException {
		Map<String, String> nameValue = simpleUPnPcommand(controlURL,
				serviceType, "GetStatusInfo", null);
		String connectionStatus = nameValue.get("NewConnectionStatus");
		return connectionStatus != null
				&& connectionStatus.equalsIgnoreCase("Connected");
	}

	public Map<String, String> simpleUPnPcommand(String url, String service,
			String action, Map<String, String> args) throws WeUPnPException {
		StringBuffer soapBody = new StringBuffer();

		soapBody
				.append("<?xml version=\"1.0\"?>\r\n"
						+ "<SOAP-ENV:Envelope "
						+ "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "
						+ "SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
						+ "<SOAP-ENV:Body>" + "<m:" + action + " xmlns:m=\""
						+ service + "\">");

		if (args != null && args.size() > 0) {
			for (String key : args.keySet()) {
				soapBody.append("<" + key + ">" + args.get(key) + "</" + key
						+ ">");
			}

		}

		soapBody.append("</m:" + action + ">");
		soapBody.append("</SOAP-ENV:Body></SOAP-ENV:Envelope>");

		HttpURLConnection conn = null;
		try {
			URL postUrl = new URL(url);
			conn = (HttpURLConnection) postUrl.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "text/xml");

			String soapAction = service + "#" + action;
			conn.setRequestProperty("SOAPAction", soapAction);
			conn.setRequestProperty("Connection", "Close");

			byte[] soapBodyBytes = soapBody.toString().getBytes();

			conn.setRequestProperty("Content-Length", Integer
					.toString(soapBodyBytes.length));

			conn.getOutputStream().write(soapBodyBytes);

			XMLReader parser = XMLReaderFactory.createXMLReader();
			NameValueHandler nameValueHandler = new NameValueHandler();
			parser.setContentHandler(nameValueHandler);

			if (conn.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				parser.parse(new InputSource(conn.getErrorStream()));
			} else {
				parser.parse(new InputSource(conn.getInputStream()));
			}
			return nameValueHandler.getNameValue();
		} catch (MalformedURLException e) {
			throw new WeUPnPException("Could not send simple upnp command", e);
		} catch (SAXException e) {
			throw new WeUPnPException("Could not send simple upnp command", e);
		} catch (IOException e) {
			throw new WeUPnPException("Could not send simple upnp command", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

	}

	public String getExternalIPAddress() throws WeUPnPException {
		Map<String, String> nameValue = simpleUPnPcommand(controlURL,
				serviceType, "GetExternalIPAddress", null);
		return nameValue.get("NewExternalIPAddress");
	}

	public boolean addPortMapping(int externalPort, int internalPort,
			String internalClient, String protocol, String description)
			throws WeUPnPException {
		Map<String, String> args = new HashMap<String, String>();
		args.put("NewRemoteHost", "");
		args.put("NewExternalPort", Integer.toString(externalPort));
		args.put("NewProtocol", protocol);
		args.put("NewInternalPort", Integer.toString(internalPort));
		args.put("NewInternalClient", internalClient);
		args.put("NewEnabled", Integer.toString(1));
		args.put("NewPortMappingDescription", description);
		args.put("NewLeaseDuration", Integer.toString(0));

		Map<String, String> nameValue = simpleUPnPcommand(controlURL,
				serviceType, "AddPortMapping", args);

		return nameValue.get("errorCode") == null;
	}

	public PortMappingEntry getSpecificPortMappingEntry(int externalPort,
			String protocol, final PortMappingEntry portMappingEntry)
			throws WeUPnPException {
		portMappingEntry.setExternalPort(externalPort);
		portMappingEntry.setProtocol(protocol);

		Map<String, String> args = new HashMap<String, String>();
		args.put("NewRemoteHost", "");
		args.put("NewExternalPort", Integer.toString(externalPort));
		args.put("NewProtocol", protocol);

		Map<String, String> nameValue = simpleUPnPcommand(controlURL,
				serviceType, "GetSpecificPortMappingEntry", args);
		String internalClient = nameValue.get("NewInternalClient");
		String internalPort = nameValue.get("NewInternalPort");

		if (internalClient == null) {
			throw new WeUPnPException("Did not get internal client");
		}

		portMappingEntry.setInternalClient(internalClient);

		if (internalPort != null) {
			try {
				portMappingEntry
						.setInternalPort(Integer.parseInt(internalPort));
			} catch (NumberFormatException e) {
				throw new WeUPnPException("Got invalid internal port number "
						+ internalPort, e);
			}
		}

		return portMappingEntry;

	}

	public PortMappingEntry getGenericPortMappingEntry(int index)
			throws WeUPnPException {
		Map<String, String> args = new HashMap<String, String>();
		args.put("NewPortMappingIndex", Integer.toString(index));

		Map<String, String> nameValue = simpleUPnPcommand(controlURL,
				serviceType, "GetGenericPortMappingEntry", args);

		PortMappingEntry portMappingEntry = new PortMappingEntry();

		if (nameValue.get("NewExternalPort") != null) {
			portMappingEntry.setExternalPort(parseInt(nameValue
					.get("NewExternalPort")));
		} else {
			portMappingEntry.setExternalPort(-1);
		}

		portMappingEntry.setRemoteHost(nameValue.get("NewRemoteHost"));
		portMappingEntry.setInternalClient(nameValue.get("NewInternalClient"));
		portMappingEntry.setProtocol(nameValue.get("NewProtocol"));

		if (nameValue.get("NewInternalPort") != null) {
			portMappingEntry.setInternalPort(parseInt(nameValue
					.get("NewInternalPort")));
		} else {
			portMappingEntry.setInternalPort(-1);
		}

		portMappingEntry.setEnabled(nameValue.get("NewEnabled"));
		portMappingEntry.setPortMappingDescription(nameValue
				.get("NewPortMappingDescription"));
		// portMappingEntry.set(nameValue.get("NewLeaseDuration"));

		if (nameValue.get("errorCode") != null) {
			throw new WeUPnPException("Got error code '"
					+ nameValue.get("errorCode")
					+ "' when getting port mapping " + index);
		}
		return portMappingEntry;
	}

	private Integer parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			// TODO: log warning
			return null;
		}
	}

	public int getPortMappingNumberOfEntries() throws WeUPnPException {

		Map<String, String> nameValue = simpleUPnPcommand(controlURL,
				serviceType, "GetPortMappingNumberOfEntries", null);

		String portMappingNumberString = nameValue
				.get("NewPortMappingNumberOfEntries");

		try {
			return Integer.parseInt(portMappingNumberString);
		} catch (NumberFormatException e) {
			throw new WeUPnPException("Got invalid port number '"
					+ portMappingNumberString + "'", e);
		}
	}

	public boolean deletePortMapping(int externalPort, String protocol)
			throws WeUPnPException {
		Map<String, String> args = new HashMap<String, String>();
		args.put("NewRemoteHost", "");
		args.put("NewExternalPort", Integer.toString(externalPort));
		args.put("NewProtocol", protocol);
		Map<String, String> nameValue = simpleUPnPcommand(controlURL,
				serviceType, "DeletePortMapping", args);
		// TODO: check, if port mapping was deleted
		// return nameValue.get("errorCode") == null;
		return true;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceTypeCIF() {
		return serviceTypeCIF;
	}

	public void setServiceTypeCIF(String serviceTypeCIF) {
		this.serviceTypeCIF = serviceTypeCIF;
	}

	public String getControlURL() {
		return controlURL;
	}

	public void setControlURL(String controlURL) {
		this.controlURL = controlURL;
	}

	public String getControlURLCIF() {
		return controlURLCIF;
	}

	public void setControlURLCIF(String controlURLCIF) {
		this.controlURLCIF = controlURLCIF;
	}

	public String getEventSubURL() {
		return eventSubURL;
	}

	public void setEventSubURL(String eventSubURL) {
		this.eventSubURL = eventSubURL;
	}

	public String getEventSubURLCIF() {
		return eventSubURLCIF;
	}

	public void setEventSubURLCIF(String eventSubURLCIF) {
		this.eventSubURLCIF = eventSubURLCIF;
	}

	public String getSCPDURL() {
		return sCPDURL;
	}

	public void setSCPDURL(String sCPDURL) {
		this.sCPDURL = sCPDURL;
	}

	public String getSCPDURLCIF() {
		return sCPDURLCIF;
	}

	public void setSCPDURLCIF(String sCPDURLCIF) {
		this.sCPDURLCIF = sCPDURLCIF;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceTypeCIF() {
		return deviceTypeCIF;
	}

	public void setDeviceTypeCIF(String deviceTypeCIF) {
		this.deviceTypeCIF = deviceTypeCIF;
	}

	public String getURLBase() {
		return uRLBase;
	}

	public void setURLBase(String uRLBase) {
		this.uRLBase = uRLBase;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	public String getPresentationURL() {
		return presentationURL;
	}

	public void setPresentationURL(String presentationURL) {
		this.presentationURL = presentationURL;
	}

}
