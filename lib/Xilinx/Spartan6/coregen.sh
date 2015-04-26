#!/bin/sh

for i in *.xco
do
	coregen -b $i -p .
done
