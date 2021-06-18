#!/bin/bash
#
# This script is responsible from building submodules.
# intented to be launched from  the app/ directory

#out=/dev/stdout
out="/tmp/out"
tmp="/tmp/tmp${RANDOM}${RANDOM}";
dest="../app/libs/"
MT=true
function finit() {
	if [ -d "$tmp" ] ; then
		rm -fr "$tmp" ; 
	fi
}
function init() {
	finit
	mkdir -p "$tmp";
}
function compileMod() {
	local mod="$1"
	local op="$(pwd)"

	echo "Running on : $mod ..." > $out
        cd "$mod"
        ./gradlew clean
        ./gradlew assembleDebug
        for p in $(find -name "*.aar") ; do
                mkdir -p "$op/libs/"
                cp "$p" "$op/libs/"
        done
        cd "$op"

}
for mod in "$@" ; do
	if $MT ; then
		compileMod "$mod" &
	else
		compileMod "$mod"
	fi
done
$MT && wait $(jobs -p)
