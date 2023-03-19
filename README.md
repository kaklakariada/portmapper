# UPnP PortMapper

[![Java CI](https://github.com/kaklakariada/portmapper/workflows/Java%20CI/badge.svg)](https://github.com/kaklakariada/portmapper/actions?query=workflow%3A%22Java+CI%22)
[![Download UPnP PortMapper](https://img.shields.io/sourceforge/dw/upnp-portmapper.svg)](https://sourceforge.net/projects/upnp-portmapper/files/latest/download)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.kaklakariada/portmapper)](https://search.maven.org/artifact/com.github.kaklakariada/portmapper)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.kaklakariada%3Aportmapper&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.kaklakariada%3Aportmapper)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.kaklakariada%3Aportmapper&metric=coverage)](https://sonarcloud.io/dashboard?id=com.github.kaklakariada%3Aportmapper)

UPnP PortMapper is an easy to use program that manages the port mappings (port forwarding) of a UPnP enabled internet gateway device (router) in the local network. You can easily view, add and remove port mappings.

Using port forwarding, it is possible to access servers (SSH, Web, Game servers, ...) running in a private network from the internet. Port mappings can be configured using the web administration interface of a router, but using the UPnP PortMapper is much more convenient: Just create a new preset or select an existing preset and click one button to add a port mapping for your current computer, the IP address is retrieved automatically!

The interface is written in English and German. PortMapper automatically selects the language according your operating system.

## Changes

See [CHANGELOG.md](CHANGELOG.md) for changes in the new version.

## Getting started

[![Download UPnP PortMapper](https://a.fsdn.com/con/app/sf-download-button)](https://sourceforge.net/projects/upnp-portmapper/files/latest/download)

[Download](http://sourceforge.net/projects/upnp-portmapper/files/latest/download) binaries from [SourceForge](http://sourceforge.net/projects/upnp-portmapper/).

### Install Java 11<a name="install_java"></a>

UPnP PortMapper requires JRE 11 (Java Runtime Environment) or later. I recommend you download OpenJDK 11 JRE from [AdoptOpenJDK](https://adoptopenjdk.net/releases.html?variant=openjdk11&jvmVariant=hotspot).

For Windows please choose the `.msi` installer which will set the environment variable `JAVA_HOME` and add java to the `PATH`.

Verify that the installation was successful by running this command:

```bash
$ java -version
openjdk version "11.0.3" 2019-04-16
OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.3+7)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.3+7, mixed mode)
```

### Using PortMapper with Java 1.8

If you are still using Java 1.8 and can't upgrade to Java 11, you can use [PortMapper version 2.1.1](https://github.com/kaklakariada/portmapper/releases/tag/v2.1.1).

### Running PortMapper

To run PortMapper, double click on the JAR file or run

```bash
$ java -jar portmapper.jar
```

on the command line.

## Troubleshooting

### General

- Update the firmware of your router to the latest version.

### PortMapper fails to start

**Error 1**: When you double click `portmapper.jar`, an error dialog with the following message is displayed:

`A JNI error has occurred, please check your installation and try again`

**Error 2**: When you start PortMapper from the command line using `java -jar portmapper.jar` you get the following exception:

`java.lang.UnsupportedClassVersionError: org/chris/portmapper/PortMapperStarter has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0`

Usually this means your Java version is outdated. Please [install Java 11 or later](#install_java).

Run `java -version` on the command line to check the default version. If this returns something else than Java 11, you can specify the complete path, e.g.:

```bash
"C:\Program Files\AdoptOpenJDK\jdk-11.0.3.7-hotspot\bin\java.exe" -jar portmapper.jar
```

If this does not help: run PortMapper from the command line using the command above and create a [ticket](https://github.com/kaklakariada/portmapper/issues) containing the complete error message and stack trace.

#### java.lang.ClassNotFoundException: /language=en

```
$ java -Duser.language=en -jar portmapper.jar
Error: Could not find or load main class .language=en
Caused by: java.lang.ClassNotFoundException: /language=en
```

This error occurs when using PowerShell to start PortMapper with a system property argument, e.g. `-Duser.language=en`. To fix this, enclose the system property in double quotes, e.g.:

```
$ java "-Duser.language=en" -jar portmapper.jar
```

#### java.lang.NoClassDefFoundError: org/slf4j/LoggerFactory

```
Exception in thread "main" java.lang.NoClassDefFoundError: org/slf4j/LoggerFactory
        at org.chris.portmapper.PortMapperStarter.<clinit>(PortMapperStarter.java:26)
Caused by: java.lang.ClassNotFoundException: org.slf4j.LoggerFactory
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:581)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:178)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:522)
        ... 1 more
```

You probably try to run a `.jar` that does not contain the required dependencies. Please either

* [Download UPnP PortMapper from SourceForge](https://sourceforge.net/projects/upnp-portmapper/files/latest/download)
* When building PortMapper yourself
  * Build and execute PortMapper using a single command `./gradlew run`
  * Build PortMapper using command `./gradlew build` and execute JAR `*-all.jar` that includes all dependencies: `java -jar build/libs/portmapper-$version-all.jar`

### Router not found

- Check if UPnP is activated in your router's settings.
- Use a different UPnP library in the settings. Please note that `DummyRouterFactory` is just for testing.
- Check if a network bridge is active on your computer. Try to deactive it as it may [prevent detection of the router](https://sourceforge.net/p/upnp-portmapper/bugs/75/#54ef).
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

### Port mappings are expiring

Some routers delete port mappings after some time. To avoid this you can use the command line interface of PortMapper to add the port mapping in an infinite loop. Under Windows you can use a `.cmd` script like this:

```cmd
:loop

rem Add port mapping
java -jar portmapper.jar -add -externalPort <port> -internalPort <port> -protocol tcp

rem Wait for 6 hours (6*60*60 seconds)
timeout 21600

goto loop
```

Press `Ctrl+C` to stop the infinite loop.

See [the command line interface section](#command-line-interface) for details about using the command line interface and available options.

### Error when fetching port mappings

Sometimes you get a log message like this:

```
Got error response when fetching port mapping for entry number 0: '(IncomingActionResponseMessage) 500 Internal Server Error'. Stop getting more entries.
```

This error message is expected. UPnP does not allow getting the total number of available port mappings. That's why PortMapper continues fetching the mappings until it receives an error like this one. You can ignore these messages, they don't mean there is a problem.

### Ports are not forwarded<a name="verify_server_running"></a>

If you use a tool to verify that a port forwarding works, please make sure to start the server process. If no server is running on the forwarded port, the tool might show the port as closed. See issue [#88](https://github.com/kaklakariada/portmapper/issues/88).

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

PortMapper is translated to English (`en`) and German (`de`). It automatically detects the operating system's language, using English as default. If you want use a different language, add command line option `-Duser.language=de` to java, e.g.:

```bash
$ java "-Duser.language=de" -jar portmapper.jar
```

### Using a custom directory for configuration files

PortMapper stores its configuration as XML files in a folder. Under Windows this folder is located at `%AppData%\UnknownApplicationVendor\PortMapper\` (e.g. `C:\Users\<username>\AppData\Roaming\UnknownApplicationVendor\PortMapper`). You can change this folder by specifying a command line argument:

```bash
java -Dportmapper.config.dir=C:/path/to/config -jar portmapper.jar
```

Create an empty directory before starting, else PortMapper will fail with an error message.

The configuration files are only used when PortMapper runs in GUI mode. When running in command line mode the configuration files are not used. Instead you must specify all settings as command line arguments.

## Participate

Your feedback is most welcome at the project page:

- Found a bug? Create an [issue](https://github.com/kaklakariada/portmapper/issues)!
- Miss some important feature? Create an [issue](https://github.com/kaklakariada/portmapper/issues)!
- Need help using the UPnP PortMapper? Post a message in the Forum!
- Want to help with developing? Contact [me](http://sourceforge.net/u/christoph/profile/) via [SourceForge.net](http://sourceforge.net/u/christoph/profile/send_message)!
- Want to send me a mail? Use `christoph at users.sourceforge.net`!

## Development

See [developer guide](./doc/developer_guide.md).
