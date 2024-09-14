#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

base_dir="$( cd "$(dirname "$0")/../.." >/dev/null 2>&1 ; pwd -P )"
readonly base_dir
readonly build_dir="$base_dir/build"
readonly release_artifacts_dir="$build_dir/release-artifacts"

cd "$base_dir"
echo "Reading project version from Gradle project at ${base_dir}..."
project_version=$(./gradlew properties --console=plain --quiet | grep "^version:" | awk '{print $2}')
readonly project_version
echo "Read project version '$project_version' from Gradle project"

mkdir -p "$release_artifacts_dir"
cp -v "$build_dir/libs/portmapper-$project_version-all.jar" "$release_artifacts_dir/portmapper-$project_version.jar"
cp -v "$build_dir/libs-checksums/portmapper-$project_version-all.jar.sha512" "$release_artifacts_dir/portmapper-$project_version.jar.sha512"

release_artifacts=$(find "$release_artifacts_dir" -type f)
readonly release_artifacts

readonly title="Release $project_version"
readonly tag="$project_version"
echo "Creating release:"
echo "Git tag : $tag"
echo "Title   : $title"
echo "Artifacts: $release_artifacts"

release_url=$(gh release create --latest --title "$title" --target main "$tag" "$release_artifacts")
readonly release_url
echo "Release URL: $release_url"
