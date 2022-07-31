read -p "Enter the new version number: " version
exec "./update-version.sh" $version 
read -p "All good, press enter to close the window";