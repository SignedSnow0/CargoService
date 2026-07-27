%====================================================================================
% cargoservice description   
%====================================================================================
%====================================================================================
context(ctxcargoservice, "localhost",  "TCP", "5000").
 qactor( cargoservice, ctxcargoservice, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
