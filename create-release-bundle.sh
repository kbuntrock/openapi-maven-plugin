#!/bin/bash

# Only the creation of the release bundle (based on circleCI "build-tag" configuration)
mkdir release

version=$(mvn help:evaluate -Dexpression=project.version -f ./openapi-maven-plugin/pom.xml -q -DforceStdout)
echo ${version}
nextVersionIsSnapshot=false
if [[ "${version}" == *-SNAPSHOT ]]
then
	echo "${version} IS a snapshot version"
else
	echo "${version} IS NOT a snapshot version"
	nextVersionIsSnapshot=true
fi
echo "Next version is a snapshot? $nextVersionIsSnapshot"
# Same regex, but with a different capture group
majorNumber=$(echo ${version} | sed -r 's/^([0-9]+)\.([0-9]+)\.([0-9]+).*$/\1/')
minorNumber=$(echo ${version} | sed -r 's/^([0-9]+)\.([0-9]+)\.([0-9]+).*$/\2/')
patchNumber=$(echo ${version} | sed -r 's/^([0-9]+)\.([0-9]+)\.([0-9]+).*$/\3/')
echo "Major version number is $majorNumber"
echo "Minor version number is $minorNumber"
echo "Patch version number is $patchNumber"

#mvn -f ./openapi-maven-plugin/pom.xml clean install
releasePath=release/io/github/kbuntrock/openapi-maven-plugin/${version}
mkdir -p ${releasePath}

cp openapi-maven-plugin/target/openapi-maven-plugin-${version}.jar ${releasePath}
cp openapi-maven-plugin/pom.xml ${releasePath}/openapi-maven-plugin-${version}.pom
cd ${releasePath}

md5sum openapi-maven-plugin-${version}.pom | cut -f 1 -d " " > openapi-maven-plugin-${version}.pom.md5
sha1sum openapi-maven-plugin-${version}.pom | cut -f 1 -d " " > openapi-maven-plugin-${version}.pom.sha1
md5sum openapi-maven-plugin-${version}.jar | cut -f 1 -d " " > openapi-maven-plugin-${version}.jar.md5
sha1sum openapi-maven-plugin-${version}.jar | cut -f 1 -d " " > openapi-maven-plugin-${version}.jar.sha1

gpg -ab openapi-maven-plugin-${version}.jar
gpg -ab openapi-maven-plugin-${version}.pom

cd ../../../../..
jar -cvfM release-bundle-openapi-${version}.jar .

pwd
rm -rf io

ls -l

read -p "Stop" unasignedValue
