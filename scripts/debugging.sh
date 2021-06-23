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
	echo " state"
}
cmd="$1"
shift


if [ "$cmd" = "enable" ] ; then
	rpl "DEBUG = false" "DEBUG = true"
elif [ "$cmd" = "disable" ] ; then
        rpl "DEBUG = true" "DEBUG = false"
elif [ "$cmd" = "state" ] ; then
	grep "DEBUG = false" $Logger 2> /dev/null 1> /dev/null
	if [ "$?" = "0" ] ; then
		echo "disabled"
	else
		echo "enabled"
	fi
else
	display_help
fi
