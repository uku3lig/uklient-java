#!/bin/bash

if [ "$#" -ne 1 ]; then
  ./gradlew --console plain run
else
  ./gradlew --console plain run --args="$*"
fi