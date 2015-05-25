package org.example.websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.example.model.Device;
    
@ServerEndpoint("/actions")
public class DeviceWebSocketServer {

	private final Logger LOG = Logger.getLogger(DeviceWebSocketServer.class.getName()); 

	@Inject
	    private DeviceSessionHandler sessionHandler;
	 
    @OnOpen
        public void open(Session session) {
    	  LOG.info(" --------- OPEN "+session.getId()+"--------- ");
    	  sessionHandler.addSession(session);
    }
    
    

    @OnClose
    public void close(Session session) {
  	  LOG.info(" --------- CLOSE "+session.getId()+"--------- ");
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        LOG.log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
    	 LOG.info(" --------- handle Message "+message+","+session.getId()+" --------- ");
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            if ("add".equals(jsonMessage.getString("action"))) {
                Device device = new Device();
                device.setName(jsonMessage.getString("name"));
                device.setDescription(jsonMessage.getString("description"));
                device.setType(jsonMessage.getString("type"));
                device.setStatus("Off");
                sessionHandler.addDevice(device);
            }

            if ("remove".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.removeDevice(id);
            }

            if ("toggle".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.toggleDevice(id);
            }
        }
    }
}    