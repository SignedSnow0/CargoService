%====================================================================================
% cargoservice description   
%====================================================================================
request( loadRequest, loadRequest(X) ).
reply( retryLater, retryLater(RetryMessage) ).  %%for loadRequest
reply( rejected, rejected(RejectedMessage) ).  %%for loadRequest
reply( accepted, accepted(SlotID) ).  %%for loadRequest
request( markContainer, markContainer(ID) ).
reply( markDone, markDone(msg) ).  %%for markContainer
event( serviceWorking, serviceWorking(X) ).
event( outOfService, outOfService(X) ).
event( iOPortDeposited, iOPortDeposited(X) ).
dispatch( sonardata, sonardata(Distance) ).
dispatch( blinkLed, blinkLed(Blink) ).
request( moverobot, moverobot(TARGETX,TARGETY,STEPTIME) ).
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
dispatch( setplanbuildelay, value(V) ).
dispatch( move, move(M) ). %MOVE = l|r|a|d|h mosse aril sincrone ok
dispatch( setrobotstate, setpos(X,Y,D) ). %set robot position to (X,Y) direction D=up|down|left|right
request( setdirection, dir(D) ). %set robot direction to D=up|down|left|right
reply( setdirectiondone, pos(PX,PY) ).  %%for setdirection
request( tuneAtHome, tuneAtHome(X) ). %reposition in home X don't care
reply( tuneDone, tuneDone(X) ).  %%for tuneAtHome
%====================================================================================
context(ctxcargoservice, "localhost",  "TCP", "5000").
context(ctxrobotsmart, "robotsmart26",  "TCP", "8020").
 qactor( robotsmart, ctxrobotsmart, "external").
  qactor( cargoservice, ctxcargoservice, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
  qactor( sonarwrapper, ctxcargoservice, "it.unibo.sonarwrapper.Sonarwrapper").
 static(sonarwrapper).
