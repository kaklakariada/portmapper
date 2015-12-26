/**
 * UPnP PortMapper - A tool for managing port forwardings via UPnP
 * Copyright (C) 2015 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.chris.portmapper;

import org.chris.portmapper.model.Protocol;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import static java.util.Arrays.*;

public class CommandLineArguments {

    @Option(name = "-h", aliases = "-help", usage = "Print usage help", help = true)
    private boolean printHelp;

    @Option(name = "-gui", usage = "Start graphical user interface (default)", forbids = { "-add", "-delete", "-info",
            "-list" })
    private boolean startGui;

    @Option(name = "-add", usage = "Add a new port mapping", depends = { "-internalPort", "-externalPort",
            "-protocol" }, forbids = { "-gui", "-delete", "-info", "-list" })
    private boolean addPortMapping;
    @Option(name = "-delete", usage = "Delete a new port mapping", depends = { "-externalPort",
            "-protocol" }, forbids = { "-gui", "-add", "-info", "-list" })
    private boolean deletePortMapping;
    @Option(name = "-info", usage = "Print router info", forbids = { "-gui", "-delete", "-add", "-list" })
    private boolean printInfo;
    @Option(name = "-list", usage = "Print existing port mappings", forbids = { "-gui", "-delete", "-info", "-add" })
    private boolean listPortMappings;

    @Option(name = "-ip", usage = "Internal IP of the port mapping (default: localhost)")
    private String internalIp;
    @Option(name = "-internalPort", usage = "Internal port of the port mapping")
    private int internalPort;
    @Option(name = "-externalPort", usage = "External port of the port mapping")
    private int externalPort;
    @Option(name = "-protocol", usage = "Protocol of the port mapping")
    private Protocol protocol;
    @Option(name = "-description", usage = "Description of the port mapping")
    private String description;

    @Option(name = "-lib", usage = "UPnP library to use")
    private String upnpLib;

    @Option(name = "-routerIndex", usage = "Router index if more than one is found (zero-based)")
    private Integer routerIndex;

    private final CmdLineParser parser;

    public CommandLineArguments() {
        parser = new CmdLineParser(this, ParserProperties.defaults().withShowDefaults(true).withUsageWidth(80));
    }

    public boolean parse(final String[] args) {
        try {
            parser.parseArgument(asList(args));
            return true;
        } catch (final CmdLineException e) {
            System.err.println(e.getMessage());
            printHelp();
            return false;
        }
    }

    public void printHelp() {
        parser.printUsage(System.err);
        System.err.println(
                " Example: java -jar PortMapper.jar -add -externalPort 22 -internalPort 22 [-ip <ip-addr>] -description desc");
    }

    public boolean isPrintHelp() {
        return printHelp;
    }

    public boolean isStartGui() {
        return startGui;
    }

    public boolean isAddPortMapping() {
        return addPortMapping;
    }

    public boolean isDeletePortMapping() {
        return deletePortMapping;
    }

    public boolean isPrintInfo() {
        return printInfo;
    }

    public boolean isListPortMappings() {
        return listPortMappings;
    }

    public String getInternalIp() {
        return internalIp;
    }

    public int getInternalPort() {
        return internalPort;
    }

    public int getExternalPort() {
        return externalPort;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getUpnpLib() {
        return upnpLib;
    }

    public Integer getRouterIndex() {
        return routerIndex;
    }

    public String getDescription() {
        return description;
    }
}
