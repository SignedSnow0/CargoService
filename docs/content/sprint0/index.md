---
draft: false
title: "Sprint 0"
---

## Introduction

A _Maritime Cargo shipping company_ (from now on, simply **company**) intends to automate the operations of load of **containers** in the ship’s cargo hold (or simply hold). To this end, the company plans to employ a _Differential Drive Robot_ (from now, called **cargorobot**).
The _hold_ is a rectangular, flat are a with an Input/Output port (**IOPort**). The area provides `4 slots` to store the containers and a slot named **slot5**.

![Requirements image](image-requrements.png)

In the picture above:

- The **slots1-4** depict the hold areas reserved to store one _container_ each,
- The **slots5** depicts an area, where the _cargorobot_ must temporarily store a container, before to place it in one of
  the _slots1-4_. During temporary storage, a ‘marker’ device labels the container with an identification barcode and
  signals when this marking activity is completed.
- The **IOPort** is a device with a **pushbutton** and a **display**. The _pushbutton_ is pressed by the customer in order
  to to send a request to load a container on the cargo. The _display_ is used to show the answer to the request and
  to show the current state of the _hold_.
- The **sensor** associated to the _IOPort_ is a device (a _sonar_) used to detect the presence of a container, when it
  measures a distance `D`, such that `D < DFREE/2`, during a reasonable time (e.g. `3` secs).

## Requirements

The company asks us to build service named **cargoservice** that should work as follows.
The _cargoservice_ is able to receive a **request to load** a container sent by some customer by using the _pushbutton_ of the _IOPort_.

- It sends the answer `retrylater`, if the **IOPort** is currently occupied by a container or if the system is _Out of service_
- It rejects the request when the hold is already full, i.e. the `slots1-4` are already occupied.
- Otherwise, it considers the system as _engaged_, detects a free slot and returns as answer the name of such a reserved slot. While engaged, the system must blink a Led.

When the _request to load_ is accepted, the customer must move the container in the _sensor_ area within prefixed amount of time (e.g. 30 secs), otherwise the systems becomes **disengaged**. Then, the _cargoservice_ uses the _cargorobot_ to move the container from the _IOPort_ to the _slot5_ (for marking the container) and then to the reserved slot.
The service must also show on the _display_ on the _IOPort_:

- the current state of the _hold_
- the message **‘Service working’**, when it is all is going well
- the message **‘Out of service’** if the _sonar sensor_ measures a distance `D > DFREE` for at least `3` secs (perhaps a failure of the _sonar_).

## Requirement Analysis

The system states the presence of a *hold* which contains *slots* and an *IOPort*. Theese objects must me modeled inside the system and will likely be used by the robot to plan its movement across the hold.
We begin by defining a *slot*, it must contain:
* An **id** to distinguish it (1-5)
* A **flag** to specify it is currently occupied

```java
public interface ISlot {
    public int getID();
    public boolean isOccupied();
}
```

The slots are contained in the hold, which also has the *IOPort* location
```java
public interface IPosition {
    public int getX();
    public int getY();
}

public interface IHold {
    public IPosition getIOPortPosition();
    public List<Pair<IPosition, ISlot>> getSlots();
}
```
Since the *hold* is rectangular in shape, the reprentation of the map can be a matrix.
Each cell is the size of the robot, the position reprents the coordinates of the cell in this matrix. This system can take advantage of the robot's movement system which has a **step** function that moves the robot forward by one unit.

The special *slot5* is distinguished by its ID which is always `5`.
The requirements do not specify the starting position of the *cargorobot*, and this will likely be an issue when the robot will have to plan a path to reach a slot/port.

`Does the system specify an initial position for the cargorobot (HOME) or does it have a way to know where it is located at every moment?`

The current requirements do not specify any data about the *container*, the only information needed is to know if it currently occupied a slot, which can be obtained by the method `isOccupied()`, so the current system avoids modeling it.

---

The reuirements present a message called **request to load** which is used to start the load process.
Since we want to model the service as a collection of microservices we will the `qak` language. The message expects an aswer so it will be modeled as a *request*
```qak
Request loadRequest : loadRequest(X)

Reply retryLater : retryLater(RetryMessage) for loadRequest
Reply rejected : rejected(RejectedMessage) for loadRequest
Reply accepted : accepted(SlotID) for loadRequest
```
---

The robot is given by the company, we can use it by the given interface which can be found [here at chapter 27](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf). We need to develop a system that can plan a path from the *IOPort* and the slots and vice versa. This system will be an actor with the following behaviours:
1. After a *load request* is acceptes it must wait for up to `30` second for a containter to be placed in the *IOPort*
2. It must move the *robot* from the *IOPort* to the *slot5*
3. It must wait for the robot to mark the container and then it must move it to the empty *slot*

```qak
ExternalQActor robot context robotservice

QActor robotplanner context cargoservice {
    State receiveRequest {
        // reject the request (retryLater or slots full) or go to engaged
    }

    State engaged {
        // wait for the container or go back to receiveRequest
    }

    State moveRobot {

    }

    State markContainer {

    }
}
```
---
The *display* and *sonar* are a series of hardware devices that are installed on a `Raspberry Pi Pico W`, an interface will be needed and to keep it consistent with the rest of the system it will also be a microservice developed in qak.
```qak
Event sonarDistance : sonarDistance(D)
```

```qak
QActor displayService context cargoservice {
    State getHoldState {
    }

    State getStatusMessage {
    }
}
```
The raspberry will likely need a comunication protocol, various solutions exist, for example, TCP, WebSocket, MQTT, etc. 
We still need to discuss with the company if it has any prefrences or we are free to choose the best for our use case.

## Problem Analysis

## Test Plans

## Project

## Testing

## Deployment

## Maintenance
