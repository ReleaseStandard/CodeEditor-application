#!/bin/bash
#
# Simple script to start working on some modules
#
#

#base="https://github.com/ReleaseStandard/"
base="file://$(pwd)/../"
if [ "$#" -eq "0" ] ; then
	echo "You must give plugins as arguments here ..."
	exit 1
fi

for plugin in "$@" ; do
	if [ -L "${plugin}" ] ; then
		echo "Alrdy working on $plugin ..."
		continue
	fi
	echo "Working on $plugin"
	url="${base}/${plugin}"
	echo "Getting module from url=${url} ..."
	git submodule add --depth=1 "$url"
	rm -fr "${plugin}"
	ln -rs "../${plugin}" ./
done
