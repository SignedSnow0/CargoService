---
draft: false
title: "Sprint 2"
---

## Introduction

This sprint builds on top of the work of [sprint 1](https://iss.signedsnow0.it/sprint0/) and aims to build missing components of the system, in particular:
* The *cargorobot* actor
* Physically connecting the sonar module and the LED to the Pico board

## Requirement Analysis
The requirement analysis has already been specified in the [sprint 0](https://iss.signedsnow0.it/sprint0/#requirement-analysis), this particular sprint does not require anything else to be specified in this section.

## Problem Analysis

Using the previous [requirement analysis](http://iss.signedsnow0.it/sprint0/#requirement-analysis) as source, we will briefly summarize here the behaviour of the **cargorobot** actor:

1. It listens for a **move request** sent by _cargoservice_
2. Assuming the system is engaged, it first moves from the _HOME_ position to the _IOPort_
3. Then it will listen for another **move request** sent by the _cargoservice_
4. It will move itself and the container (put there previously by a human) from _IOPort_ to **slot-5**
5. 

---

## Test Plans

The tests for this sprint focus on the interaction of the various components that have been built. At this point we can test the flow an interaction with the user:
* In the initial state, since no hold has been occupied, when the user makes a _request to load_, the system should reply with request accepted and the status of the page should reflect that. Also once the sonar detects a container the system should transition to the _engaged_ state and start to blink the _led_ on the Raspberry.
* We can also test the edge cases that do not set the service to _engaged_, in particular, if the _ioport_ is occupied or all slots are occupied, the display should receive a _retry later_ and _rejected_ message respectively, also various log messages have been added to the system to trace the behaviour.

Since these test do not simply run on a single component, unit tests are not feasible so end to end tests are required, the user can manually interact with the app and see the expected behaviour in the web gui and the physical led. 

Several unit tests regarding the POJOs from the model were [produced](https://github.com/SignedSnow0/CargoService/tree/main/sprint1/src/test). They can be run via gradle via the following command:
```bash
./gradlew test
```
The project's *build.gradle* [file](https://github.com/SignedSnow0/CargoService/blob/main/sprint1/build.gradle) had to be modified to add this functionality.

---

## Deployment
The system will be deployed on [docker](https://www.docker.com/), to keep the components separated different images will be built:
* cargoservice
* ioport

This allows the possibility to deploy the various components on different nodes, giving flexibility on the client.
The _cargoservice_ and _ioport_ will be essentially the same: they will take a _tar_ file of the application and run it on a _jvm_ image, exposing the required ports respectively.
```Dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /cargoservice
ADD ./build/distributions/cargoservice-1.0.tar /cargoservice
WORKDIR /cargoservice/cargoservice-1.0

EXPOSE 5000
CMD ["./bin/cargoservice"]
```

```Dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /ioport
ADD ./build/distributions/ioport-1.0.tar /ioport
WORKDIR /ioport/ioport-1.0

EXPOSE 8080
CMD ["./bin/ioport"]
```

Lastly, to connect the two images in one cohesive system a compose file will be made, this file connects the two services internally via tcp on ports 5000/5001 on a private network, and exposes to the host only port 8080 which is needed for the web interface of the _ioport_.
```yaml
services:
  cargoservice:
    build:
      dockerfile: ./docker/Dockerfile.cargoservice
    image: cargoservice:latest
    container_name: cargoservice
  
  ioport:
    build:
      dockerfile: ./docker/Dockerfile.ioport
    image: ioport:latest
    container_name: ioport
    ports:
      - 8080:8080
    depends_on:
      - cargoservice
```
If the client wishes to build and run the application, only two steps are required
1. Build the tar files by running `gradle distTar` and `gradle ioportDistTar`
2. Build and deploy the docker images by running `docker compose up --build`