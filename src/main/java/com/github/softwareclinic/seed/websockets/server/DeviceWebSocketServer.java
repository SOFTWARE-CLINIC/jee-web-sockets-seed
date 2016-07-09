package com.github.softwareclinic.seed.websockets.server;

import com.github.softwareclinic.seed.websockets.domain.Device;
import com.github.softwareclinic.seed.websockets.server.DeviceSessionHandler.Action;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.softwareclinic.seed.websockets.domain.Device.Status.Off;

@ApplicationScoped
@ServerEndpoint("/actions")
public class DeviceWebSocketServer {

    @Inject
    private DeviceSessionHandler sessionHandler;

    @OnOpen
    public void open(Session session) {
        sessionHandler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            Action action = getAction(jsonMessage);

            if (Action.add == action) {
                Device device = new Device();
                device.setName(jsonMessage.getString("name"));
                device.setDescription(jsonMessage.getString("description"));
                device.setType(jsonMessage.getString("type"));
                device.setStatus(Off);
                sessionHandler.addDevice(device);
            }

            if (Action.remove == action) {
                int id = jsonMessage.getInt("id");
                sessionHandler.removeDevice(id);
            }

            if (Action.toggle == action) {
                int id = jsonMessage.getInt("id");
                sessionHandler.toggleDevice(id);
            }
        }
    }

    private Action getAction(JsonObject request) {
        return Action.valueOf(request.getString("action"));
    }

}
