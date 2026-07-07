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
dispatch( iOPortDeposited, iOPortDeposited(X) ).
%====================================================================================
context(ctxcargoservice, "localhost",  "TCP", "5000").
 qactor( cargoservice, ctxcargoservice, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
  qactor( marker, ctxcargoservice, "it.unibo.marker.Marker").
 static(marker).
  qactor( sonar, ctxcargoservice, "it.unibo.sonar.Sonar").
 static(sonar).
