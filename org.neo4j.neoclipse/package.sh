#!/bin/bash

version=1.2
basedir=$PWD
cd build/export
builddir=$PWD

for build in `find . -maxdepth 1 -type d | grep -E "^\./[^.]"`
do
	platform=${build##*/}
	cd $build
	cp $basedir/LICENSE.txt $basedir/NOTICE.txt $basedir/README.txt neoclipse/
	rm -f neoclipse/about.html
	name=neoclipse-$version-$platform
	mv neoclipse $name
  if [ "$platform" == "win*" ]
  	then zip -r9l $builddir/$name.zip $name
  	else tar -czf $builddir/$name.tar.gz $name
  fi
  cd ..
done

