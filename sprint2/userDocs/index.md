---
draft: false
title: "Sprint 2"
---

## Introduction

This sprint builds on top of the work of [sprint 1](https://iss.signedsnow0.it/sprint1/) and aims to integrate the last component of the system: the _cargorobot_.

## Requirement Analysis

The requirement analysis has already been specified in the [sprint 0](https://iss.signedsnow0.it/sprint0/#requirement-analysis), this particular sprint does not require anything else to be specified in this section.

## Problem Analysis

Using the previous [requirement analysis](http://iss.signedsnow0.it/sprint0/#requirement-analysis) as source, we will briefly summarize here the behaviour of the **cargorobot** actor:

1. It moves from the _HOME_ to the _IOPORT_
2. It takes the container from _IOPORT_ to the _slot-5_
3. Waits for the marking to be done
4. Moves the container from the _slot-5_ to the assigned slot
5. It goes back to the _HOME_

As described in the [sprint 0](http://iss.signedsnow0.it/sprint0/#requirement-analysis) we have a few implementations of an already existing _cargorobot_ to choose from:

- [VirtualRobot26](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.25)
- [RobotObj26](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.26)
- [RobotService26](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.27)
- [RobotSmart26](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.30)

|             | VirtualRobot26 | RobotObj26     | RobotService26   | RobotSmart26     |
| ----------- | -------------- | -------------- | ---------------- | ---------------- |
| Language    | Java           | Java           | Qak              | Qak              |
| Protocol    | HTTP/WS        | Java interface | Dispatch/Request | Dispatch/Request |
| Syntax      | cril           | cril           | aril             | aril             |
| Synchronism | Sync/Async     | Sync           | Async            | Async            |
| Pathfinding | None           | None           | None             | A\*              |

The implementation that matches more closely our needs is **RobotSmart26** because:

- We need to move the robot between places that are not always fixed and known beforehand, so a pathfinding service is needed in our use case.
- The system need to stay reactive during the robot's movement, so an asynchronous implementation is needed.
- The robot uses a map system that closely resembles our hold implementation.

The code of _RobotSmart26_ is available on the [repository](https://github.com/anatali/issLab2026/tree/main/robotsmart26), since our project shouldn't modify the code, it will be compiled as is and the rest of the project will interact only by the _qak_ messages:

```qak
Request buildPlan : buildPlan(PX,PY,TX,TY)
Reply buildPlanDone : buildPlanDone( PLAN ) for buildPlan

Request moverobot : moverobot(TARGETX, TARGETY,STEPTIME)
Reply moverobotdone : moverobotok(ARG) for moverobot
Reply moverobotfailed :  moverobotfailed(PLANDONE, PLANTODO) for moverobot

Dispatch noplan : noplan(X)
Dispatch setplanbuildelay : value(V)

Request  doplan : doplan(PLAN, STEPTIME)
Reply doplandone : doplandone(ARG) for doplan
Reply doplanfailed : doplanfailed(PLANTODO) for doplan

Request step : step(TIME)
Reply stepdone : stepdone(V) for step
Reply stepfailed : stepfailed(DURATION, CAUSE) for step

Dispatch move : move(M)
Dispatch setrobotstate : setpos(X,Y,D)

Request setdirection : dir(D)
Reply setdirectiondone : pos(PX,PY) for setdirection

Request tuneAtHome : tuneAtHome(X)
Reply tuneDone : tuneDone(X) for tuneAtHome

Request getrobotstate : getrobotstate(ARG)
Reply robotstate : robotstate(POS,DIR) for getrobotstate
```

---

## Test Plans
By the end of the sprint every component of the system has implemented in its entirety, so the user can fully test each part and see if it meets the reqirements. As in the [sprint 1](http://iss.signedsnow0.it/sprint1/) the tests are end-to-end and no unit tests are required. To see if the system works, a virtual environment containing a simulation of the robot and the hold is available as a web page.

## Deployment
The system will need two more containers:
- The cargorobot subsystem
- A virtual environment to test the system

As said before, because the _cargorobot_'s code should not be touched we decided to distribute the precompiled code in a single _tar_ file. The system will also need a couple files to load the hold map and some initialization parameters
```Dockerfile
FROM eclipse-temurin:17.0.5_8-jre-focal AS builder

ADD ./external/robotsmart26/robotsmart26-1.0.tar /

WORKDIR /robotsmart26-1.0/bin

COPY ./*.pl ./
COPY ./external/robotsmart26/basicrobotParams.json ./
COPY ./*.bin ./
COPY ./external/robotsmart26/tf25map.txt ./

CMD ["bash", "robotsmart26"]
```

The wenv instead will simply be pulled from the docker registry
```yaml
# Other services from sprint 1
# ...
wenv:
    container_name: wenv
    image: docker.io/natbodocker/virtualrobotdisi26:1.0
    ports:
      - 8090:8090 
      - 8091:8091/tcp
      - 8091:8091/udp
    restart: unless-stopped

  robotoutgui25:
    container_name: robotoutgui25
    image: docker.io/natbodocker/robotoutgui25:1.0
    ports:
     - 8085:8085/tcp
    restart: unless-stopped

  robotsmart26:
    container_name: robotsmart26
    build:
      dockerfile: ./docker/Dockerfile.robotsmart26
    ports:
      - "8020:8020/tcp"
      - "8020:8020/udp"
    environment:
      - VIRTUAL_ENV=wenv
      - MQTTBROKER=mosquitto
    depends_on:
      - wenv
    restart: unless-stopped
```