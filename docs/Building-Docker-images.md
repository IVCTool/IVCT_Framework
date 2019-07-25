# Building Docker images for IVCT Framework Components

Every IVCT Component has a [Dockerfile](https://docs.docker.com/engine/reference/builder/) that describes how to build a Docker image for that component. After building the IVCT Framework using Gradle as described [here](https://github.com/MSG134/IVCT_Framework/wiki/gradleDoc) the Docker image build can be started as follows from the directory of the Dockerfile:

`docker build --build-arg version=<version> -t <image tag> .`

The Dockerfiles for the IVCT Components utilize the distributions created by the gradle install process. The distribution zip is unzipped to the Docker image, and the entry point of the image is configured to start the component.

The build of the IVCT Framework components is automated by Travis. The container images are pushed to the Docker Hub, IVCT project: https://cloud.docker.com/u/ivct.