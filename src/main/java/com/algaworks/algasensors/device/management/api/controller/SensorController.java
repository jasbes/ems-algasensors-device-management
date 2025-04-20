package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClient;
import com.algaworks.algasensors.device.management.api.model.SensorDetailOutput;
import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorMonitoringOutput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {
    private final SensorRepository sensorRepository;
    private final SensorMonitoringClient sensorMonitoringClient;

    @GetMapping
    public Page<SensorOutput> search(@PageableDefault Pageable pageable) {
        return sensorRepository
                .findAll(pageable)
                .map(SensorOutput::fromEntity);
    }

    @GetMapping("{sensorId}")
    public SensorOutput getOne(@PathVariable TSID sensorId) {
        return sensorRepository.findById(new SensorId(sensorId))
                .map(SensorOutput::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("{sensorId}/detail")
    public SensorDetailOutput getOneWithDetail(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        SensorMonitoringOutput monitoringOutput = sensorMonitoringClient.getDetail(sensorId);
        SensorOutput sensorOutput = SensorOutput.fromEntity(sensor);

        return SensorDetailOutput.builder()
                .monitoring(monitoringOutput)
                .sensor(sensorOutput)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput input) {
        return SensorOutput.fromEntity(sensorRepository.saveAndFlush(input.toEntity()));
    }

    @PutMapping("/{sensorId}")
    public SensorOutput update(@PathVariable TSID sensorId,
                               @RequestBody SensorInput input) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        sensor.setName(input.getName());
        sensor.setLocation(input.getLocation());
        sensor.setIp(input.getIp());
        sensor.setModel(input.getModel());
        sensor.setProtocol(input.getProtocol());

        sensor = sensorRepository.save(sensor);

        return SensorOutput.fromEntity(sensor);
    }

    @DeleteMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensorRepository.delete(sensor);

        sensorMonitoringClient.disableMonitoring(sensorId);
    }

    @PutMapping("/{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setEnabled(true);
        sensorRepository.save(sensor);

        sensorMonitoringClient.enableMonitoring(sensorId);
    }

    @DeleteMapping("/{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setEnabled(false);
        sensorRepository.save(sensor);

        sensorMonitoringClient.disableMonitoring(sensorId);
    }
}
