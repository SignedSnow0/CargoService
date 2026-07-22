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
request( registerListener, register(X) ).
reply( registered, registered(OK) ).  %%for registerListener
dispatch( outOfServiceMsg, outOfService(X) ).
dispatch( serviceWorkingMsg, serviceWorking(X) ).
request( moverobot, moverobot(TARGETX,TARGETY,STEPTIME) ).
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
dispatch( setplanbuildelay, value(V) ).
dispatch( setrobotstate, setpos(X,Y,D) ).
request( tuneAtHome, tuneAtHome(X) ).
reply( tuneDone, tuneDone(X) ).  %%for tuneAtHome
%====================================================================================
context(ctxcargoservice, "localhost",  "TCP", "5000").
context(ctxrobotsmart, "robotsmart26",  "TCP", "8020").
 qactor( robotsmart, ctxrobotsmart, "external").
  qactor( cargoservice, ctxcargoservice, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
  qactor( sonarwrapper, ctxcargoservice, "it.unibo.sonarwrapper.Sonarwrapper").
 static(sonarwrapper).
  qactor( ioportadapter, ctxcargoservice, "it.unibo.ioportadapter.Ioportadapter").
 static(ioportadapter).
