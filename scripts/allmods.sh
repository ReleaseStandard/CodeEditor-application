#!/bin/bash
find ../ -mindepth 1 -maxdepth 1 -type d \( -name "CodeEditor-*" -and -not -name "CodeEditor-application" -and -not -name "CodeEditor-buildSrc" -and -not -name "CodeEditor-emptyPlugin" \) | sed 's/:$//' |sed 's/.*\/\([^\/]\+\)$/\1/'
