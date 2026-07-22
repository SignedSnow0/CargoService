package ioport;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsContext;
import it.unibo.kactor.MsgUtil;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.ConnectionFactory;

public class IOPortServer {
	public static void main(String[] args) throws UnknownHostException, IOException {
		new IOPortServer();
	}
	
	public IOPortServer() throws UnknownHostException, IOException {
		var app = Javalin.create(config -> {
			config.jetty.modifyWebSocketServletFactory(factory -> {
                factory.setIdleTimeout(Duration.ofMinutes(30));
            });
			
        	config.staticFiles.add(staticFiles -> {
				staticFiles.directory = "/page";
				staticFiles.location = Location.CLASSPATH;
		    });
		}).start(8080);

		app.get("/", ctx -> {
        	var inputStream = getClass().getResourceAsStream("/page/index.html");     
        	if (inputStream != null) {
        	    String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        	    ctx.html(content);
        	} else {
		        ctx.status(404).result("Page not found!");
		    }
        });
		
		app.ws("/", ws -> {
            ws.onConnect(ctx -> {
            	sessions.add(ctx);
            	System.out.println("Client connected");
            });
            
            ws.onClose(ctx -> {
            	sessions.remove(ctx);
            });
            
            ws.onError(ctx -> {
            	sessions.remove(ctx);
            });
            
            ws.onMessage(ctx -> {
            	var message = new ApplMessage(ctx.message());
            	if (!message.isRequest()) {
            		return;
            	}
            	
            	System.out.println("Received message: " + message.toString());
            	
            	var reply = cargoserviceConnection.request(message);
            	ctx.send(reply.toJsonString());
            });
        });

		CompletableFuture.runAsync(() -> {
		   try {
		        var regMsg = MsgUtil.buildRequest("ioport", "registerListener", "register(ioport)", "ioportadapter");
		        System.out.println("Sending registration request...");
		        
		        var regReply = ioportConnection.request(regMsg);
		        System.out.println("Registered connection: " + regReply);

		        while (true) {
		            System.out.println("Listening for event dispatches...");
		            
		            var msg = ioportConnection.receive();
		            System.out.println("Received message from cargoservice: " + msg.toString());
		            
		            broadcast(msg);
		        }
		    } catch (Exception e) {
		        System.err.println("Event loop exception: " + e.getMessage());
		        e.printStackTrace();
		    }
		});
		
		System.out.println("Connected to cargoservice");
	}
	
	private void broadcast(IApplMessage msg) {
		sessions.forEach(ctx -> {
			if (ctx.session.isOpen()) {
				ctx.send(msg.toJsonString());
			}
		});
	}
	
	private Interaction cargoserviceConnection = ConnectionFactory.createClientSupport(ProtocolType.tcp, "cargoservice", "5000");
	private Interaction ioportConnection = ConnectionFactory.createClientSupport(ProtocolType.tcp, "cargoservice", "5000");
	private List<WsContext> sessions = new ArrayList<WsContext>();
}