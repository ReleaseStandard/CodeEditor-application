#!/bin/bash
for d in $(./scripts/allmods.sh) ; do
	if [ -L "$d" ] ; then
		./scripts/stopwork.sh $d
	fi
done
