#!/bin/bash
#
# This script is responsible from removing a module from CodeEditor-application.
#
#

if [ "$#" -eq "0" ] ; then
	echo "You must give plugins as arguments here ..."
	exit 1
fi

tmp="/tmp/stopwork${RANDOM}${RANDOM}";

function init() {
	finit
	> "$tmp";
}
function finit() {
	echo "Removing $tmp ...";
	rm -fr "$tmp";
}

# Warning without executing this you will get duplicate error on the next build.
function flushlibbuild() {
	echo "Removing libs : app/libs/* ...";
	rm -f app/libs/*;
}
for plugin in "$@" ; do
	if ! [ -L "$plugin" ] ; then
		echo "Not working on $plugin ..."
		continue
	fi
	echo "Stop working on $plugin"
	rm -f "$plugin"
	rm -fr "../.git/modules/CodeEditor-application/modules/$plugin"
	grep -v "${plugin}" .gitmodules > "$tmp";
	mv "$tmp" .gitmodules
done

flushlibbuild
