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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class NameValueHandler extends DefaultHandler {

	private final Map<String, String> nameValue;

	public NameValueHandler() {
		this.nameValue = new HashMap<String, String>();
	}

	private String currentElement;

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		currentElement = localName;
	}

	public void characters(char[] ch, int start, int length) {
		nameValue.put(currentElement, new String(ch, start, length).trim());
	}

	public Map<String, String> getNameValue() {
		return nameValue;
	}
}
