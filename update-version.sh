#!/bin/bash
read -p "Enter the new version number: " version
mvn versions:set -DnewVersion="$version";
cd ./src/test/resources-its/com/github/kbuntrock/it;
mvn versions:set-property -Dproperty=openapi-plugin-project-version -DnewVersion="$version";

read;