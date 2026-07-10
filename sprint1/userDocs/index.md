---
draft: false
title: "Sprint 1"
---

## Introduction

This sprint builds on top of the work of [sprint 0](https://iss.signedsnow0.it/sprint0/) and aims to build the core buisness of the system, in particular:
* The main *cargoservice* actor
* The *sonar* subsystem
* The *IOPort* and *pushbutton* interfaces
* Concrete implementations of the following interfaces: *ISlot*, *IHold*, *IPosition* 

## Requirements


## Requirement Analysis
The requirement analysis has already been specified in the [sprint 0](https://iss.signedsnow0.it/sprint0/#requirement-analysis), this particular sprint does not require anything else to be specified in this section.

## Problem Analysis

The **cargoservice** actor works as the main component of the system, the requirement analysis specifies the workflow of the component, here we will specify it again in order to focus on many details with more precision:

1. It listens for a **request to load** received by the _pushbutton_
2. It checks the state of the **IOPort**, and decides which answer to send back, as defined in the [sprint 0](http://iss.signedsnow0.it/sprint0/#requirement-analysis)
3. If a slot is free, it waits for the container to be placed in the _sensor area_ and then delegates the _cargorobot_ service to move the container to **slot-5**
4. It then moves the container from **slot-5** to the reserved slot again with the use of the **cargorobot**

```qak
QActor cargoservice context ctxcargoservice {
    [# val TimeoutMillis = 30000 #]
    [# val ReservedSlot = 0 #]

    State s0 disengaged {
        // Wait for a request to load

        if [# ioport occupied #] {
            //send retrylater
        } else if [# slots occupied #] {
            //send rejected
        } else {
            //reserve slot
            //set reserved slot var
            //send accepted(slot id)
            Goto engaged
        }

    }

    State engaged {
        //call blink led service
    }
    Transition t0
        whenTime TimeoutMillis -> disengaged
        whenMessage IOPortDeposited -> moveRobot

    State moveRobot {
        // call robot service from IOPort to slot 5
        // wait 3s to mark the container
        // call robot service from slot 5 to slot ReservedSlot

        //stop blink led service
        Goto disengaged
    }
}
```

---

The **sonar** is a [HC-SR04](https://www.handsontec.com/dataspecs/HC-SR04-Ultrasonic.pdf#[{%22num%22%3A21%2C%22gen%22%3A0}%2C{%22name%22%3A%22XYZ%22}%2C34%2C799%2C0]) and is connected to a _Raspberry Pi Pico W_, by nature the hardware has a few characteristics:

- The raspberry is too lightweight to support a full JVM needed to run _qak_ directly, so we have to use either cpp or micropython as the language
- Since _qak_ is not a possibility the comunication must be implemented with a protocol, preferably one that _qak_ suports out of the box
- _qak_ supports a plethora of protocols, TCP, CoAP, MQTT, ecc..

The software house has decided to use **MQTT** as the protocol of choice because:
- It uses a centralized broker/client system, allowing the system to easily change or add devices in the future (multiple IOPorts or a device substituion), otherwise at least one of the nodes should know how to reach the other one, which is not always granted
- The broker is not particulary resource intensive and can easily be deployed along the rest of the service, likely in the same node as the main *cargoservice* actor
- The topic system keeps messages from various nodes neatly separated

```python
def connectWifi():
    #connect to wifi

    print("Connected:", wlan.ifconfig())

def connectMqtt():
    # create Mqtt client
    # connect to Mqtt broker
    return client

def measureDistance():
    # measure distance using sonar
    return distance

TRIG = Pin(3, Pin.OUT)
ECHO = Pin(2, Pin.IN)

connectWifi()
client = connectMqtt()

while True:
    try:
        d = measureDistance()
        if d is not None:
            # create formatted msg
            client.publish(TOPIC, msg.encode() )

        time.sleep(1)
    except Exception as e:
        print("Exception: ", type(e).__name__, e)
```

- We have assumed that the Board will be connected through Wifi
- The configurations parameters (Wifi SSID, Password, MQTT broker, topic, ect..) will be set in a _.env_ file in order to enhance reusability.
- The _msg_ will be formatted as specified by the documentation of [unibo.basicomm23-1.0](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf#chapter.9), a library developed by our software house, already used by _qak_

Once we have the raw distance data we have two options:
* Send to the rest of the system the distance, it will then be a responsability of each component to interpret the data accordingly as defined in the requirements.
* Create an intermediary that transforms the raw data into messages that respect the semantics of the requirements that will be sent to the rest of the system.

We decided to create an intermediary wrapper in order to have a single point where the distance is parsed, this allows to avoid repeating code, making it more robust and easily adjustable in the future.


```qak
Event serviceWorking : serviceWorking(X)
Event outOfService : outOfService(X)

Dispatch containerInIOPort : containerInIoPort(X)

QActor sonarwrapper context ctxcargoservice {
    State s0 initial {
        // listen to the sonar messages and send the right events/dispatch
    }
}
```

We choose to make the display messages events to permit a future expansion through extensibility. The containerInIOPort, instead is a dispatch because the only receiver should be the _cargoservice_ actor.

---

Since the **IOPort** has to mantain the current status of the system at any moment the web page will need a **WebSocket** connection to ensure it receives updates, also receive the state of the _hold_ our implementation in the [requirement analysis](http://iss.signedsnow0.it/sprint0/#requirement-analysis) will also need a listener that will forward any change via WebSocket to all connected clients.

```js
const socket = new WebSocket("ws://localhost:8080");

const requestSpan = document.getElementById("request-result");
const statusSpan = document.getElementById("current-status");

socket.addEventListener("open", (event) => {
  //Request initial state
});

socket.addEventListener("message", (event) => {
  // Dispatch by message type and update right span
});
```
---

## Test Plans

## Project

## Testing

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

## Maintenance
