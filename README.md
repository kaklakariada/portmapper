# UPnP PortMapper

[![Build Status](https://travis-ci.org/kaklakariada/portmapper.svg?branch=master)](https://travis-ci.org/kaklakariada/portmapper)
[![Download UPnP PortMapper](https://img.shields.io/sourceforge/dw/upnp-portmapper.svg)](https://sourceforge.net/projects/upnp-portmapper/files/latest/download)

UPnP PortMapper is an easy to use program that manages the port mappings (port forwarding) of a UPnP enabled internet gateway device (router) in the local network. You can easily view, add and remove port mappings.

Using port forwarding, it is possible to access servers (SSH, Web, Game servers, ...) running in a private network from the internet. Port mappings can be configured using the web administration interface of a router, but using the UPnP PortMapper is much more convenient: Just create a new preset or select an existing preset and click one button to add a port mapping for your current computer, the IP address is retrieved automatically!

The interface is written in English and German. PortMapper automatically selects the language according your operating system.

## Download

[![Download UPnP PortMapper](https://a.fsdn.com/con/app/sf-download-button)](https://sourceforge.net/projects/upnp-portmapper/files/latest/download)

[Download](http://sourceforge.net/projects/upnp-portmapper/files/latest/download) binaries from [SourceForge](http://sourceforge.net/projects/upnp-portmapper/).
UPnP PortMapper requires JRE 8 (Java Runtime Environment) or later. We recommend Java 11. You can download JDK 11 from [oracle.com](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html).

## Usage

To run PortMapper, double click on the JAR file or run

```bash
$ java -jar portmapper.jar
```

on the command line.

## Troubleshooting

### General

- Update the firmware of your router to the latest version.

### Router not found

- Check if UPnP is activated in your router's settings.
- Use a different UPnP library in the settings. Please note that `DummyRouterFactory` is just for testing.
- Set Log level to `TRACE` in the settings, connect again and check the log.

#### Manually specify location URL

If you can connect to your router from one device but not from another you can directly specify the location URL.

**Note**: this is only supported for library weupnp (`-lib org.chris.portmapper.router.weupnp.WeUPnPRouterFactory`).

1. Get location URL on the machine that can connect to the router by clicking the `Info` button (look for something like `INFO  - location = http://192.168.178.1:49000/igddesc.xml`)
1. Specify the location URL as a command line argument:

```bash
$ java "-Dportmapper.locationUrl=<locationurl>" -jar portmapper.jar -lib org.chris.portmapper.router.weupnp.WeUPnPRouterFactory <args>
```

### Adding port forwardings not possible

- Check that your router allows write access via UPnP.
- Try to add port forwardings manually via your router's user interface.
- Use a different UPnP library in the settings. Please note that `DummyRouterFactory` is just for testing.

### Multiple routers

If you have multiple routers in your network please use library `org.chris.portmapper.router.weupnp.WeUPnPRouterFactory` or `org.chris.portmapper.router.sbbi.SBBIRouterFactory`. After connecting a dialog will allow you to select one of the found routers. `org.chris.portmapper.router.cling.ClingRouterFactory` currently only supports one router.

### Small font on high resolution displays

If you have a high resolution display and the fonts (e.g. the log) in PortMapper is too small, please upgrade to Java 9 or later. See http://openjdk.java.net/jeps/263 for details.

### Known issues

- Under Ubuntu Linux it is not possible to retrieve the IP address of the local host, the address must be entered manually.

## Command line interface

PortMapper also has a command line interface. You can see the available options by adding parameter `-h`:

```
$ java -jar portmapper.jar -h
 -add                  : Add a new port mapping
 -delete               : Delete a new port mapping
 -description VAL      : Description of the port mapping
 -externalPort N       : External port of the port mapping
 -gui                  : Start graphical user interface
 -h (-help)            : Print usage help
 -info                 : Print router info
 -internalPort N       : Internal port of the port mapping
 -ip VAL               : Internal IP of the port mapping (default: localhost)
 -lib VAL              : UPnP library to use
 -list                 : Print existing port mappings
 -protocol [TCP | UDP] : Protocol of the port mapping
 -routerIndex N        : Router index if more than one is found (zero-based)
```

### Examples

- Create a new port mapping for a specific IP address

```bash
$ java -jar portmapper.jar -add -externalPort <port> -internalPort <port> -ip <ip-addr> -protocol tcp
```

- Create a new port mapping for the local machine (just omit the IP)

```bash
$ java -jar portmapper.jar -add -externalPort <port> -internalPort <port> -protocol tcp
```

- Delete a port forwarding

```bash
$ java -jar portmapper.jar -delete -externalPort <port> -protocol tcp
```

- List existing port forwardings

```bash
$ java -jar portmapper.jar -list
```

- Specify a UPnP library (see below for available libraries)

```bash
$ java -jar portmapper.jar -lib org.chris.portmapper.router.weupnp.WeUPnPRouterFactory -list
```

### UPnP libraries

PortMapper includes three third party UPnP libraries. If the default does not work for your device, try using a different library.

- [Cling](https://github.com/4thline/cling): `org.chris.portmapper.router.cling.ClingRouterFactory` (default)
- [weupnp](https://github.com/bitletorg/weupnp): `org.chris.portmapper.router.weupnp.WeUPnPRouterFactory`
- [SBBI UPnP lib](https://sourceforge.net/projects/upnplibmobile/): `org.chris.portmapper.router.sbbi.SBBIRouterFactory`
- `org.chris.portmapper.router.dummy.DummyRouterFactory` (for testing only)

### Select language

PortMapper is translated to English (`en`) and German (`de`). It automatically detects the operating system's language using English as default. If you want use a different language, add command line option `-Duser.language=de` to java, e.g.:

```bash
$ java -Duser.language=de -jar portmapper.jar
```

## Development

Build PortMapper on the command line:

```bash
$ git clone https://github.com/kaklakariada/portmapper.git
$ cd portmapper
$ ./gradlew build
$ java -jar build/libs/portmapper-*.jar
```

### Generate license header for added files

```bash
$ ./gradlew licenseFormat
```

## Participate

Your feedback is most welcome at the project page:

- Found a bug? Create an [issue](https://github.com/kaklakariada/portmapper/issues)!
- Miss some important feature? Create an [issue](https://github.com/kaklakariada/portmapper/issues)!
- Need help using the UPnP PortMapper? Post a message in the Forum!
- Want to help with developing? Contact [me](http://sourceforge.net/u/christoph/profile/) via [SourceForge.net](http://sourceforge.net/u/christoph/profile/send_message)!
- Want to send me a mail? Use `christoph at users.sourceforge.net`!
