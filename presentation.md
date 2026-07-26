---
marp: true
title: ISS_Tema_Finale_2026
paginate: true
theme: gaia
class: lead
---

# TF2026 presentation

Made by Claudio Marchini
and Cesare Tomasi

--- 

# Sprint 0: brief summary

The goal of this sprint was to analize the requirements given to us and to translate them from natural language to a machine-readable representation. The following tasks were completed:
* the _hold_, _slot_ and _position_ entities were identified and were given matching Java interfaces (**ISlot**, **IHold**, **IPosition**) 
* the entire system was identified as _distributed_ and _heterogeneous_, which meant implementing a distributed model based on services
* the qak language was chosen to implement the services needed (**cargoservice**, **cargorobot**) for its convenience and expressivity

---


# Sprint 0: brief summary

* the load request interaction was modeled as _request/reply_
* the already developed **RobotSmart26** was chosen as the cargorobot's implementation (thanks to it being a service and developed in qak, its pathfinding algorithm and its grid based movement)
* the **IOPort** component (used by the client to interact with the system) was also defined as a web GUI


---

# Sprint 1: brief summary

This sprint was built on top of the requirement analysis of **sprint 0** and aimed to build several cardinal components of the system, in particular:
* The main **cargoservice** actor
* The **sonar** subsystem
* The **IOPort** and **pushbutton** interfaces
* Concrete Java implementations and tests of the following interfaces: **ISlot**, **IHold**, **IPosition** 

---

# Sprint 1: brief summary

In more detail:

* the sonar hw module was connected to a _Raspberry Pi Pico W_ and a python script for communication with both it and the cargoservice actor via _MQTT_ (chosen for modularity and lightweightness) was developed
* the message format used was defined following the protobook documentation and the actor **sonarwrapper** was introduced to to have a single point where the distance is parsed (making it more robust and easily adjustable in the future) 

---

# Sprint 1: brief summary

* the IOPort web GUI was finalized (**WebSocket** was chosen over a HTTP request-response style interaction because the component has to mantain the current status of the system at any moment)
* the current version of the _cargoservice_ actor was now capable of communicating with both working subsystems
* a _Dockerfile_ to easily deploy the various components on different nodes was also developed

---

# Sprint 2: brief summary

This sprint was built on top of the work of **sprint 1** and aimed to integrate the last component of the system: the _cargorobot_.

In more detail:

* the *cargoservice* actor was updated and it is now capable to instruct the **RobotSmart26** (taken as is) using requests and dispatches (via TCP)
* the _aril_ language (from protobook documentation) was used to model the basic commands of the robot's movement

---

# Sprint 2: brief summary

* the system is now entirely functional and can be tested by the user via a virtual environment containing a simulation of the robot and the hold (available as a **web page**)
* the Dockerfile was updated to handle both the cargorobot and the virtual environment  

---

# Final system architecture
