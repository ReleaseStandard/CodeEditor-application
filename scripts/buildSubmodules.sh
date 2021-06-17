#!/bin/bash
#
# This script is responsible from building submodules.
# intented to be launched from  the app/ directory

#out=/dev/stdout
out="/tmp/out"
tmp="/tmp/tmp${RANDOM}${RANDOM}";
dest="../app/libs/"

function finit() {
	if [ -d "$tmp" ] ; then
		rm -fr "$tmp" ; 
	fi
}
function init() {
	finit
	mkdir -p "$tmp";
}
for mod in "$@" ; do
	echo "Running on : $mod ..." > $out
	op="$(pwd)"
	cd "$mod"
	./gradlew assembleDebug
	for p in $(find -name "*.aar") ; do
		mkdir -p "$op/libs/"
		cp "$p" "$op/libs/"
	done
	cd "$op" 
done
