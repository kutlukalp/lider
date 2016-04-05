#!/bin/bash

###
# This script generates Lider distribution (Lider.tar.gz) which is a Karaf container bundled with all required Lider core bundles.
#
# Generated file can be found under lider-distro/target/ directory.
###
LIDER_ROOT_PATH=`readlink -f ..`
echo $LIDER_ROOT_PATH

# Build lider
echo "Building Lider project"
cd $LIDER_ROOT_PATH
mvn clean install -DskipTests

# Generate Lider.tar.gz
echo "Generating Lider distro files"
cd $LIDER_ROOT_PATH/lider-distro/
mvn clean install

#Prepare temp folder
echo "Preparing temp folder"
TMPFOLDER="/tmp/lider-distro"
rm -rf $TMPFOLDER
cp -r lider-a.b-x $TMPFOLDER

#Update Build Number
export VER="1.0."`date +"%Y%m%d%H%M%S"`
#Update Package Build Number
CHANGELOGFILE=$TMPFOLDER/debian/changelog
cat /dev/null > $CHANGELOGFILE
echo "lider ($VER) unstable; urgency=low" >> $CHANGELOGFILE
echo "" >> $CHANGELOGFILE
echo "  * Initial Release." >> $CHANGELOGFILE
echo "" >> $CHANGELOGFILE
echo " -- emrekgn <emre.akkaya@agem.com.tr>  Fri, 14 Mar 2014 16:55:32 +0200" >> $CHANGELOGFILE

echo "Creating bin tree"
mkdir -p $TMPFOLDER/lider

echo "Copying Lider files"
cp -R "$LIDER_ROOT_PATH/lider-distro/target/lider-distro-1.0.0-SNAPSHOT.tar.gz" "$TMPFOLDER/lider/"

cd $TMPFOLDER
echo "Building deb package"
dpkg-buildpackage -d

echo "Created lider deb package"
