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

As described in the [sprint 0] (http://iss.signedsnow0.it/sprint0/#requirement-analysis) we have a few implementations of an already existing _cargorobot_ to choose from:

- [VirtualRobot26](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.25)
- [RobotObj26](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.26)
- [RobotService26](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.27)
- [RobotSmart26](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.30)

|             | VirtualRobot26 | RobotObj26      | RobotService26   | RobotSmart26     |
|-------------|----------------|-----------------|------------------|------------------|
| Language    | Java           | Java            | Qak              | Qak              |
| Protocol    | HTTP/WS        | Java interface  | Dispatch/Request | Dispatch/Request |
| Syntax      | cril           | cril            | aril             | aril             |
| Synchronism | Sync/Async     | Sync            | Async            | Async            |
| Pathfinding | None           | None            | None             | A*               |

The implementation that matches more closely our needs is **RobotSmart26** because:
* We need to move the robot between places that are not always fixed and known beforehand, so a pathfinding service is needed in our use case.
* The system need to stay reactive during the robot's movement, so an asynchronous implementation is needed.
* The robot uses a map system that closely resembles our hold implementation.

1. It listens for a **move request** sent by _cargoservice_
2. Assuming the system is engaged, it first moves from the _HOME_ position to the _IOPort_
3. Then it will listen for another **move request** sent by the _cargoservice_
4. It will move itself and the container (put there previously by a human) from _IOPort_ to **slot-5**

---

## Test Plans

## Deployment