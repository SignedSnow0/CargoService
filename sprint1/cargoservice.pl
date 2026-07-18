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
%====================================================================================
context(ctxcargoservice, "localhost",  "TCP", "5000").
 qactor( cargoservice, ctxcargoservice, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
  qactor( sonarwrapper, ctxcargoservice, "it.unibo.sonarwrapper.Sonarwrapper").
 static(sonarwrapper).
