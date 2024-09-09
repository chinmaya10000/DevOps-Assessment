#!/usr/bin/env groovy

export IMAGE_NAME=$1
DOCKER_USER=$2
DOCKER_PWD=$3
echo $DOCKER_PWD | docker login -u $DOCKER_USER --password-stdin
docker-compose up -d
