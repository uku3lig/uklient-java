#!/bin/bash

if [ "$#" -ne 1 ]; then
  ./gradlew -q --console plain run
else
  ./gradlew -q --console plain run --args="$*"
fi