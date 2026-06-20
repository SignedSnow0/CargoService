---
draft: false
title: "Sprint 0"
---

## Introduction

A Maritime Cargo shipping company (fron now on, simply company) intends to automate the operations of load of freight in the ship's cargo hold (or simply hold). To this end, the company plans to employ a Differential Drive Robot (from now, called cargorobot) for the loading of goods (named products) in the ship's hold. The products to be loaded must be placed in a container of predefined dimensions and registered, by specifying its weight, within a database, by using a proper service (productservice). After the registration, the productservice returns a unique product identifier as a natural number PID, PID>0. The hold is a rectangular, flat area with an Input/Output port (IOPort). The area provides 4 slots for the product containers.

## Requirements

The company asks us to build a software systems (named **cargoservice**) that:

1. Is able to receive the request to load on the cargo a **product** container already registered in the productservice. The request is rejected when:
   - the **product-weight** is evaluated too high, since the ship can carry a **maximum load** of `MaxLoad > 0 kg`.
   - the hold is already full, i.e. the **4 slots** are alrready **occupied**.

   If the request is accepted, the cargoservice associates a slot to the product **PID** and returns the name of the reserved slot. Afterwards, it waits that the product container is delivered to the **ioport**. In the meantime, other requests are not elaborated.

2. Is able todetect (by means of the **sonar** sensor) the presence of the product container at the ioport.
3. Is able to ensure that the product container is placed by the cargorobot within its reserved slot. At the end of the work:
   - the cargorobot should returns to its `HOME` location.
   - the cargoservice can process another load-request.
4. Is able to show the current state of the **hold**, by means of a dynamically updated **web-gui**.
5. Interrupts any activity and turns on a led if the sonar sensor measures a distance `D > DFREE` for at least 3 secs (perhaps a sonar failure). The service continues its activities as soon as the sonar measures a distance `D <= DFREE`.

## Requirement Analysis

To satisfy requirement #1, it is necessary to give a formal definition of product:

```java
public interface IProduct {
    String getName();
    double getWeight();
    int getProductIdentifier();
    boolean isRegistered();
}
```

---

The system requires to define a request to load as a message containing useful information for the decision process, in particular the message should contain:

- the product weight
- the hold capacity
- the PID

since the reqirements specify a response to the request, it is modeled as a qak request:

```qak
Request loadRequest : loadRequest(Weight, Capacity, PID)
Reply loadAccepted : loadAccepted(SlotName) for loadRequest
Reply loadRejected : loadRejected(Reason) for loadRequest
```

From the customer we know that the sonar is a Raspberry Pi pico w placed in front of the IOPort, it measures continuously the distance and is used to detect two possible states:

- D < DFREE / 2 for at least 3 seconds: a container is detected at the IOPort
- D > DFREE for at least 3 seconds: possible sonar error, it resumes activity when it detects a distance of less than DFREE

---

To satisfy requirement #3, the robot should be able to move between the home, ioport and the reserved slots. In order to do that a representation of the hold has to be defined, in particular the representation should be a matrix of squares 1 robot unit wide, where 1 robot unit is a square the lenght of the robot. The hold should contain all slots and the home and IOPort.

```java
public interface IPosition {
    int getRow();
    int getColumn();
}

public interface ISlot {
    String getName();
    boolean isOccupied();
}

public interface IHold {
    IPosition getHome();
    IPosition getIoPort();
    List getSlots();
}
```

---

The web gui will have to display various information (to be discussed with the customer), but in general to display such information the system will need a service to poll the data for the gui.

```qak
QActor guiservice context cargoservice {
    State s0 initial {

    }
    Transition t0
        whenMsg getX -> handleGetX
}
```

---

To interrupt the system, the sonar will emit a particular message, since no response is needed and the receivers are not known it is modeled as an event:

```qak
Event sonarDistance : sonarDistance(D)
```

---

The robot will likely need an actor to work as an adapter, much in the same way as the sonar:

```qak
QActor robot context cargoservice {
    State processLoad {
    }

    State goHome {
    }
}
```

## Problem Analysis

## Test Plans

## Project

## Testing

## Deployment

## Maintenance
