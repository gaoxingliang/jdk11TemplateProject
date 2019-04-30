#!/usr/bin/env bash
#this script is used for building linux jre base on amazon jdk
echo "using file $1 for building jre"
TMPJREDIR="tmpbuilddir"
SUB_TARGETDIR="generated"
jrename=`basename $1 .tar.gz`
rm -rf $TMPJREDIR
mkdir -p $TMPJREDIR
tar -xvzf $1 -C $TMPJREDIR
echo "using jre $jrename"
cd $TMPJREDIR/$jrename/jmods
for file in ./*; do
    jmod_basename=`basename $file .jmod`
    jmods=$jmod_basename,$jmods
done

rm -rf $TMPJREDIR/$SUB_TARGETDIR
mkdir -p $TMPJREDIR/$SUB_TARGETDIR
echo "$TMPJREDIR/$jrename/bin/jlink --module-path $TMPJREDIR/$jrename/jmods/ --compress=2 --no-header-files --output $TMPJREDIR/$SUB_TARGETDIR  --add-modules $jmods"
cd ../../
echo `pwd`
`$jrename/bin/jlink --module-path $jrename/jmods/ --compress=2 --no-header-files --output $SUB_TARGETDIR  --add-modules $jmods`
cd $SUB_TARGETDIR
tar -cvzf ../../jre_linux_x64.tar.gz .
rm -rf $TMPJREDIR
echo "Done file is jre_linux_x64.tar.gz under current folder"