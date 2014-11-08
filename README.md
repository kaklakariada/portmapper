UPnP PortMapper
===============

UPnP PortMapper is an easy to use program that manages the port mappings (port forwarding) of a UPnP enabled internet gateway device (router) in the local network. You can easily view, add and remove port mappings.

Using port forwarding, it is possible to access servers (SSH, Web, Game servers, ...) running in a private network from the internet. Port mappings can be configured using the web administration interface of a router, but using the UPnP PortMapper is much more convenient: Just create a new preset or select an existing preset and click one button to add a port mapping for your current computer, the IP address is retrieved automatically!

The interface is written in English and German. PortMapper automatically selects the language according your operating system.

Download
--------

[Download](http://sourceforge.net/projects/upnp-portmapper/files/latest/download) binaries from [SourceForge](http://sourceforge.net/projects/upnp-portmapper/).
UPnP PortMapper requires Java 7 or later. You can download it at [java.com](http://java.com).

Usage
-----

To run PortMapper, double click on the JAR file or run

	$ java -jar PortMapper-1.9.6.jar

on the command line.

PortMapper also has a command line interface. You can see the available options by adding parameter -h:

    $ java -jar PortMapper-1.9.6.jar -h
    usage: java -jar PortMapper.jar [-a <ip port external_port protocol> | -d
       <external_port protocol [...]> | -g | -h | -l | -r <port protocol [...]>
       | -s]    [-i <index>]    [-u <class name>]
    -a <ip port external_port protocol>   Add port forwarding
    -d <external_port protocol [...]>     Delete port forwarding
    -g                                    Start graphical user interface (default)
    -h                                    print this message
    -i <index>                            Router index (if more than one is found)
    -l                                    List forwardings
    -r <port protocol [...]>              Add all forwardings to the current host
    -s                                    Get Connection status
    -u <class name>                       UPnP library
	Protocol is UDP or TCP
	UPnP library class names:
	- org.chris.portmapper.router.sbbi.SBBIRouterFactory (default)
	- org.chris.portmapper.router.weupnp.WeUPnPRouterFactory
	- org.chris.portmapper.router.dummy.DummyRouterFactory


Known issues
------------

* Under Ubuntu Linux it is not possible to retrieve the IP address of the local host, the address must be entered manually.

Building
========

Build PortMapper on the command line:

    $ git clone https://github.com/kaklakariada/portmapper.git
    $ cd portmapper
    $ ./gradlew build
    $ java -jar build/libs/portmapper-1.9.6.jar

Developing using Eclipse
------------------------

Generate Eclipse project files:

    $ ./gradlew eclipse

Then import the project into your Eclipse workspace.

Participate
===========

Your feedback is most welcome at the project page:

- Found a bug? Post a [Bug report](http://sourceforge.net/p/upnp-portmapper/bugs/)!
- Miss some important feature? Post a [Feature request](http://sourceforge.net/p/upnp-portmapper/feature-requests/)!
- Need help using the UPnP PortMapper? Post a message in the Forum!
- Want to help with developing? Contact [me](http://sourceforge.net/u/christoph/profile/) via [SourceForge.net](http://sourceforge.net/u/christoph/profile/send_message)!
- Want to send me a mail? Use `christoph at users.sourceforge.net`!
