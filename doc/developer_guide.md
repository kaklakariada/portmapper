
# Developer Guide

## Using PortMapper as a library

PortMapper is available as a Maven dependency at [Maven Central](https://repo1.maven.org/maven2/com/github/kaklakariada/portmapper/). Use the following coordinates:

* Gradle: `com.github.kaklakariada:portmapper:2.2.3`
* Maven:

  ```xml
  <dependency>
    <groupId>com.github.kaklakariada</groupId>
    <artifactId>portmapper</artifactId>
    <version>2.2.3</version>
  </dependency>
  ```

**Important:** The version published to Maven Central is intended to be used as a library in other programs, not as a standalone program. If you don't know what this means you probably want to [download UPnP PortMapper from SourceForge](https://sourceforge.net/projects/upnp-portmapper/files/latest/download).

## Building PortMapper

Build PortMapper on the command line:

```bash
git clone https://github.com/kaklakariada/portmapper.git
cd portmapper
./gradlew build
java -jar build/libs/portmapper-*.jar
```

## Generate license header for added files

```bash
./gradlew licenseFormat
```

## Check if dependencies are up-to-date

```bash
./gradlew dependencyUpdates
```

## Publish to Maven Central

1. Add the following to your `~/.gradle/gradle.properties`:

    ```properties
    ossrhUsername=<your maven central username>
    ossrhPassword=<your maven central passwort>

    signing.keyId=<gpg key id (last 8 chars)>
    signing.password=<gpg key password>
    signing.secretKeyRingFile=<path to secret keyring file>
    ```

2. Increment version number in `build.gradle` and `README.md`, commit and push.
3. Run the following command:

    ```bash
    ./gradlew clean check build publish closeAndReleaseRepository --info
    ```

4. Create a new [release](https://github.com/kaklakariada/portmapper/releases) on GitHub.
5. After some time the release will be available at [Maven Central](https://repo1.maven.org/maven2/com/github/kaklakariada/portmapper/).
