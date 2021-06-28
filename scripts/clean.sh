#!/bin/bash

for d in $(find -name "libs") ; do 
	rm -f $d/*
done
