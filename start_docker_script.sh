#!/bin/bash

DOCKER_BUILDKIT=1 docker build -t gameofthree-service:1.0 .

docker run  -p 8080:8080 gameofthree-service:1.0
