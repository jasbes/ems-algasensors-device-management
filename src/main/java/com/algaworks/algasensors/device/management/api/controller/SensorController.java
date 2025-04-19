package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput input) {
        return SensorOutput.fromEntity(sensorRepository.saveAndFlush(input.toEntity()));
    }
}
