#!/bin/bash
#
# This is used to get a production version of the code.
#  - remove all Logger.debug calls
#
tmp="/tmp/CodeEditor/"
tmpfile="$tmp/mytemp${RANDOM}"
pwd="$(pwd)"

function intro() {
	echo ""
	echo "$@"
	echo ""
}

function end() {
	echo ""
	echo ""
	echo ""
}
function p() {
	echo "$@";
}
function init() {
	intro "init"
	p "rm ..."
	rm -fr "$tmp"
	rm -f "$tmpfile";
	p "mkdir ...";
	mkdir -p "$tmp"
	cp -r . "$tmp"
	cd "$tmp"
	echo "pwd=$(pwd)";
	find -type d -name "build" -exec rm -fr {} \; 2> /dev/null
	end
}
init

function removeLoggerCalls() {
	find -name "*.java" -exec bash -c "grep -v -E '(Logger.debug|Logger.v)' '{}' > '$tmpfile' && mv '$tmpfile' '{}'" \;
	
}
removeLoggerCalls

function clean() {
	intro "clean"
	rm -f "$tmpfile"
	rm -fr "$tmp"
	cd "$pwd"
	end
}
#clean


