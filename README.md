UPnP PortMapper
===============

A tool for managing port forwardings via UPnP.

Download binaries at SourceForge: http://sourceforge.net/projects/upnp-portmapper/


Build Instructions
==================

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

