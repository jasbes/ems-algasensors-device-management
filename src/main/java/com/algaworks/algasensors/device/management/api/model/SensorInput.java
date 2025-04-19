package com.algaworks.algasensors.device.management.api.model;

import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import lombok.Data;

@Data
public class SensorInput {
    private String name;
    private String ip;
    private String location;
    private String protocol;
    private String model;

    public Sensor toEntity() {
        return Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID()))
                .name(name)
                .ip(ip)
                .location(location)
                .protocol(protocol)
                .model(model)
                .enabled(false)
                .build();
    }
}
