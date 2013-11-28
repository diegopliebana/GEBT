#!/bin/bash
find -name "*.java" > sources.txt
rm -rf out/
mkdir out/
mkdir out/production/
javac -d out/production/ -cp "lib/jdom.jar:.:src/" @sources.txt
