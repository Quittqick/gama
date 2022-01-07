#!/bin/bash
set -e
echo "zip_withjdk"		
COMMIT=$@

REPO="gama-platform/gama"
RELEASE="1.8.2"
thePATH="$GITHUB_WORKSPACE/ummisco.gama.product/target/products/Gama1.7"


cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products


MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE










COMMIT="${COMMIT:0:7}"

BRANCH_NAME=$(echo $GITHUB_REF | cut -d'/' -f 3)
COMMIT=$(echo $GITHUB_SHA | cut -c1-8)
timestamp=$(date '+_%D')

SUFFIX=$timestamp'_'$COMMIT'.zip'
echo $SUFFIX



n=0
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64.zip" 
n=1
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64.zip" 
n=2
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64.zip"  
n=3
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64_withJDK.zip" 
n=4
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64_withJDK.zip"  
n=5
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64_withJDK.zip" 


#git clone --depth=50 --branch=master https://github.com/gama-platform/jdk.git  jdk	



rem111(){
rm "${RELEASEFILES[0]}"
rm "${RELEASEFILES[1]}"
rm "${RELEASEFILES[2]}"


	
cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64
zip -9 -qyr "${RELEASEFILES[0]}" . && echo "compressed ${RELEASEFILES[0]}" || echo "compress fail ${RELEASEFILES[0]}"
cd ../../../../../../../




cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64
zip -9 -qr "${RELEASEFILES[2]}" . && echo "compressed ${RELEASEFILES[2]}" || echo "compress fail ${RELEASEFILES[2]}"
cd ../../../../../../../





cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64
zip -9 -qyr "${RELEASEFILES[1]}" . && echo "compressed ${RELEASEFILES[1]}" || echo "compress fail ${RELEASEFILES[1]}"
cd ../../../../../../../

}













wget -q "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17%2B35/OpenJDK17-jdk_x64_linux_hotspot_17_35.tar.gz" -O "jdk_linux_15.tar.gz"
wget -q "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17%2B35/OpenJDK17-jdk_x64_windows_hotspot_17_35.zip" -O "jdk_win_15.zip"
wget -q "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17%2B35/OpenJDK17-jdk_x64_mac_hotspot_17_35.tar.gz" -O "jdk_osx_15.tar.gz"
mkdir  jdk_linux
mkdir  jdk_win
mkdir  jdk_osx


echo "unzip jdk linux"	
tar -zxf jdk_linux_15.tar.gz -C jdk_linux/
mv jdk_linux/jdk-17* jdk_linux/jdk
echo "unzip jdk osx"	
tar -zxf jdk_osx_15.tar.gz -C jdk_osx/ 
mv jdk_osx/jdk-17* jdk_osx/jdk 
echo "unzip jdk win"	
unzip -q jdk_win_15.zip -d jdk_win
mv jdk_win/jdk-17* jdk_win/jdk 











cp -R jdk_linux/jdk $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64
#cp $GITHUB_WORKSPACE/travis/jdk/linux/64/Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64
echo "-vm" > Gama.ini
echo "./jdk/bin/java" >> Gama.ini
cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/Gama.ini >> Gama.ini
rm $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/Gama.ini
mv Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/Gama.ini
cp $GITHUB_WORKSPACE/travis/jdk/linux/gama-headless.sh $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless




cp -R jdk_win/jdk $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64
#cp $GITHUB_WORKSPACE/travis/jdk/win/64/Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64
echo "-vm" > Gama.ini
echo "./jdk/bin/" >> Gama.ini
cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64/Gama.ini >> Gama.ini
rm $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64/Gama.ini
mv Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64/Gama.ini

cp $GITHUB_WORKSPACE/travis/jdk/win/gama-headless.bat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64/headless







cp -R jdk_osx/jdk $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents
#cp $GITHUB_WORKSPACE/travis/jdk/mac/64/Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse
echo "-vm" > Gama.ini
echo "../jdk/Contents/Home/bin/java" >> Gama.ini
cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse/Gama.ini >> Gama.ini
rm $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse/Gama.ini
mv Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse/Gama.ini

cp $GITHUB_WORKSPACE/travis/jdk/mac/gama-headless.sh $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/headless




	
cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64

zip -9 -qyr "${RELEASEFILES[3]}" . && echo "compressed ${RELEASEFILES[3]}" || echo "compress fail ${RELEASEFILES[3]}"

cd ../../../../../../../
 



cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64

zip -9 -qr "${RELEASEFILES[4]}" . && echo "compressed ${RELEASEFILES[4]}" || echo "compress fail ${RELEASEFILES[4]}"

cd ../../../../../../../





cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64

zip -9 -qyr "${RELEASEFILES[5]}" . && echo "compressed ${RELEASEFILES[5]}" || echo "compress fail ${RELEASEFILES[5]}"

cd ../../../../../../../



echo DONE
