package com.github.softwareclinic.seed.websockets.server;

import com.github.softwareclinic.seed.websockets.domain.Device;
import com.github.softwareclinic.seed.websockets.repository.DeviceRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.softwareclinic.seed.websockets.domain.Device.Status.Off;
import static com.github.softwareclinic.seed.websockets.domain.Device.Status.On;

@ApplicationScoped
public class DeviceSessionHandler {

    @Inject
    private DeviceRepository deviceRepository;

    private final Set<Session> sessions = new HashSet<>();

    public void addSession(Session session) {
        sessions.add(session);
        for (Device device : deviceRepository.findAll()) {
            JsonObject addMessage = createAddMessage(device);
            sendToSession(session, addMessage);
        }
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public void addDevice(Device device) {
        deviceRepository.create(device);
        JsonObject addMessage = createAddMessage(device);
        sendToAllConnectedSessions(addMessage);
    }

    public void removeDevice(int id) {
        Device device = deviceRepository.findOne(id);
        if (device != null) {
            deviceRepository.delete(device.getId());
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", Action.remove.name())
                    .add("id", id)
                    .build();
            sendToAllConnectedSessions(removeMessage);
        }
    }

    public void toggleDevice(int id) {
        JsonProvider provider = JsonProvider.provider();
        Device device = deviceRepository.findOne(id);
        if (device != null) {
            if (Device.Status.On == device.getStatus()) {
                device.setStatus(Off);
            } else if (Device.Status.Off == device.getStatus()) {
                device.setStatus(On);
            } else {
                throw new IllegalStateException("Unknown device status: " + device.getStatus());
            }
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", Action.toggle.name())
                    .add("id", device.getId())
                    .add("status", device.getStatus().name())
                    .build();
            sendToAllConnectedSessions(updateDevMessage);
        }
    }

    private JsonObject createAddMessage(Device device) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", Action.add.name())
                .add("id", device.getId())
                .add("name", device.getName())
                .add("type", device.getType())
                .add("status", device.getStatus().name())
                .add("description", device.getDescription())
                .build();
        return addMessage;
    }

    private void sendToAllConnectedSessions(JsonObject message) {
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public enum Action {
        add, remove, toggle
    }

}
