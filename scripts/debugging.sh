#!/bin/bash


tmp="/tmp/tmp${RANDOM}${RANDOM}"
Logger=CodeEditor-logger-debug/logger-debug/src/main/java/io/github/rosemoe/editor/core/util/Logger.java

function rpl() {
	sed "s/$1/$2/" $Logger > $tmp
	mv $tmp $Logger
}
function display_help() {
	echo "Usage: "
	echo " enable"
	echo " disable"
}
cmd="$1"
shift


if [ "$cmd" = "enable" ] ; then
	rpl "DEBUG = false" "DEBUG = true"
elif [ "$cmd" = "disable" ] ; then
        rpl "DEBUG = true" "DEBUG = false"
else
	display_help
fi
