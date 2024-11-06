#!/bin/bash
export HAWTIO_REPOS_TAG=hawtio-4.2.0

#the version should be from https://github.com/hawtio/hawtio/blob/hawtio-$HAWTIO_REPOS_TAG/hawtio-jbang/dist/HawtioJBang.java
export HAWTIO_VERSION=4.1.0

docker buildx build --build-arg HAWTIO_REPOS_TAG=$HAWTIO_REPOS_TAG -t quay.io/rh_integration/hawtio-jbang:$HAWTIO_VERSION --platform linux/amd64,linux/arm64,linux/ppc64le,linux/s390x --provenance=false --push .
