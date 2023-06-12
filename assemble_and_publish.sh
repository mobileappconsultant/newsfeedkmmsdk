#!/bin/sh
./gradlew assemble || exit
./gradlew publish || exit