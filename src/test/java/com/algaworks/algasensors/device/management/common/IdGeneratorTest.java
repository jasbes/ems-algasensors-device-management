package com.algaworks.algasensors.device.management.common;

import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class IdGeneratorTest {
    @Test
    void shouldGenerateTSID() {
        TSID tsid = IdGenerator.generateTSID();
        assertThat(tsid.getInstant())
                .isCloseTo(Instant.now(), within(1, ChronoUnit.MINUTES));
    }
}