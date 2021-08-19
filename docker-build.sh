#!/bin/bash

./mvnw clean package || exit 1

mkdir -p target/dependency
pushd target/dependency
jar -xf ../*.jar
popd

docker build -t metrics-manager .
