package ioport;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
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
            	System.out.println("Client connected");
            });
            
            ws.onMessage(ctx -> {
            	var message = new ApplMessage(ctx.message());
            	if (!message.isRequest()) {
            		return;
            	}
            	
            	System.out.println("Received message: " + message.toString());
            	var reply = cargoserviceConnection.request(message);
            });
        });

		cargoserviceConnection = ConnectionFactory.createClientSupport(ProtocolType.tcp, "localhost", "5000");
		System.out.println("Connected to cargoservice");
	}
	
	private Interaction cargoserviceConnection; 
}
