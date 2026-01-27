#!/bin/bash
# Helper script to run tests with Java 17 (required for Mockito compatibility)

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
mvn "$@"
