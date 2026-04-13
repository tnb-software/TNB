#!/bin/sh
TAG="2.14.0"
DOCKER_IMAGE_NAME=quay.io/fuse_qe/mina-sshd

docker rmi -f ${DOCKER_IMAGE_NAME}:${TAG}
docker buildx create --name mybuilder --bootstrap --use
docker buildx build -f Dockerfile -t ${DOCKER_IMAGE_NAME}:${TAG} --platform linux/amd64,linux/ppc64le,linux/s390x,linux/arm64 --builder mybuilder --push .

echo =========================================================================
echo Docker image is ready.
echo =========================================================================
