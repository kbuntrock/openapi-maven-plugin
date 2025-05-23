#!/bin/bash

version=$1
if [ -z "$version" ]
then
	echo -e "Version is missing."
	exit 0
fi

currentVersion=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout -f openapi-maven-plugin/pom.xml)

# Recomppute the version string because of some unseeable characters at the end preventing the last sed command ...

# Same regex, but with a different capture group
majorNumber=$(echo ${currentVersion} | sed -r 's/^([0-9]+)\.([0-9]+)\.([0-9]+).*$/\1/')
minorNumber=$(echo ${currentVersion} | sed -r 's/^([0-9]+)\.([0-9]+)\.([0-9]+).*$/\2/')
patchNumber=$(echo ${currentVersion} | sed -r 's/^([0-9]+)\.([0-9]+)\.([0-9]+).*$/\3/')
snapshotString=""
if [[ "${currentVersion}" == *-SNAPSHOT* ]]
then
	snapshotString="-SNAPSHOT"
fi

currentVersion="$majorNumber.$minorNumber.$patchNumber$snapshotString"
echo "currentVersion reworked is ${currentVersion}!"

# End of the "recompute" part

sed -i -e "s/<version>${currentVersion}<\/version>/<version>${version}<\/version>/g" docs/quick-start.md
sed -i -e "s/<version>${currentVersion}<\/version>/<version>${version}<\/version>/g" docs/fr/quick-start.md

cd ./openapi-maven-plugin;
mvn versions:set -DnewVersion="$version" -DgenerateBackupPoms=false;
cd ../openapi-maven-plugin/src/test/resources-its/io/github/kbuntrock/it;
mvn versions:set-property -Dproperty=openapi-plugin-project-version -DnewVersion="$version" -DgenerateBackupPoms=false;
cd ../../../../../../../../integration-tests/src/test/resources-its/io/github/kbuntrock/it
mvn versions:set-property -Dproperty=openapi-plugin-project-version -DnewVersion="$version" -DgenerateBackupPoms=false;
