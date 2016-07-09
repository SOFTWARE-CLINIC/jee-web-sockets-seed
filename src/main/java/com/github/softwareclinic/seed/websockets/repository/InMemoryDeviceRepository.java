package com.github.softwareclinic.seed.websockets.repository;

import com.github.softwareclinic.seed.websockets.domain.Device;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class InMemoryDeviceRepository implements DeviceRepository {

    private final AtomicInteger sequence;

    private final Set<Device> devices;

    public InMemoryDeviceRepository() {
        sequence = new AtomicInteger(1);
        devices = new HashSet<>();
    }

    @Override
    public Device create(Device device) {
        device.setId(sequence.getAndIncrement());
        devices.add(device);
        return device;
    }

    @Override
    public List<Device> findAll() {
        return new ArrayList<>(devices);
    }

    @Override
    public Device findOne(int id) {
        for (Device device : devices) {
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }

    @Override
    public void delete(int id) {
        Device device = findOne(id);
        if (device != null) {
            devices.remove(device);
        }
    }

}
