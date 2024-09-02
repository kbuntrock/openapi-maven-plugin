#!/bin/bash
version=$1
if [ -z "$version" ]
then
	echo -e "Version is missing."
	exit 0
fi
cd ./openapi-maven-plugin;
mvn versions:set -DnewVersion="$version" -DgenerateBackupPoms=false;
cd ../../openapi-maven-plugin/src/test/resources-its/io/github/kbuntrock/it;
mvn versions:set-property -Dproperty=openapi-plugin-project-version -DnewVersion="$version" -DgenerateBackupPoms=false;
cd ../../../../../../../../integration-tests/src/test/resources-its/io/github/kbuntrock/it
mvn versions:set-property -Dproperty=openapi-plugin-project-version -DnewVersion="$version" -DgenerateBackupPoms=false;
