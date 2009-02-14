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

public class PortMappingEntry {
    private int internalPort;
    private int externalPort;
    private String remoteHost;
    private String internalClient;
    private String protocol;
    private String enabled;
    
    private String portMappingDescription;
    
    public PortMappingEntry() {
    }

    public int getInternalPort() {
        return internalPort;
    }

    public void setInternalPort(int internalPort) {
        this.internalPort = internalPort;
    }

    public int getExternalPort() {
        return externalPort;
    }

    public void setExternalPort(int externalPort) {
        this.externalPort = externalPort;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getInternalClient() {
        return internalClient;
    }

    public void setInternalClient(String internalClient) {
        this.internalClient = internalClient;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getPortMappingDescription() {
        return portMappingDescription;
    }

    public void setPortMappingDescription(String portMappingDescription) {
        this.portMappingDescription = portMappingDescription;
    }
    
}
