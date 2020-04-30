#!/bin/bash

if ! type mvn > /dev/null; then
  mvn clean compile exec:java
  exit 0
else
  echo "Maven is required to run this generator!"
  exit 1
fi