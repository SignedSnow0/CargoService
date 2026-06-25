---
draft: false
title: "Sprint 0"
---

## Introduction

A _Maritime Cargo shipping company_ (from now on, simply **company**) intends to automate the operations of load of **containers** in the ship’s cargo hold (or simply hold). To this end, the company plans to employ a _Differential Drive Robot_ (from now, called **cargorobot**).
The _hold_ is a rectangular, flat are a with an Input/Output port (**IOPort**). The area provides `4 slots` to store the containers and a slot named **slot5**.

![Requirements image](image-requirements.png)

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

The system states the presence of a _hold_ which contains _slots_ and an _IOPort_. Theese objects must me modeled inside the system and will likely be used by the robot to plan its movement across the hold.
We begin by defining a _slot_, it must contain:

- An **id** to distinguish it (1-5)
- A **flag** to specify it is currently occupied

```java
public interface ISlot {
    public int getID();
    public boolean isOccupied();

    public void setOccupied(boolean value);
}
```

The slots are contained in the hold, which also has the _IOPort_ location

```java
public interface IPosition {
    public int getX();
    public int getY();
}

public interface IHold {
    public IPosition getIOPortPosition();
    public IPosition getHomePosition();
    public List<Pair<IPosition, ISlot>> getSlots();
}
```

Since the _hold_ is rectangular in shape, the reprentation of the map can be a matrix.
We choose to make each cell the size of the robot, the position reprents the coordinates of the cell in this matrix. This system can take advantage of the robot's movement system which has a **step** function (talked about below) that moves the robot forward by one unit.

The special _slot5_ is distinguished by its ID which is always `5`.

The *cargorobot*'s initial position is defined by the **HOME**.

The current requirements do not specify any data about the _container_, the only information needed is to know if it currently occupied a slot, which can be obtained by the method `isOccupied()`, so the current system avoids modeling it.

---

Since the system will be deployed on multiple nodes that are eterogeneous (Raspberry Pico W, a physical robot and a master node) a distributed model based on services is required.
Using a general purpose progamming language (such as Java or C#) for this specific use case would force us to writes lots of lines of code and also would be very technology dependent on the chosen communication protocol.
For this reasons, our software house has already developed a DSL for this specific purpose: **[qak](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.17)**.
The language is very concise and also supports multiple communication protocols (that will likely be used by the system, like TCP, WebSocket, MQTT) all in a transparent way for the programmer. This makes _qak_ the best choice for this project wherever a service is needed.

---

The requirements present a message called **request to load** which is used to start the load process.
Since we want to model the service as a collection of microservices we will the `qak` language. The message expects an aswer so it will be modeled as a _request_

```qak
Request loadRequest : loadRequest(X)

Reply retryLater : retryLater(RetryMessage) for loadRequest
Reply rejected : rejected(RejectedMessage) for loadRequest
Reply accepted : accepted(SlotID) for loadRequest
```
The requirements specify that an LED (hardware component which is capable of emitting light) must blink while the system is *engaged*.
The company told us that the chosen LED is going to be the one attached to the Raspberry Pico board. 

---

The system requires us to model the **cargorobot**, fortunately the software house has previously built an interface to use a DDR robot using _qak_, either **[VirtualRobot26](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.25)** or **[RobotService26](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.27)**.

On top of the existing code another actor is needed to manage the states of the system:

1. After a _load request_ is acceptes it must wait for up to `30` second for a containter to be placed in the _IOPort_
2. It must move the _robot_ from the _IOPort_ to the _slot5_
3. It must wait for the robot to mark the container and then it must move it to the empty _slot_

```qak
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
The **IOPort** is the component that is used by the client to interact with the system.
The company told us that the **pushbutton** for the _IOPort_ and the **display** must be a web page that will be used by various users to control the system and watch its status.
The page will then need:

- A button that will act as the _pushbutton_ to send the _request to load_
- At least some string that show the response and the state of the hold
```html
<!DOCTYPE html>
<head>
    <title>IOPort Display</title>
</head>
<body>
    <button>Request to load</button><br>
    Response: <span id="request-result">accepted at hold 1</span><br>
    Current status: <span id="current-status">service working</span>
</body>
</html>
```
---
## Problem Analysis

## Test Plans
Since at the current status the system doesn't have much code, the only component that can be tested is the *hold*, in particular:
1. If the hold is empty a *request to load* should give an *accepted* response.
2. If the hold is full a *request to load* should give a *rejected* response.
```java
public class HoldTest {
    public void TestEmptyHold() {
        var hold = new Hold();

        assertFalse(hold.getSlots()[0].getValue().isOccupied());
        assertFalse(hold.getSlots()[1].getValue().isOccupied());
        assertFalse(hold.getSlots()[2].getValue().isOccupied());
        assertFalse(hold.getSlots()[3].getValue().isOccupied());
    }

    public void TestFullHold() {
        var hold = new Hold();
        for (int i = 0; i < 4; i++) {
            hold.getSlots()[i].getValue().setOccupied(true);
        }

        assertTrue(hold.getSlots()[0].getValue().isOccupied());
        assertTrue(hold.getSlots()[1].getValue().isOccupied());
        assertTrue(hold.getSlots()[2].getValue().isOccupied());
        assertTrue(hold.getSlots()[3].getValue().isOccupied());
    }
}
```

## Project

## Testing

## Deployment

## Maintenance