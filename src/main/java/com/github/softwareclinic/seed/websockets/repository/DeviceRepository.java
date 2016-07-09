package com.github.softwareclinic.seed.websockets.repository;

import com.github.softwareclinic.seed.websockets.domain.Device;

import java.util.List;

public interface DeviceRepository {

    Device create(Device device);

    List<Device> findAll();

    Device findOne(int id);

    void delete(int id);

}
