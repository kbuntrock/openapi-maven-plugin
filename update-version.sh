#!/bin/bash
read -p "Enter the new version number: " version
mvn versions:set -DnewVersion="$version" -DgenerateBackupPoms=false;
cd ./sample/sample-api;
mvn versions:set-property -Dproperty=openapi-plugin-project-version -DnewVersion="$version" -DgenerateBackupPoms=false;
cd ../../src/test/resources-its/com/github/kbuntrock/it;
mvn versions:set-property -Dproperty=openapi-plugin-project-version -DnewVersion="$version" -DgenerateBackupPoms=false;

read -p "All good, press enter to close the window";