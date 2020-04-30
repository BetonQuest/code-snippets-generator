#!/bin/bash

if ! type mvn > /dev/null; then
  echo "Maven is required to run this generator!"
  exit 1
else
  mvn clean compile exec:java
  exit 0
fi