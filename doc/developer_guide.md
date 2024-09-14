
# Developer Guide

## Using PortMapper as a library

PortMapper is available as a Maven dependency at [Maven Central](https://repo1.maven.org/maven2/com/github/kaklakariada/portmapper/). Use the following coordinates:

* Gradle: `com.github.kaklakariada:portmapper:2.2.4`
* Maven:

  ```xml
  <dependency>
    <groupId>com.github.kaklakariada</groupId>
    <artifactId>portmapper</artifactId>
    <version>2.2.4</version>
  </dependency>
  ```

**Important:** The version published to Maven Central is intended to be used as a library in other programs, not as a standalone program. If you don't know what this means you probably want to [download UPnP PortMapper from SourceForge](https://sourceforge.net/projects/upnp-portmapper/files/latest/download).

## Building PortMapper

Build PortMapper on the command line:

```sh
git clone https://github.com/kaklakariada/portmapper.git
cd portmapper
./gradlew build
java -jar build/libs/portmapper-*.jar
```

## Generate license header for added files

```sh
./gradlew licenseFormat
```

## Check if dependencies are up-to-date

```sh
./gradlew dependencyUpdates
```

### Creating a Release

#### Preparations

1. Checkout the `main` branch, create a new branch.
2. Update version number in `build.gradle` and `README.md`.
3. Add changes in new version to `CHANGELOG.md`.
4. Commit and push changes.
5. Create a new pull request, have it reviewed and merged to `main`.

#### Perform the Release

1. Start the release workflow
  * Run command `gh workflow run release.yml --repo kaklakariada/portmapper --ref main`
  * or go to [GitHub Actions](https://github.com/kaklakariada/portmapper/actions/workflows/release.yml) and start the `release.yml` workflow on branch `main`.
2. Update title and description of the newly created [GitHub release](https://github.com/kaklakariada/portmapper/releases).
6. After some time the release will be available at [Maven Central](https://repo1.maven.org/maven2/com/github/kaklakariada/portmapper/).
