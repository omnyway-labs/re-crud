#!/bin/bash

set -xe

TOP=$(dirname $(dirname $0))
cd ${TOP}
CURRENT_VERSION=$(lein print :version)
CURRENT_VERSION_NO_QUOTES=$(echo $CURRENT_VERSION | sed 's/"//g')


cd ${TOP}/example-app
lein update-dependency org.omnyway/re-crud ${CURRENT_VERSION_NO_QUOTES}

lein cljsbuild once min
