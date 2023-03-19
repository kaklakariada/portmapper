# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.2.3] - 2023-03-19

- [PR #113](https://github.com/kaklakariada/portmapper/pull/113)
  - Upgrade dependencies
  - Fixed too small font size in log view on high resolution screens
  - Fixed static code analysis warnings

## [2.2.2] - 2021-03-06

- No changes, move deployment from JCenter to Maven Central.

## [2.2.1] - 2020-07-11

### Fixed

- Improve getting local IP address, thanks to [brunoais](https://github.com/brunoais) for pull request [#54](https://github.com/kaklakariada/portmapper/pull/54)
- Improve ClingRouterFactory: Search for the right device type and return all found devices. This fixes [#43](https://github.com/kaklakariada/portmapper/issues/43) reported by [@elmimmo](https://github.com/elmimmo) and maybe fixes [#41](https://github.com/kaklakariada/portmapper/issues/41) reported by [@TheGamingLab](https://github.com/TheGamingLab).

### Changed

## [2.2.0] - 2019-04-19

### Changed

- Require Java 11 [BREAKING CHANGE] You can download OpenJDK 11 JRE from [AdoptOpenJDK](https://adoptopenjdk.net/releases.html?variant=openjdk11&jvmVariant=hotspot)
- Add Italian translation, thanks to [@ItachiSan](https://github.com/ItachiSan) for pull request [#36](https://github.com/kaklakariada/portmapper/pull/36)
- Add simplified Chinese translation, thanks to [@wwj402](https://github.com/wwj402) for pull request [#33](https://github.com/kaklakariada/portmapper/pull/33)

### Fixed

- Fix crash on startup, thanks to [@Lacedaemon](https://github.com/Lacedaemon) for reporting [#30](https://github.com/kaklakariada/portmapper/issues/30)
- Improve Cling error handling [ee6b8b9](https://github.com/kaklakariada/portmapper/commit/ee6b8b930df3ef0a48702b4a02bbfe2bf9cf7e13)

### Known issues

- Chinese translation is broken due to encoding issues.

## [2.1.1] - 2018-01-28

### Changed

- Requrire Java 1.8 (BREAKING CHANGE)

### Added

### Fixed

