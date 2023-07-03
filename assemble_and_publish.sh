#!/bin/sh
./gradlew assemble || exit
./gradlew publish || exit

#./deploy_ios.sh