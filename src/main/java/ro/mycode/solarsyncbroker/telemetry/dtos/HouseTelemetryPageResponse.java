package ro.mycode.solarsyncbroker.telemetry.dtos;

import java.time.LocalDateTime;

public record HouseTelemetryPageResponse(
        Long id,
        Long houseId,
        LocalDateTime localDateTime,
        Double pvPowerKw,
        Double loadPowerKw,
        Double gridPowerKw,
        Double batterySocPercent,
        Double batteryPowerKw
) {
}
