#!/bin/bash

DOCKER_BUILDKIT=1 docker build -t gameofthree-service:1.0 .

docker run -it gameofthree-service:1.0 /bin/bash
